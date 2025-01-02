/**
 * Tracks statistics for the data processing pipeline
 */
public class ProcessingStats {
    private final AtomicLong totalProcessed;
    private final AtomicLong successCount;
    private final AtomicLong errorCount;
    private final AtomicLong sourceErrors;
    private final AtomicLong transformErrors;
    private final AtomicLong validateErrors;
    private final AtomicLong sinkErrors;
    private final AtomicLong unexpectedErrors;
    private final AtomicLong batchCount;
    private final LocalDateTime startTime;
    
    public ProcessingStats() {
        this.totalProcessed = new AtomicLong();
        this.successCount = new AtomicLong();
        this.errorCount = new AtomicLong();
        this.sourceErrors = new AtomicLong();
        this.transformErrors = new AtomicLong();
        this.validateErrors = new AtomicLong();
        this.sinkErrors = new AtomicLong();
        this.unexpectedErrors = new AtomicLong();
        this.batchCount = new AtomicLong();
        this.startTime = LocalDateTime.now();
    }
    
    public void recordSuccess() {
        successCount.incrementAndGet();
        totalProcessed.incrementAndGet();
    }
    
    public void recordError() {
        errorCount.incrementAndGet();
        totalProcessed.incrementAndGet();
    }
    
    public void recordSourceError() {
        sourceErrors.incrementAndGet();
        recordError();
    }
    
    public void recordTransformationError() {
        transformErrors.incrementAndGet();
        recordError();
    }
    
    public void recordValidationError() {
        validateErrors.incrementAndGet();
        recordError();
    }
    
    public void recordSinkError() {
        sinkErrors.incrementAndGet();
        recordError();
    }
    
    public void recordUnexpectedError() {
        unexpectedErrors.incrementAndGet();
        recordError();
    }
    
    public void recordBatchFetch() {
        batchCount.incrementAndGet();
    }
    
    public double getErrorRate() {
        long total = totalProcessed.get();
        return total > 0 ? (double) errorCount.get() / total : 0.0;
    }
    
    public ProcessingStats snapshot() {
        ProcessingStats snapshot = new ProcessingStats();
        snapshot.totalProcessed.set(this.totalProcessed.get());
        snapshot.successCount.set(this.successCount.get());
        snapshot.errorCount.set(this.errorCount.get());
        snapshot.sourceErrors.set(this.sourceErrors.get());
        snapshot.transformErrors.set(this.transformErrors.get());
        snapshot.validateErrors.set(this.validateErrors.get());
        snapshot.sinkErrors.set(this.sinkErrors.get());
        snapshot.unexpectedErrors.set(this.unexpectedErrors.get());
        snapshot.batchCount.set(this.batchCount.get());
        return snapshot;
    }
    
    @Override
    public String toString() {
        Duration uptime = Duration.between(startTime, LocalDateTime.now());
        return String.format(
            "ProcessingStats{total=%d, success=%d, errors=%d, errorRate=%.2f%%, " +
            "sourceErrors=%d, transformErrors=%d, validateErrors=%d, " +
            "sinkErrors=%d, unexpectedErrors=%d, batches=%d, uptime=%s}",
            totalProcessed.get(), successCount.get(), errorCount.get(),
            getErrorRate() * 100, sourceErrors.get(), transformErrors.get(),
            validateErrors.get(), sinkErrors.get(), unexpectedErrors.get(),
            batchCount.get(), formatDuration(uptime));
    }
    
    private String formatDuration(Duration duration) {
        long hours = duration.toHours();
        long minutes = duration.toMinutesPart();
        long seconds = duration.toSecondsPart();
        return String.format("%02d:%02d:%02d", hours, minutes, seconds);
    }
} 