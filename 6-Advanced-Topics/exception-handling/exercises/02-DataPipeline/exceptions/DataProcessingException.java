/**
 * Base exception class for all data processing exceptions
 */
public abstract class DataProcessingException extends RuntimeException {
    private final LocalDateTime timestamp;
    private final String correlationId;
    
    protected DataProcessingException(String message) {
        super(message);
        this.timestamp = LocalDateTime.now();
        this.correlationId = generateCorrelationId();
    }
    
    protected DataProcessingException(String message, Throwable cause) {
        super(message, cause);
        this.timestamp = LocalDateTime.now();
        this.correlationId = generateCorrelationId();
    }
    
    public LocalDateTime getTimestamp() {
        return timestamp;
    }
    
    public String getCorrelationId() {
        return correlationId;
    }
    
    public abstract String getDetails();
    
    @Override
    public String toString() {
        return String.format("%s[%s] %s - %s\nDetails: %s",
            getClass().getSimpleName(),
            correlationId,
            timestamp,
            getMessage(),
            getDetails());
    }
    
    private String generateCorrelationId() {
        return UUID.randomUUID().toString();
    }
} 