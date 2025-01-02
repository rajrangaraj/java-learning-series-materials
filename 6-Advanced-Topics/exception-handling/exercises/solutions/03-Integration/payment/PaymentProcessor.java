/**
 * Handles payment processing with retry and circuit breaker patterns
 */
public class PaymentProcessor {
    private static final Logger logger = LoggerFactory.getLogger(PaymentProcessor.class);
    
    private final PaymentGateway paymentGateway;
    private final PaymentValidator validator;
    private final MetricsRegistry metrics;
    private final RetryPolicy<PaymentResult> retryPolicy;
    
    public PaymentProcessor(PaymentGateway paymentGateway, 
                          MetricsRegistry metrics) {
        this.paymentGateway = paymentGateway;
        this.validator = new PaymentValidator();
        this.metrics = metrics;
        
        this.retryPolicy = RetryPolicy.<PaymentResult>builder()
            .handle(PaymentGatewayTimeoutException.class)
            .withBackoff(Duration.ofMillis(100), Duration.ofSeconds(1))
            .withMaxRetries(3)
            .onRetry(this::handleRetry)
            .build();
    }
    
    public PaymentResult processPayment(PaymentDetails details) 
            throws PaymentException {
        Timer.Sample timer = Timer.start(metrics.getRegistry());
        
        try {
            // Validate payment details
            ValidationResult validation = validator.validate(details);
            if (!validation.isValid()) {
                throw new PaymentValidationException(
                    "Invalid payment details", 
                    validation.getErrors()
                );
            }
            
            // Process payment with retry
            return retryPolicy.execute(() -> {
                try {
                    PaymentTransaction tx = paymentGateway.createTransaction(details);
                    PaymentResult result = paymentGateway.processTransaction(tx);
                    
                    if (result.isSuccessful()) {
                        recordSuccess(timer);
                    } else {
                        recordFailure(result.getErrorCode());
                    }
                    
                    return result;
                    
                } catch (PaymentGatewayException e) {
                    handleGatewayError(e);
                    throw e;
                }
            });
            
        } catch (PaymentValidationException e) {
            metrics.incrementCounter("payment.validation.error");
            throw e;
            
        } catch (PaymentGatewayException e) {
            metrics.incrementCounter("payment.gateway.error");
            throw new PaymentServiceException(
                "Payment gateway error", e);
            
        } catch (Exception e) {
            metrics.incrementCounter("payment.unexpected.error");
            throw new PaymentServiceException(
                "Unexpected payment error", e);
        }
    }
    
    private void handleGatewayError(PaymentGatewayException e) {
        if (e instanceof PaymentGatewayTimeoutException) {
            metrics.incrementCounter("payment.gateway.timeout");
        } else if (e instanceof PaymentGatewayUnavailableException) {
            metrics.incrementCounter("payment.gateway.unavailable");
        }
        logger.error("Payment gateway error: {}", e.getMessage(), e);
    }
    
    private void handleRetry(RetryEvent<PaymentResult> event) {
        logger.warn("Retrying payment: {} (Attempt: {})", 
            event.getValue().getTransactionId(), 
            event.getAttemptCount());
        metrics.incrementCounter("payment.retry");
    }
    
    private void recordSuccess(Timer.Sample timer) {
        timer.stop(metrics.timer("payment.processing.time"));
        metrics.incrementCounter("payment.success");
    }
    
    private void recordFailure(String errorCode) {
        metrics.incrementCounter("payment.failure." + errorCode);
    }
} 