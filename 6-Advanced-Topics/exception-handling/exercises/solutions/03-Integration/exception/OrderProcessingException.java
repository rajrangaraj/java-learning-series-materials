/**
 * Base exception for order processing
 */
public abstract class OrderProcessingException extends RuntimeException {
    private final String orderId;
    private final String correlationId;
    private final LocalDateTime timestamp;
    private final Map<String, String> metadata;
    
    protected OrderProcessingException(String message, String orderId) {
        super(message);
        this.orderId = orderId;
        this.correlationId = generateCorrelationId();
        this.timestamp = LocalDateTime.now();
        this.metadata = new HashMap<>();
    }
    
    // Getters and utility methods
}

/**
 * Payment-related exceptions
 */
public class PaymentException extends OrderProcessingException {
    private final String transactionId;
    private final PaymentErrorCode errorCode;
    
    public PaymentException(String message, String orderId, 
            String transactionId, PaymentErrorCode errorCode) {
        super(message, orderId);
        this.transactionId = transactionId;
        this.errorCode = errorCode;
    }
}

/**
 * Inventory-related exceptions
 */
public class InventoryException extends OrderProcessingException {
    private final List<String> unavailableItems;
    private final String reservationId;
    
    public InventoryException(String message, String orderId, 
            List<String> unavailableItems) {
        super(message, orderId);
        this.unavailableItems = unavailableItems;
        this.reservationId = null;
    }
}

/**
 * Shipping-related exceptions
 */
public class ShippingException extends OrderProcessingException {
    private final String shipmentId;
    private final ShippingErrorCode errorCode;
    
    public ShippingException(String message, String orderId, 
            String shipmentId, ShippingErrorCode errorCode) {
        super(message, orderId);
        this.shipmentId = shipmentId;
        this.errorCode = errorCode;
    }
}

/**
 * Notification-related exceptions
 */
public class NotificationException extends OrderProcessingException {
    private final String notificationId;
    private final NotificationType type;
    
    public NotificationException(String message, String orderId, 
            String notificationId, NotificationType type) {
        super(message, orderId);
        this.notificationId = notificationId;
        this.type = type;
    }
}

/**
 * Validation-related exceptions
 */
public class ValidationException extends OrderProcessingException {
    private final List<ValidationError> errors;
    
    public ValidationException(String message, String orderId, 
            List<ValidationError> errors) {
        super(message, orderId);
        this.errors = errors;
    }
}

/**
 * Utility interfaces
 */
public interface ErrorHandler {
    void handleError(OrderProcessingException error);
    void handleUnexpectedError(String orderId, Throwable error);
}

public interface RetryPolicy<T> {
    T execute(Supplier<T> operation) throws Exception;
    void onRetry(RetryEventListener listener);
}

public interface CircuitBreaker {
    <T> T execute(Supplier<T> operation) throws Exception;
    CircuitBreakerStatus getStatus();
    void reset();
}

public interface MetricsCollector {
    void incrementCounter(String name);
    void recordValue(String name, long value);
    void startTimer(String name);
    void stopTimer(String name);
} 