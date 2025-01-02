/**
 * Represents the result of data validation
 */
public class ValidationResult {
    private final boolean valid;
    private final Map<String, List<String>> errors;
    private final Map<String, String> warnings;
    private final ValidationLevel level;
    
    public ValidationResult(ValidationLevel level) {
        this.valid = true;
        this.errors = new HashMap<>();
        this.warnings = new HashMap<>();
        this.level = level;
    }
    
    public boolean isValid() {
        return valid && errors.isEmpty();
    }
    
    public void addError(String field, String message) {
        errors.computeIfAbsent(field, k -> new ArrayList<>())
              .add(message);
    }
    
    public void addWarning(String field, String message) {
        warnings.put(field, message);
    }
    
    public Map<String, List<String>> getErrors() {
        return Collections.unmodifiableMap(errors);
    }
    
    public Map<String, String> getWarnings() {
        return Collections.unmodifiableMap(warnings);
    }
    
    public ValidationLevel getLevel() {
        return level;
    }
    
    public boolean hasWarnings() {
        return !warnings.isEmpty();
    }
    
    @Override
    public String toString() {
        if (isValid()) {
            return "Valid" + (hasWarnings() ? " with warnings" : "");
        }
        return "Invalid: " + errors.toString();
    }
} 