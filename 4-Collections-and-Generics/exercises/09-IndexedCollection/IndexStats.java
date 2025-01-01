/**
 * Statistics tracking for indexed collection operations
 */
public class IndexStats {
    private final AtomicLong indexCreatedCount;
    private final AtomicLong indexDroppedCount;
    private final AtomicLong indexRebuiltCount;
    private final AtomicLong elementAddedCount;
    private final AtomicLong elementRemovedCount;
    private final AtomicLong queryCount;
    
    public IndexStats() {
        this.indexCreatedCount = new AtomicLong();
        this.indexDroppedCount = new AtomicLong();
        this.indexRebuiltCount = new AtomicLong();
        this.elementAddedCount = new AtomicLong();
        this.elementRemovedCount = new AtomicLong();
        this.queryCount = new AtomicLong();
    }
    
    public void recordIndexCreated() { indexCreatedCount.incrementAndGet(); }
    public void recordIndexDropped() { indexDroppedCount.incrementAndGet(); }
    public void recordIndexRebuilt() { indexRebuiltCount.incrementAndGet(); }
    public void recordElementAdded() { elementAddedCount.incrementAndGet(); }
    public void recordElementRemoved() { elementRemovedCount.incrementAndGet(); }
    public void recordQuery() { queryCount.incrementAndGet(); }
    
    @Override
    public String toString() {
        return String.format(
            "IndexStats{created=%d, dropped=%d, rebuilt=%d, " +
            "added=%d, removed=%d, queries=%d}",
            indexCreatedCount.get(), indexDroppedCount.get(), 
            indexRebuiltCount.get(), elementAddedCount.get(),
            elementRemovedCount.get(), queryCount.get()
        );
    }
} 