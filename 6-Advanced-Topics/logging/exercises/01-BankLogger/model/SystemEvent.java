/**
 * Represents a system-level event
 */
public class SystemEvent {
    private String id;
    private SystemEventType type;
    private String component;
    private String operation;
    private LocalDateTime timestamp;
    private Map<String, Object> parameters;
    private boolean requiresAudit;
    
    public SystemEvent(SystemEventType type, String component, String operation) {
        this.id = UUID.randomUUID().toString();
        this.type = type;
        this.component = component;
        this.operation = operation;
        this.timestamp = LocalDateTime.now();
        this.parameters = new HashMap<>();
        this.requiresAudit = false;
    }
    
    public String getId() {
        return id;
    }
    
    public SystemEventType getType() {
        return type;
    }
    
    public String getComponent() {
        return component;
    }
    
    public String getOperation() {
        return operation;
    }
    
    public LocalDateTime getTimestamp() {
        return timestamp;
    }
    
    public Map<String, Object> getParameters() {
        return Collections.unmodifiableMap(parameters);
    }
    
    public boolean requiresAudit() {
        return requiresAudit;
    }
    
    public void addParameter(String key, Object value) {
        this.parameters.put(key, value);
    }
    
    public void setRequiresAudit(boolean requiresAudit) {
        this.requiresAudit = requiresAudit;
    }
} 