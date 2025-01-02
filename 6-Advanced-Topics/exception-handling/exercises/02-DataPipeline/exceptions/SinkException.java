/**
 * Exception thrown when writing to a data sink fails
 */
public class SinkException extends DataProcessingException {
    private final String sink;
    private final String destination;
    private final boolean retryable;
    
    public SinkException(String message) {
        super(message);
        this.sink = "unknown";
        this.destination = "unknown";
        this.retryable = false;
    }
    
    public SinkException(String message, String sink, 
            String destination, boolean retryable) {
        super(message);
        this.sink = sink;
        this.destination = destination;
        this.retryable = retryable;
    }
    
    public SinkException(String message, Throwable cause) {
        super(message, cause);
        this.sink = "unknown";
        this.destination = "unknown";
        this.retryable = false;
    }
    
    public SinkException(String message, Throwable cause,
            String sink, String destination, boolean retryable) {
        super(message, cause);
        this.sink = sink;
        this.destination = destination;
        this.retryable = retryable;
    }
    
    public String getSink() {
        return sink;
    }
    
    public String getDestination() {
        return destination;
    }
    
    public boolean isRetryable() {
        return retryable;
    }
    
    @Override
    public String getDetails() {
        return String.format("Sink: %s, Destination: %s, Retryable: %s",
            sink, destination, retryable);
    }
} 