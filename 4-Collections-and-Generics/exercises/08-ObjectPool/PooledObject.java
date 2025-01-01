/**
 * Wrapper for pooled objects with metadata
 */
public class PooledObject<T> {
    private final T object;
    private final long creationTime;
    private long lastAccessTime;
    private boolean inUse;
    
    public PooledObject(T object) {
        this.object = object;
        this.creationTime = System.currentTimeMillis();
        this.lastAccessTime = creationTime;
        this.inUse = false;
    }
    
    public T getObject() {
        lastAccessTime = System.currentTimeMillis();
        return object;
    }
    
    public void markAsInUse() {
        inUse = true;
        lastAccessTime = System.currentTimeMillis();
    }
    
    public void markAsAvailable() {
        inUse = false;
        lastAccessTime = System.currentTimeMillis();
    }
    
    public boolean isExpired(long maxIdleTimeMillis) {
        return !inUse && 
            System.currentTimeMillis() - lastAccessTime > maxIdleTimeMillis;
    }
} 