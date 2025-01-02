/**
 * Exception for file validation errors
 */
public class ValidationException extends ProcessingException {
    
    public ValidationException(String message) {
        super(message, "VALIDATION_ERROR");
    }
    
    public ValidationException(String message, Map<String, String> details) {
        super(message, "VALIDATION_ERROR", details);
    }
    
    public ValidationException(String message, Throwable cause) {
        super(message, "VALIDATION_ERROR", cause);
    }
} 