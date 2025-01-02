/**
 * Interface for payment gateway operations
 */
public interface PaymentGateway {
    PaymentTransaction createTransaction(PaymentDetails details) 
        throws PaymentGatewayException;
        
    PaymentResult processTransaction(PaymentTransaction transaction) 
        throws PaymentGatewayException;
        
    PaymentStatus checkStatus(String transactionId) 
        throws PaymentGatewayException;
        
    PaymentRefund refundTransaction(String transactionId, BigDecimal amount) 
        throws PaymentGatewayException;
}

/**
 * Payment transaction details
 */
public class PaymentTransaction {
    private final String transactionId;
    private final PaymentDetails details;
    private final LocalDateTime timestamp;
    private PaymentStatus status;
    private Map<String, String> metadata;
    
    public PaymentTransaction(String transactionId, PaymentDetails details) {
        this.transactionId = transactionId;
        this.details = details;
        this.timestamp = LocalDateTime.now();
        this.status = PaymentStatus.PENDING;
        this.metadata = new HashMap<>();
    }
    
    // Getters, setters, and utility methods
}

/**
 * Payment processing result
 */
public class PaymentResult {
    private final String transactionId;
    private final boolean successful;
    private final PaymentStatus status;
    private final String errorCode;
    private final String errorMessage;
    private final Map<String, String> metadata;
    
    private PaymentResult(Builder builder) {
        this.transactionId = builder.transactionId;
        this.successful = builder.successful;
        this.status = builder.status;
        this.errorCode = builder.errorCode;
        this.errorMessage = builder.errorMessage;
        this.metadata = new HashMap<>(builder.metadata);
    }
    
    public static Builder success(String transactionId) {
        return new Builder(transactionId, true);
    }
    
    public static Builder failure(String transactionId) {
        return new Builder(transactionId, false);
    }
    
    // Builder pattern implementation
    public static class Builder {
        // Builder implementation
    }
} 