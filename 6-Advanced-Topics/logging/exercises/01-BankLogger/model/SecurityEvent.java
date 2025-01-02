/**
 * Represents a security-related event
 */
public class SecurityEvent {
    private String id;
    private SecurityEventType type;
    private Severity severity;
    private String location;
    private String userId;
    private LocalDateTime timestamp;
    private Map<String, Object> metadata;
    
    // Constructor, getters, and setters omitted for brevity
} 