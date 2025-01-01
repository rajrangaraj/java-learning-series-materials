/**
 * Interface defining cache eviction policies
 */
public interface EvictionPolicy<K, V> {
    /**
     * Called when an entry is accessed
     */
    void onAccess(K key, V value);
    
    /**
     * Called when an entry is added
     */
    void onAdd(K key, V value);
    
    /**
     * Returns the next key to evict
     */
    K getEvictionCandidate();
} 