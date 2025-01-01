/**
 * Thread-safe cache implementation with expiration
 */
public class ConcurrentCache<K, V> {
    private class CacheEntry {
        V value;
        long expirationTime;
        
        CacheEntry(V value, long expirationTimeMillis) {
            this.value = value;
            this.expirationTime = System.currentTimeMillis() + expirationTimeMillis;
        }
        
        boolean isExpired() {
            return System.currentTimeMillis() > expirationTime;
        }
    }
    
    private final ConcurrentHashMap<K, CacheEntry> cache;
    private final long defaultExpirationMillis;
    private final ScheduledExecutorService cleanup;
    
    public ConcurrentCache(long defaultExpirationMillis, long cleanupIntervalMillis) {
        this.cache = new ConcurrentHashMap<>();
        this.defaultExpirationMillis = defaultExpirationMillis;
        
        this.cleanup = Executors.newSingleThreadScheduledExecutor();
        this.cleanup.scheduleAtFixedRate(
            this::removeExpiredEntries,
            cleanupIntervalMillis,
            cleanupIntervalMillis,
            TimeUnit.MILLISECONDS
        );
    }
    
    public void put(K key, V value) {
        put(key, value, defaultExpirationMillis);
    }
    
    public void put(K key, V value, long expirationMillis) {
        cache.put(key, new CacheEntry(value, expirationMillis));
    }
    
    public Optional<V> get(K key) {
        CacheEntry entry = cache.get(key);
        
        if (entry == null || entry.isExpired()) {
            cache.remove(key);
            return Optional.empty();
        }
        
        return Optional.of(entry.value);
    }
    
    private void removeExpiredEntries() {
        cache.entrySet().removeIf(entry -> entry.getValue().isExpired());
    }
    
    public void shutdown() {
        cleanup.shutdown();
    }
} 