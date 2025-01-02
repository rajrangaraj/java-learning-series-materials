/**
 * Metadata for data sinks
 */
public class SinkMetadata {
    private final String type;
    private final String destination;
    private final long recordsWritten;
    private final int batchSize;
    private final int queueSize;
    private final Map<String, Object> properties;
    private final LocalDateTime startTime;
    private final AtomicLong totalBytes;
    private final AtomicLong failedWrites;
    
    public SinkMetadata(String type, String destination,
                       long recordsWritten, int batchSize, int queueSize) {
        this.type = type;
        this.destination = destination;
        this.recordsWritten = recordsWritten;
        this.batchSize = batchSize;
        this.queueSize = queueSize;
        this.properties = new HashMap<>();
        this.startTime = LocalDateTime.now();
        this.totalBytes = new AtomicLong();
        this.failedWrites = new AtomicLong();
    }
    
    public String getType() {
        return type;
    }
    
    public String getDestination() {
        return destination;
    }
    
    public long getRecordsWritten() {
        return recordsWritten;
    }
    
    public int getBatchSize() {
        return batchSize;
    }
    
    public int getQueueSize() {
        return queueSize;
    }
    
    public Map<String, Object> getProperties() {
        return Collections.unmodifiableMap(properties);
    }
    
    public void addProperty(String key, Object value) {
        properties.put(key, value);
    }
    
    public void recordBytesWritten(long bytes) {
        totalBytes.addAndGet(bytes);
    }
    
    public void recordFailedWrite() {
        failedWrites.incrementAndGet();
    }
    
    public long getTotalBytes() {
        return totalBytes.get();
    }
    
    public long getFailedWrites() {
        return failedWrites.get();
    }
    
    public double getFailureRate() {
        return recordsWritten > 0 ? 
            (double) failedWrites.get() / recordsWritten : 0.0;
    }
    
    public Duration getUptime() {
        return Duration.between(startTime, LocalDateTime.now());
    }
    
    public double getThroughput() {
        double seconds = getUptime().toSeconds();
        return seconds > 0 ? recordsWritten / seconds : 0.0;
    }
    
    @Override
    public String toString() {
        return String.format(
            "SinkMetadata{type=%s, destination=%s, " +
            "recordsWritten=%d, queueSize=%d, " +
            "failureRate=%.2f%%, throughput=%.2f/s}",
            type, destination, recordsWritten, queueSize,
            getFailureRate() * 100, getThroughput());
    }
} 