/**
 * LRU Cache implementation using LinkedHashMap
 */
public class LRUCache<K, V> extends LinkedHashMap<K, V> {
    private final int capacity;
    
    public LRUCache(int capacity) {
        super(capacity, 0.75f, true);
        this.capacity = capacity;
    }
    
    @Override
    protected boolean removeEldestEntry(Map.Entry<K, V> eldest) {
        return size() > capacity;
    }
    
    public V getOrDefault(K key, Supplier<V> defaultValueSupplier) {
        return computeIfAbsent(key, k -> defaultValueSupplier.get());
    }
    
    public List<K> getMostRecentKeys(int n) {
        return keySet().stream()
                      .limit(n)
                      .collect(Collectors.toList());
    }
    
    public void putAll(Map<K, V> map, boolean skipExisting) {
        map.forEach((key, value) -> {
            if (!skipExisting || !containsKey(key)) {
                put(key, value);
            }
        });
    }
} 