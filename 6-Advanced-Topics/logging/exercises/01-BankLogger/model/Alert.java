/**
 * Represents a system or security alert
 */
public class Alert {
    private final String id;
    private final AlertType type;
    private final Severity severity;
    private final String message;
    private final LocalDateTime timestamp;
    private final Object details;
    private final Set<String> recipients;
    private final Map<String, Object> metadata;
    
    public Alert(AlertType type, Severity severity, String message, Object details) {
        this.id = UUID.randomUUID().toString();
        this.type = type;
        this.severity = severity;
        this.message = message;
        this.timestamp = LocalDateTime.now();
        this.details = details;
        this.recipients = new HashSet<>();
        this.metadata = new HashMap<>();
    }
    
    public String getId() {
        return id;
    }
    
    public AlertType getType() {
        return type;
    }
    
    public Severity getSeverity() {
        return severity;
    }
    
    public String getMessage() {
        return message;
    }
    
    public LocalDateTime getTimestamp() {
        return timestamp;
    }
    
    public Object getDetails() {
        return details;
    }
    
    public Set<String> getRecipients() {
        return Collections.unmodifiableSet(recipients);
    }
    
    public Map<String, Object> getMetadata() {
        return Collections.unmodifiableMap(metadata);
    }
    
    public void addRecipient(String recipient) {
        this.recipients.add(recipient);
    }
    
    public void addMetadata(String key, Object value) {
        this.metadata.put(key, value);
    }
} 