/**
 * Composite index implementation for multiple fields
 */
public class CompositeIndex<T> {
    private final Map<List<Object>, Set<T>> indexMap;
    private final List<IndexExtractor<T>> extractors;
    
    public CompositeIndex(List<IndexExtractor<T>> extractors) {
        this.indexMap = new ConcurrentHashMap<>();
        this.extractors = new ArrayList<>(extractors);
    }
    
    public void addElement(T element) {
        List<Object> key = extractKey(element);
        if (key != null) {
            indexMap.computeIfAbsent(key, k -> ConcurrentHashMap.newKeySet())
                   .add(element);
        }
    }
    
    public void removeElement(T element) {
        List<Object> key = extractKey(element);
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
    
    private List<Object> extractKey(T element) {
        List<Object> key = new ArrayList<>();
        for (IndexExtractor<T> extractor : extractors) {
            Object value = extractor.extractKey(element);
            if (value == null) {
                return null;
            }
            key.add(value);
        }
        return key;
    }
    
    public Set<T> find(List<Object> key) {
        return indexMap.getOrDefault(key, Collections.emptySet());
    }
    
    public void clear() {
        indexMap.clear();
    }
} 