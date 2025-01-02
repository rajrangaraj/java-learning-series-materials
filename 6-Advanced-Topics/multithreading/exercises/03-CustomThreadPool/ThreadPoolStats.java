/**
 * Statistics tracking for custom thread pool
 */
public class ThreadPoolStats {
    private final AtomicLong taskSubmissions;
    private final AtomicLong taskRejections;
    private final AtomicLong taskStarts;
    private final AtomicLong taskCompletions;
    private final AtomicLong taskFailures;
    private final AtomicLong taskSteals;
    private final AtomicLong totalExecutionTime;
    
    public ThreadPoolStats() {
        this.taskSubmissions = new AtomicLong();
        this.taskRejections = new AtomicLong();
        this.taskStarts = new AtomicLong();
        this.taskCompletions = new AtomicLong();
        this.taskFailures = new AtomicLong();
        this.taskSteals = new AtomicLong();
        this.totalExecutionTime = new AtomicLong();
    }
    
    public void recordSubmission() {
        taskSubmissions.incrementAndGet();
    }
    
    public void recordRejection() {
        taskRejections.incrementAndGet();
    }
    
    public void recordTaskStart() {
        taskStarts.incrementAndGet();
    }
    
    public void recordTaskCompletion(long executionTimeNanos) {
        taskCompletions.incrementAndGet();
        totalExecutionTime.addAndGet(executionTimeNanos);
    }
    
    public void recordTaskFailure() {
        taskFailures.incrementAndGet();
    }
    
    public void recordTaskSteal() {
        taskSteals.incrementAndGet();
    }
    
    public long getTaskSubmissions() {
        return taskSubmissions.get();
    }
    
    public long getTaskRejections() {
        return taskRejections.get();
    }
    
    public long getTaskStarts() {
        return taskStarts.get();
    }
    
    public long getTaskCompletions() {
        return taskCompletions.get();
    }
    
    public long getTaskFailures() {
        return taskFailures.get();
    }
    
    public long getTaskSteals() {
        return taskSteals.get();
    }
    
    public double getAverageExecutionTime() {
        long completions = taskCompletions.get();
        return completions == 0 ? 0 : 
            (double) totalExecutionTime.get() / completions / 1_000_000; // Convert to ms
    }
    
    @Override
    public String toString() {
        return String.format(
            "ThreadPoolStats{submissions=%d, rejections=%d, " +
            "completions=%d, failures=%d, steals=%d, avgExecTime=%.2fms}",
            taskSubmissions.get(), taskRejections.get(),
            taskCompletions.get(), taskFailures.get(),
            taskSteals.get(), getAverageExecutionTime());
    }
} 