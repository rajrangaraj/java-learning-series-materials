/**
 * Email-based implementation of NotificationService
 */
public class EmailNotificationService implements NotificationService {
    private static final Logger logger = LoggerFactory.getLogger(EmailNotificationService.class);
    
    private final EmailClient emailClient;
    private final TemplateEngine templateEngine;
    private final MessageQueue messageQueue;
    private final MetricsCollector metrics;
    
    public EmailNotificationService(EmailClient emailClient, 
            TemplateEngine templateEngine,
            MessageQueue messageQueue,
            MetricsCollector metrics) {
        this.emailClient = emailClient;
        this.templateEngine = templateEngine;
        this.messageQueue = messageQueue;
        this.metrics = metrics;
        
        initializeMessageHandlers();
    }
    
    @Override
    public void sendOrderConfirmation(NotificationRequest request) 
            throws NotificationException {
        try {
            metrics.startTimer("email.order_confirmation");
            
            Template template = templateEngine.getTemplate("order_confirmation");
            String content = template.render(createTemplateContext(request));
            
            EmailMessage email = EmailMessage.builder()
                .to(request.getCustomerEmail())
                .subject("Order Confirmation - " + request.getOrderId())
                .content(content)
                .build();
                
            // Queue email for async sending
            messageQueue.publish("notifications.email", 
                new Message("ORDER_CONFIRMATION", email));
                
            metrics.incrementCounter("email.queued");
            
        } catch (TemplateException e) {
            handleTemplateError(request, e);
        } catch (MessageQueueException e) {
            handleQueueError(request, e);
        } finally {
            metrics.stopTimer("email.order_confirmation");
        }
    }
    
    private void initializeMessageHandlers() {
        messageQueue.subscribe("notifications.email", message -> {
            try {
                EmailMessage email = (EmailMessage) message.getPayload();
                emailClient.sendEmail(email);
                
                messageQueue.acknowledge(message.getId());
                metrics.incrementCounter("email.sent");
                
            } catch (EmailException e) {
                handleEmailError(message, e);
            } catch (Exception e) {
                handleUnexpectedError(message, e);
            }
        });
    }
    
    private Map<String, Object> createTemplateContext(NotificationRequest request) {
        Map<String, Object> context = new HashMap<>();
        context.put("orderId", request.getOrderId());
        context.put("customerName", request.getCustomerName());
        context.put("orderItems", request.getOrderItems());
        context.put("total", request.getOrderTotal());
        context.put("shippingAddress", request.getShippingAddress());
        context.put("trackingNumber", request.getTrackingNumber());
        return context;
    }
    
    private void handleTemplateError(NotificationRequest request, TemplateException e) 
            throws NotificationException {
        logger.error("Template error for order {}: {}", 
            request.getOrderId(), e.getMessage(), e);
        metrics.incrementCounter("email.template.error");
        
        throw new NotificationException(
            "Failed to generate email content", 
            request.getOrderId(),
            null,
            NotificationType.ORDER_CONFIRMATION
        );
    }
    
    private void handleQueueError(NotificationRequest request, MessageQueueException e) 
            throws NotificationException {
        logger.error("Queue error for order {}: {}", 
            request.getOrderId(), e.getMessage(), e);
        metrics.incrementCounter("email.queue.error");
        
        throw new NotificationException(
            "Failed to queue notification", 
            request.getOrderId(),
            null,
            NotificationType.ORDER_CONFIRMATION
        );
    }
    
    private void handleEmailError(Message message, EmailException e) {
        logger.error("Email sending failed: {}", e.getMessage(), e);
        metrics.incrementCounter("email.send.error");
        
        if (isRetryableError(e)) {
            message.incrementRetryCount();
            if (message.getRetryCount() < 3) {
                requeueWithDelay(message);
            } else {
                moveToDeadLetter(message, "Max retries exceeded");
            }
        } else {
            moveToDeadLetter(message, e.getMessage());
        }
    }
    
    private boolean isRetryableError(EmailException e) {
        return e instanceof TemporaryFailureException ||
               e instanceof RateLimitException;
    }
    
    private void requeueWithDelay(Message message) {
        try {
            long delay = calculateBackoff(message.getRetryCount());
            messageQueue.publish("notifications.email.retry", 
                message.withDelay(delay));
        } catch (MessageQueueException e) {
            logger.error("Failed to requeue message", e);
            moveToDeadLetter(message, "Requeue failed");
        }
    }
    
    private void moveToDeadLetter(Message message, String reason) {
        try {
            messageQueue.deadLetter(message.getId(), reason);
            metrics.incrementCounter("email.dead_letter");
        } catch (MessageQueueException e) {
            logger.error("Failed to move message to DLQ", e);
        }
    }
    
    private long calculateBackoff(int retryCount) {
        return (long) Math.pow(2, retryCount) * 1000; // Exponential backoff
    }
} 