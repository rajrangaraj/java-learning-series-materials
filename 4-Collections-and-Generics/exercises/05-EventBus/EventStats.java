/**
 * Statistics tracking for event bus operations
 */
public class EventStats {
    private final AtomicLong publishCount;
    private final AtomicLong deliveryCount;
    private final AtomicLong errorCount;
    private final AtomicLong deadLetterCount;
    
    public EventStats() {
        this.publishCount = new AtomicLong();
        this.deliveryCount = new AtomicLong();
        this.errorCount = new AtomicLong();
        this.deadLetterCount = new AtomicLong();
    }
    
    public void recordPublish() { publishCount.incrementAndGet(); }
    public void recordDelivery() { deliveryCount.incrementAndGet(); }
    public void recordError() { errorCount.incrementAndGet(); }
    public void recordDeadLetter() { deadLetterCount.incrementAndGet(); }
    
    public double getDeliveryRate() {
        long total = publishCount.get();
        return total == 0 ? 0.0 : (double) deliveryCount.get() / total;
    }
    
    @Override
    public String toString() {
        return String.format(
            "EventStats{published=%d, delivered=%d, errors=%d, deadLetters=%d, deliveryRate=%.2f}",
            publishCount.get(), deliveryCount.get(), errorCount.get(), 
            deadLetterCount.get(), getDeliveryRate()
        );
    }
} 