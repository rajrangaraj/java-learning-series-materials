/**
 * Statistics tracking for task execution
 */
public class TaskStats {
    private final AtomicLong scheduledCount;
    private final AtomicLong startedCount;
    private final AtomicLong completedCount;
    private final AtomicLong failedCount;
    
    public TaskStats() {
        this.scheduledCount = new AtomicLong();
        this.startedCount = new AtomicLong();
        this.completedCount = new AtomicLong();
        this.failedCount = new AtomicLong();
    }
    
    public void recordScheduled() { scheduledCount.incrementAndGet(); }
    public void recordStarted() { startedCount.incrementAndGet(); }
    public void recordCompleted() { completedCount.incrementAndGet(); }
    public void recordFailed() { failedCount.incrementAndGet(); }
    
    public double getSuccessRate() {
        long total = completedCount.get() + failedCount.get();
        return total == 0 ? 0.0 : (double) completedCount.get() / total;
    }
    
    @Override
    public String toString() {
        return String.format(
            "TaskStats{scheduled=%d, started=%d, completed=%d, failed=%d, successRate=%.2f}",
            scheduledCount.get(), startedCount.get(), completedCount.get(), 
            failedCount.get(), getSuccessRate()
        );
    }
} 