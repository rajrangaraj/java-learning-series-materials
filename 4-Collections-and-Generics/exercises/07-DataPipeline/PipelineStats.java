/**
 * Statistics tracking for pipeline operations
 */
public class PipelineStats {
    private final AtomicLong inputCount;
    private final AtomicLong processedCount;
    private final AtomicLong errorCount;
    private final AtomicLong outputCount;
    
    public PipelineStats() {
        this.inputCount = new AtomicLong();
        this.processedCount = new AtomicLong();
        this.errorCount = new AtomicLong();
        this.outputCount = new AtomicLong();
    }
    
    public void recordInput() { inputCount.incrementAndGet(); }
    public void recordProcessed() { processedCount.incrementAndGet(); }
    public void recordError() { errorCount.incrementAndGet(); }
    public void recordOutput() { outputCount.incrementAndGet(); }
    
    public double getSuccessRate() {
        long total = processedCount.get();
        return total == 0 ? 0.0 : 
            (double) (processedCount.get() - errorCount.get()) / total;
    }
    
    @Override
    public String toString() {
        return String.format(
            "PipelineStats{input=%d, processed=%d, errors=%d, output=%d, successRate=%.2f}",
            inputCount.get(), processedCount.get(), errorCount.get(), 
            outputCount.get(), getSuccessRate()
        );
    }
} 