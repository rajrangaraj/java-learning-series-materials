/**
 * Exception thrown when a data source is unavailable or fails
 */
public class SourceUnavailableException extends DataProcessingException {
    private final String source;
    private final String location;
    
    public SourceUnavailableException(String message) {
        super(message);
        this.source = "unknown";
        this.location = "unknown";
    }
    
    public SourceUnavailableException(String message, String source, String location) {
        super(message);
        this.source = source;
        this.location = location;
    }
    
    public SourceUnavailableException(String message, Throwable cause) {
        super(message, cause);
        this.source = "unknown";
        this.location = "unknown";
    }
    
    public SourceUnavailableException(String message, Throwable cause, 
            String source, String location) {
        super(message, cause);
        this.source = source;
        this.location = location;
    }
    
    public String getSource() {
        return source;
    }
    
    public String getLocation() {
        return location;
    }
    
    @Override
    public String getDetails() {
        return String.format("Source: %s, Location: %s", source, location);
    }
} 