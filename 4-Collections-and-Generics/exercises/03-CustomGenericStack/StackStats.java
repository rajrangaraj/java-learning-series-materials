/**
 * Statistics tracking for stack operations
 */
public class StackStats {
    private final AtomicLong pushCount;
    private final AtomicLong popCount;
    private final AtomicLong searchCount;
    private final AtomicLong searchHits;
    private final AtomicLong resizeCount;
    private final AtomicLong clearCount;
    private volatile int currentCapacity;
    
    public StackStats() {
        this.pushCount = new AtomicLong();
        this.popCount = new AtomicLong();
        this.searchCount = new AtomicLong();
        this.searchHits = new AtomicLong();
        this.resizeCount = new AtomicLong();
        this.clearCount = new AtomicLong();
    }
    
    public void recordPush() { pushCount.incrementAndGet(); }
    public void recordPop() { popCount.incrementAndGet(); }
    public void recordClear() { clearCount.incrementAndGet(); }
    
    public void recordSearch(boolean found) {
        searchCount.incrementAndGet();
        if (found) {
            searchHits.incrementAndGet();
        }
    }
    
    public void recordResize(int newCapacity) {
        resizeCount.incrementAndGet();
        this.currentCapacity = newCapacity;
    }
    
    public double getSearchHitRate() {
        long total = searchCount.get();
        return total == 0 ? 0.0 : (double) searchHits.get() / total;
    }
    
    @Override
    public String toString() {
        return String.format(
            "StackStats{pushes=%d, pops=%d, searches=%d, " +
            "searchHitRate=%.2f, resizes=%d, clears=%d, capacity=%d}",
            pushCount.get(), popCount.get(), searchCount.get(),
            getSearchHitRate(), resizeCount.get(), clearCount.get(),
            currentCapacity);
    }
} 