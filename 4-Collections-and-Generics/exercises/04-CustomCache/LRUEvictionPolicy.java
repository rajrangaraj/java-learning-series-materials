/**
 * Least Recently Used eviction policy implementation
 */
public class LRUEvictionPolicy<K, V> implements EvictionPolicy<K, V> {
    private final LinkedHashMap<K, V> accessOrder;
    
    public LRUEvictionPolicy() {
        this.accessOrder = new LinkedHashMap<>(16, 0.75f, true);
    }
    
    @Override
    public void onAccess(K key, V value) {
        accessOrder.get(key); // Updates access order
    }
    
    @Override
    public void onAdd(K key, V value) {
        accessOrder.put(key, value);
    }
    
    @Override
    public K getEvictionCandidate() {
        return accessOrder.keySet().iterator().next();
    }
} 