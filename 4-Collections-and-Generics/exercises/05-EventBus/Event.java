/**
 * Base class for all events
 */
public abstract class Event {
    private final long timestamp;
    private final String source;
    
    protected Event(String source) {
        this.timestamp = System.currentTimeMillis();
        this.source = source;
    }
    
    public long getTimestamp() {
        return timestamp;
    }
    
    public String getSource() {
        return source;
    }
} 