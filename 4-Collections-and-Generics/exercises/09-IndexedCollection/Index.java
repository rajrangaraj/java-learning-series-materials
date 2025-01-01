/**
 * Generic index implementation
 */
public class Index<T> {
    private final Map<Object, Set<T>> indexMap;
    private final IndexExtractor<T> extractor;
    
    public Index(IndexExtractor<T> extractor) {
        this.indexMap = new ConcurrentHashMap<>();
        this.extractor = extractor;
    }
    
    public void addElement(T element) {
        Object key = extractor.extractKey(element);
        if (key != null) {
            indexMap.computeIfAbsent(key, k -> ConcurrentHashMap.newKeySet())
                   .add(element);
        }
    }
    
    public void removeElement(T element) {
        Object key = extractor.extractKey(element);
        if (key != null) {
            Set<T> elements = indexMap.get(key);
            if (elements != null) {
                elements.remove(element);
                if (elements.isEmpty()) {
                    indexMap.remove(key);
                }
            }
        }
    }
    
    public Set<T> find(Object key) {
        return indexMap.getOrDefault(key, Collections.emptySet());
    }
    
    public void clear() {
        indexMap.clear();
    }
    
    public int size() {
        return indexMap.size();
    }
} 