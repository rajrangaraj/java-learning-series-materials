/**
 * Generic collection with multiple indexing support
 */
public class IndexedCollection<T> {
    private final Map<String, Index<T>> indexes;
    private final Set<T> elements;
    private final IndexStats stats;
    
    public IndexedCollection() {
        this.indexes = new ConcurrentHashMap<>();
        this.elements = ConcurrentHashMap.newKeySet();
        this.stats = new IndexStats();
    }
    
    public void createIndex(String indexName, IndexExtractor<T> extractor) {
        Index<T> index = new Index<>(extractor);
        indexes.put(indexName, index);
        
        // Build index for existing elements
        elements.forEach(element -> index.addElement(element));
        stats.recordIndexCreated();
    }
    
    public void dropIndex(String indexName) {
        if (indexes.remove(indexName) != null) {
            stats.recordIndexDropped();
        }
    }
    
    public void add(T element) {
        if (elements.add(element)) {
            indexes.values().forEach(index -> index.addElement(element));
            stats.recordElementAdded();
        }
    }
    
    public void remove(T element) {
        if (elements.remove(element)) {
            indexes.values().forEach(index -> index.removeElement(element));
            stats.recordElementRemoved();
        }
    }
    
    public Set<T> findByIndex(String indexName, Object value) {
        Index<T> index = indexes.get(indexName);
        if (index == null) {
            throw new IllegalArgumentException("Index not found: " + indexName);
        }
        
        Set<T> results = index.find(value);
        stats.recordQuery();
        return Collections.unmodifiableSet(results);
    }
    
    public Set<T> findByCompositeIndex(Map<String, Object> criteria) {
        if (criteria.isEmpty()) {
            return Collections.unmodifiableSet(elements);
        }
        
        Set<T> results = null;
        for (Map.Entry<String, Object> entry : criteria.entrySet()) {
            Set<T> indexResults = findByIndex(entry.getKey(), entry.getValue());
            
            if (results == null) {
                results = new HashSet<>(indexResults);
            } else {
                results.retainAll(indexResults);
            }
            
            if (results.isEmpty()) {
                break;
            }
        }
        
        return Collections.unmodifiableSet(results != null ? results : new HashSet<>());
    }
    
    public void rebuildIndex(String indexName) {
        Index<T> index = indexes.get(indexName);
        if (index != null) {
            index.clear();
            elements.forEach(element -> index.addElement(element));
            stats.recordIndexRebuilt();
        }
    }
    
    public void rebuildAllIndexes() {
        indexes.keySet().forEach(this::rebuildIndex);
    }
    
    public Set<String> getIndexNames() {
        return Collections.unmodifiableSet(indexes.keySet());
    }
    
    public IndexStats getStats() {
        return stats;
    }
    
    public int size() {
        return elements.size();
    }
} 