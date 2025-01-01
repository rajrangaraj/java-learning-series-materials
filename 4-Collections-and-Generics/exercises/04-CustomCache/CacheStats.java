/**
 * Statistics tracking for cache operations
 */
public class CacheStats {
    private final AtomicLong hits;
    private final AtomicLong misses;
    private final AtomicLong puts;
    private final AtomicLong evictions;
    
    public CacheStats() {
        this.hits = new AtomicLong();
        this.misses = new AtomicLong();
        this.puts = new AtomicLong();
        this.evictions = new AtomicLong();
    }
    
    public void recordHit() { hits.incrementAndGet(); }
    public void recordMiss() { misses.incrementAndGet(); }
    public void recordPut() { puts.incrementAndGet(); }
    public void recordEviction() { evictions.incrementAndGet(); }
    
    public double getHitRate() {
        long totalRequests = hits.get() + misses.get();
        return totalRequests == 0 ? 0.0 : (double) hits.get() / totalRequests;
    }
    
    @Override
    public String toString() {
        return String.format(
            "CacheStats{hits=%d, misses=%d, puts=%d, evictions=%d, hitRate=%.2f}",
            hits.get(), misses.get(), puts.get(), evictions.get(), getHitRate()
        );
    }
} 