/**
 * Exception thrown when attempting to access an empty stack
 */
public class StackEmptyException extends RuntimeException {
    public StackEmptyException(String message) {
        super(message);
    }
} 