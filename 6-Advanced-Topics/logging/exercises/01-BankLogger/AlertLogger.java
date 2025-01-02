/**
 * Specialized logger for security and system alerts
 */
public class AlertLogger implements AutoCloseable {
    private static final Logger logger = LoggerFactory.getLogger(AlertLogger.class);
    private final String bankId;
    private final AlertNotifier alertNotifier;
    private final BlockingQueue<Alert> alertQueue;
    private final ExecutorService executor;
    private final AtomicInteger alertCount;
    private volatile boolean running;
    
    public AlertLogger(String bankId) {
        this.bankId = bankId;
        this.alertNotifier = new AlertNotifier();
        this.alertQueue = new LinkedBlockingQueue<>(1000);
        this.executor = Executors.newFixedThreadPool(2);
        this.alertCount = new AtomicInteger();
        this.running = true;
        
        initialize();
    }
    
    private void initialize() {
        // Start alert processing threads
        executor.submit(this::processAlertQueue);
        executor.submit(this::monitorAlertRate);
    }
    
    public void triggerSecurityAlert(SecurityEvent event) {
        Alert alert = new Alert(
            AlertType.SECURITY,
            event.getSeverity(),
            "Security event: " + event.getType(),
            event
        );
        
        queueAlert(alert);
    }
    
    public void triggerSystemAlert(String message, Exception e) {
        Alert alert = new Alert(
            AlertType.SYSTEM,
            Severity.HIGH,
            "System error: " + message,
            Map.of(
                "exception", e.getClass().getName(),
                "message", e.getMessage(),
                "stackTrace", getStackTraceAsString(e)
            )
        );
        
        queueAlert(alert);
    }
    
    public void notifySecurityTeam(SecurityEvent event) {
        Alert alert = new Alert(
            AlertType.SECURITY_NOTIFICATION,
            event.getSeverity(),
            "Security team notification: " + event.getType(),
            event
        );
        
        queueAlert(alert);
    }
    
    public void notifySystemAdmins(SystemEvent event) {
        Alert alert = new Alert(
            AlertType.SYSTEM_NOTIFICATION,
            Severity.MEDIUM,
            "System admin notification: " + event.getOperation(),
            event
        );
        
        queueAlert(alert);
    }
    
    private void queueAlert(Alert alert) {
        try {
            if (!alertQueue.offer(alert, 5, TimeUnit.SECONDS)) {
                logger.error("Failed to queue alert: queue full");
            } else {
                alertCount.incrementAndGet();
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            logger.error("Interrupted while queueing alert", e);
        }
    }
    
    private void processAlertQueue() {
        while (running || !alertQueue.isEmpty()) {
            try {
                Alert alert = alertQueue.poll(1, TimeUnit.SECONDS);
                if (alert != null) {
                    processAlert(alert);
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                logger.error("Alert processing interrupted", e);
                break;
            } catch (Exception e) {
                logger.error("Error processing alert", e);
            }
        }
    }
    
    private void processAlert(Alert alert) {
        try {
            // Log alert
            switch (alert.getType()) {
                case SECURITY:
                    logger.error(MarkerFactory.getMarker("SECURITY_ALERT"),
                        "Security Alert: {}", alert.getMessage());
                    break;
                case SYSTEM:
                    logger.error(MarkerFactory.getMarker("SYSTEM_ALERT"),
                        "System Alert: {}", alert.getMessage());
                    break;
                case SECURITY_NOTIFICATION:
                case SYSTEM_NOTIFICATION:
                    logger.warn("Notification: {}", alert.getMessage());
                    break;
            }
            
            // Send notifications
            alertNotifier.sendAlert(alert);
            
        } catch (Exception e) {
            logger.error("Failed to process alert", e);
        }
    }
    
    private void monitorAlertRate() {
        while (running) {
            try {
                Thread.sleep(60000); // Check every minute
                
                int count = alertCount.getAndSet(0);
                if (count > 100) { // More than 100 alerts per minute
                    logger.error("High alert rate detected: {} alerts/minute", count);
                    
                    // Notify about high alert rate
                    Alert alert = new Alert(
                        AlertType.SYSTEM,
                        Severity.HIGH,
                        "High alert rate detected",
                        Map.of("alertsPerMinute", count)
                    );
                    alertNotifier.sendAlert(alert);
                }
                
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
    }
    
    private String getStackTraceAsString(Exception e) {
        StringWriter sw = new StringWriter();
        e.printStackTrace(new PrintWriter(sw));
        return sw.toString();
    }
    
    @Override
    public void close() {
        running = false;
        executor.shutdown();
        try {
            if (!executor.awaitTermination(1, TimeUnit.MINUTES)) {
                executor.shutdownNow();
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            executor.shutdownNow();
        }
    }
} 