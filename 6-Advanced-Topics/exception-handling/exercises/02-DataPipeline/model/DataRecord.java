/**
 * Represents a data record in the processing pipeline
 */
public class DataRecord {
    private final String id;
    private final Map<String, Object> data;
    private final Map<String, Object> metadata;
    private final LocalDateTime timestamp;
    private ProcessingState state;
    private List<String> processingHistory;
    
    public DataRecord(String id, Map<String, Object> data) {
        this.id = id;
        this.data = new HashMap<>(data);
        this.metadata = new HashMap<>();
        this.timestamp = LocalDateTime.now();
        this.state = ProcessingState.NEW;
        this.processingHistory = new ArrayList<>();
    }
    
    public String getId() {
        return id;
    }
    
    public Map<String, Object> getData() {
        return Collections.unmodifiableMap(data);
    }
    
    public Object getValue(String key) {
        return data.get(key);
    }
    
    public void setValue(String key, Object value) {
        data.put(key, value);
        addToHistory("Updated field: " + key);
    }
    
    public Map<String, Object> getMetadata() {
        return Collections.unmodifiableMap(metadata);
    }
    
    public void setMetadata(String key, Object value) {
        metadata.put(key, value);
    }
    
    public LocalDateTime getTimestamp() {
        return timestamp;
    }
    
    public ProcessingState getState() {
        return state;
    }
    
    public void setState(ProcessingState state) {
        this.state = state;
        addToHistory("State changed to: " + state);
    }
    
    public List<String> getProcessingHistory() {
        return Collections.unmodifiableList(processingHistory);
    }
    
    private void addToHistory(String event) {
        processingHistory.add(LocalDateTime.now() + " - " + event);
    }
    
    public DataRecord copy() {
        DataRecord copy = new DataRecord(id, new HashMap<>(data));
        copy.metadata.putAll(this.metadata);
        copy.state = this.state;
        copy.processingHistory.addAll(this.processingHistory);
        return copy;
    }
    
    @Override
    public String toString() {
        return String.format("DataRecord{id=%s, state=%s, fields=%d}",
            id, state, data.size());
    }
} 