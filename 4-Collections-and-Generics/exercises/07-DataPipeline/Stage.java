/**
 * Interface for pipeline processing stages
 */
public interface Stage<I, O> {
    O process(I input) throws Exception;
    
    default void handleError(I input, Exception e) {
        System.err.println("Error processing input: " + input);
        e.printStackTrace();
    }
} 