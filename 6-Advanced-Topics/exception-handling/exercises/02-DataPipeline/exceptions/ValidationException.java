/**
 * Exception thrown when data validation fails
 */
public class ValidationException extends DataProcessingException {
    private final Map<String, List<String>> violations;
    private final ValidationLevel level;
    
    public ValidationException(String message) {
        super(message);
        this.violations = new HashMap<>();
        this.level = ValidationLevel.NORMAL;
    }
    
    public ValidationException(String message, 
            Map<String, List<String>> violations,
            ValidationLevel level) {
        super(message);
        this.violations = new HashMap<>(violations);
        this.level = level;
    }
    
    public ValidationException(String message, Throwable cause) {
        super(message, cause);
        this.violations = new HashMap<>();
        this.level = ValidationLevel.NORMAL;
    }
    
    public ValidationException(String message, Throwable cause,
            Map<String, List<String>> violations,
            ValidationLevel level) {
        super(message, cause);
        this.violations = new HashMap<>(violations);
        this.level = level;
    }
    
    public Map<String, List<String>> getViolations() {
        return Collections.unmodifiableMap(violations);
    }
    
    public ValidationLevel getLevel() {
        return level;
    }
    
    public void addViolation(String field, String message) {
        violations.computeIfAbsent(field, k -> new ArrayList<>())
                 .add(message);
    }
    
    @Override
    public String getDetails() {
        return String.format("Validation Level: %s, Violations: %s",
            level, violations);
    }
} 