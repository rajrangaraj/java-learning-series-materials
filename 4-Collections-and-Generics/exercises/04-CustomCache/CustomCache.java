/**
 * Generic cache implementation with eviction and statistics
 */
public class CustomCache<K, V> {
    private final ConcurrentHashMap<K, CacheEntry<V>> cache;
    private final EvictionPolicy<K, V> evictionPolicy;
    private final int maxSize;
    private final CacheStats stats;
    private final ScheduledExecutorService cleanup;
    
    public CustomCache(int maxSize, EvictionPolicy<K, V> evictionPolicy) {
        this.cache = new ConcurrentHashMap<>();
        this.evictionPolicy = evictionPolicy;
        this.maxSize = maxSize;
        this.stats = new CacheStats();
        this.cleanup = Executors.newSingleThreadScheduledExecutor();
        
        // Schedule cleanup task
        cleanup.scheduleAtFixedRate(
            this::removeExpiredEntries,
            1, 1, TimeUnit.MINUTES
        );
    }
    
    public void put(K key, V value, long ttlMillis) {
        if (cache.size() >= maxSize) {
            evict();
        }
        
        CacheEntry<V> entry = new CacheEntry<>(value, ttlMillis);
        cache.put(key, entry);
        evictionPolicy.onAdd(key, value);
        stats.recordPut();
    }
    
    public Optional<V> get(K key) {
        CacheEntry<V> entry = cache.get(key);
        
        if (entry == null) {
            stats.recordMiss();
            return Optional.empty();
        }
        
        if (entry.isExpired()) {
            cache.remove(key);
            stats.recordMiss();
            return Optional.empty();
        }
        
        evictionPolicy.onAccess(key, entry.getValue());
        stats.recordHit();
        return Optional.of(entry.getValue());
    }
    
    private void evict() {
        K keyToEvict = evictionPolicy.getEvictionCandidate();
        if (keyToEvict != null) {
            cache.remove(keyToEvict);
            stats.recordEviction();
        }
    }
    
    private void removeExpiredEntries() {
        cache.entrySet().removeIf(entry -> entry.getValue().isExpired());
    }
    
    public CacheStats getStats() {
        return stats;
    }
    
    public void shutdown() {
        cleanup.shutdown();
    }
} 