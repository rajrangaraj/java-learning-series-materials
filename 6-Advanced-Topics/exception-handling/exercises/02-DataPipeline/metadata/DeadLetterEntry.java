/**
 * Represents an entry in the dead letter queue
 */
public class DeadLetterEntry {
    private final DataRecord record;
    private final ErrorRecord error;
    private final LocalDateTime timestamp;
    private int retryCount;
    private LocalDateTime lastRetryAttempt;
    private Map<String, Object> metadata;
    
    public DeadLetterEntry(DataRecord record, ErrorRecord error,
                          LocalDateTime timestamp) {
        this.record = record;
        this.error = error;
        this.timestamp = timestamp;
        this.retryCount = 0;
        this.metadata = new HashMap<>();
    }
    
    public DataRecord getRecord() {
        return record;
    }
    
    public ErrorRecord getError() {
        return error;
    }
    
    public LocalDateTime getTimestamp() {
        return timestamp;
    }
    
    public int getRetryCount() {
        return retryCount;
    }
    
    public void setRetryCount(int retryCount) {
        this.retryCount = retryCount;
    }
    
    public LocalDateTime getLastRetryAttempt() {
        return lastRetryAttempt;
    }
    
    public void setLastRetryAttempt(LocalDateTime lastRetryAttempt) {
        this.lastRetryAttempt = lastRetryAttempt;
    }
    
    public Map<String, Object> getMetadata() {
        return Collections.unmodifiableMap(metadata);
    }
    
    public void addMetadata(String key, Object value) {
        metadata.put(key, value);
    }
    
    public Duration getAge() {
        return Duration.between(timestamp, LocalDateTime.now());
    }
    
    public boolean isRetryable() {
        return retryCount < 3 && 
               error.getType() != ErrorType.VALIDATION_ERROR;
    }
    
    @Override
    public String toString() {
        return String.format(
            "DeadLetterEntry{recordId=%s, errorType=%s, " +
            "retryCount=%d, age=%s}",
            record.getId(), error.getType(), retryCount,
            formatDuration(getAge()));
    }
    
    private String formatDuration(Duration duration) {
        return String.format("%02d:%02d:%02d",
            duration.toHours(),
            duration.toMinutesPart(),
            duration.toSecondsPart());
    }
} 