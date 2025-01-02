/**
 * Exception thrown when data transformation fails
 */
public class TransformationException extends DataProcessingException {
    private final String field;
    private final Object value;
    private final String transformation;
    
    public TransformationException(String message) {
        super(message);
        this.field = "unknown";
        this.value = null;
        this.transformation = "unknown";
    }
    
    public TransformationException(String message, String field, 
            Object value, String transformation) {
        super(message);
        this.field = field;
        this.value = value;
        this.transformation = transformation;
    }
    
    public TransformationException(String message, Throwable cause) {
        super(message, cause);
        this.field = "unknown";
        this.value = null;
        this.transformation = "unknown";
    }
    
    public TransformationException(String message, Throwable cause,
            String field, Object value, String transformation) {
        super(message, cause);
        this.field = field;
        this.value = value;
        this.transformation = transformation;
    }
    
    public String getField() {
        return field;
    }
    
    public Object getValue() {
        return value;
    }
    
    public String getTransformation() {
        return transformation;
    }
    
    @Override
    public String getDetails() {
        return String.format("Field: %s, Value: %s, Transformation: %s",
            field, value, transformation);
    }
} 