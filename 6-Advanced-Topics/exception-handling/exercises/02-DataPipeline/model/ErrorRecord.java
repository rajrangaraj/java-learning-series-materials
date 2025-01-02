/**
 * Represents an error that occurred during processing
 */
public class ErrorRecord {
    private final String id;
    private final ErrorType type;
    private final String message;
    private final LocalDateTime timestamp;
    private final DataRecord record;
    private final Map<String, Object> context;
    private final String stackTrace;
    
    public ErrorRecord(ErrorType type, String message, 
            LocalDateTime timestamp) {
        this(type, message, timestamp, null);
    }
    
    public ErrorRecord(ErrorType type, String message,
            LocalDateTime timestamp, DataRecord record) {
        this.id = UUID.randomUUID().toString();
        this.type = type;
        this.message = message;
        this.timestamp = timestamp;
        this.record = record;
        this.context = new HashMap<>();
        this.stackTrace = Thread.currentThread().getStackTrace().toString();
    }
    
    public String getId() {
        return id;
    }
    
    public ErrorType getType() {
        return type;
    }
    
    public String getMessage() {
        return message;
    }
    
    public LocalDateTime getTimestamp() {
        return timestamp;
    }
    
    public Optional<DataRecord> getRecord() {
        return Optional.ofNullable(record);
    }
    
    public Map<String, Object> getContext() {
        return Collections.unmodifiableMap(context);
    }
    
    public void addContext(String key, Object value) {
        context.put(key, value);
    }
    
    public String getStackTrace() {
        return stackTrace;
    }
} 