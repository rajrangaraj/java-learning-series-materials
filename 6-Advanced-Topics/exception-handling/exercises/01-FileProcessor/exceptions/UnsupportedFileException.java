/**
 * Exception for unsupported file types
 */
public class UnsupportedFileException extends ProcessingException {
    
    public UnsupportedFileException(String message) {
        super(message, "UNSUPPORTED_FILE");
    }
    
    public UnsupportedFileException(String message, Map<String, String> details) {
        super(message, "UNSUPPORTED_FILE", details);
    }
} 