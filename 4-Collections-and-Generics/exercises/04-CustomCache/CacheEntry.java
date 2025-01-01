/**
 * Wrapper class for cache entries with metadata
 */
public class CacheEntry<V> {
    private final V value;
    private final long creationTime;
    private final long expirationTime;
    private long lastAccessTime;
    private int accessCount;
    
    public CacheEntry(V value, long ttlMillis) {
        this.value = value;
        this.creationTime = System.currentTimeMillis();
        this.expirationTime = ttlMillis > 0 ? creationTime + ttlMillis : Long.MAX_VALUE;
        this.lastAccessTime = creationTime;
        this.accessCount = 0;
    }
    
    public V getValue() {
        this.lastAccessTime = System.currentTimeMillis();
        this.accessCount++;
        return value;
    }
    
    public boolean isExpired() {
        return System.currentTimeMillis() > expirationTime;
    }
    
    public long getLastAccessTime() {
        return lastAccessTime;
    }
    
    public int getAccessCount() {
        return accessCount;
    }
} 