/**
 * Statistics tracking for message queue operations
 */
public class QueueStats {
    private final AtomicLong producedMessages;
    private final AtomicLong consumedMessages;
    private final AtomicLong rejectedMessages;
    private final AtomicLong failedPolls;
    private final AtomicLong batchOperations;
    private final AtomicLong totalBatchSize;
    
    public QueueStats() {
        this.producedMessages = new AtomicLong();
        this.consumedMessages = new AtomicLong();
        this.rejectedMessages = new AtomicLong();
        this.failedPolls = new AtomicLong();
        this.batchOperations = new AtomicLong();
        this.totalBatchSize = new AtomicLong();
    }
    
    public void recordProducedMessage() {
        producedMessages.incrementAndGet();
    }
    
    public void recordConsumedMessage() {
        consumedMessages.incrementAndGet();
    }
    
    public void recordRejectedMessage() {
        rejectedMessages.incrementAndGet();
    }
    
    public void recordFailedPoll() {
        failedPolls.incrementAndGet();
    }
    
    public void recordBatchConsume(int batchSize) {
        batchOperations.incrementAndGet();
        totalBatchSize.addAndGet(batchSize);
    }
    
    public long getProducedMessages() {
        return producedMessages.get();
    }
    
    public long getConsumedMessages() {
        return consumedMessages.get();
    }
    
    public long getRejectedMessages() {
        return rejectedMessages.get();
    }
    
    public long getFailedPolls() {
        return failedPolls.get();
    }
    
    public double getAverageBatchSize() {
        long operations = batchOperations.get();
        return operations == 0 ? 0 : (double) totalBatchSize.get() / operations;
    }
    
    @Override
    public String toString() {
        return String.format(
            "QueueStats{produced=%d, consumed=%d, rejected=%d, " +
            "failedPolls=%d, avgBatchSize=%.2f}",
            producedMessages.get(), consumedMessages.get(),
            rejectedMessages.get(), failedPolls.get(),
            getAverageBatchSize());
    }
} 