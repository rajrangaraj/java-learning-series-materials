/**
 * Base exception for file processing errors
 */
public class ProcessingException extends Exception {
    private final String errorCode;
    private final Map<String, String> details;
    
    public ProcessingException(String message, String errorCode) {
        this(message, errorCode, null, Collections.emptyMap());
    }
    
    public ProcessingException(String message, String errorCode, Throwable cause) {
        this(message, errorCode, cause, Collections.emptyMap());
    }
    
    public ProcessingException(String message, String errorCode, 
            Map<String, String> details) {
        this(message, errorCode, null, details);
    }
    
    public ProcessingException(String message, String errorCode, 
            Throwable cause, Map<String, String> details) {
        super(message, cause);
        this.errorCode = errorCode;
        this.details = new HashMap<>(details);
    }
    
    public String getErrorCode() {
        return errorCode;
    }
    
    public Map<String, String> getDetails() {
        return Collections.unmodifiableMap(details);
    }
    
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName())
          .append(": ")
          .append(getMessage())
          .append(" (")
          .append(errorCode)
          .append(")");
        
        if (!details.isEmpty()) {
            sb.append(" Details: ").append(details);
        }
        
        if (getCause() != null) {
            sb.append(" Caused by: ").append(getCause().getMessage());
        }
        
        return sb.toString();
    }
} 