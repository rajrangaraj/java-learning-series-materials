/**
 * Demonstrates exception logging patterns using SLF4J and Logback
 */
public class ExceptionLogging {
    private static final Logger logger = LoggerFactory.getLogger(ExceptionLogging.class);
    
    public static void main(String[] args) {
        demonstrateBasicLogging();
        demonstrateParameterizedLogging();
        demonstrateExceptionLogging();
        demonstrateContextualLogging();
        demonstrateLogLevels();
    }
    
    private static void demonstrateBasicLogging() {
        try {
            // Attempt some operation that might fail
            throw new RuntimeException("Basic error");
        } catch (RuntimeException e) {
            // Basic error logging
            logger.error("An error occurred", e);
        }
    }
    
    private static void demonstrateParameterizedLogging() {
        String username = "john_doe";
        int attemptNumber = 3;
        
        try {
            // Simulate failed login attempt
            throw new SecurityException("Invalid credentials");
        } catch (SecurityException e) {
            // Parameterized logging with context
            logger.warn("Login failed for user {} (attempt {})", username, attemptNumber, e);
        }
    }
    
    private static void demonstrateExceptionLogging() {
        try {
            processComplexOperation();
        } catch (Exception e) {
            // Log with different levels based on exception type
            if (e instanceof IllegalArgumentException) {
                logger.warn("Invalid input in complex operation", e);
            } else if (e instanceof IOException) {
                logger.error("IO error in complex operation", e);
            } else {
                logger.error("Unexpected error in complex operation", e);
            }
        }
    }
    
    private static void demonstrateContextualLogging() {
        // Using MDC (Mapped Diagnostic Context)
        MDC.put("requestId", UUID.randomUUID().toString());
        MDC.put("userId", "12345");
        
        try {
            // Simulate database operation
            throw new SQLException("Database connection failed");
        } catch (SQLException e) {
            logger.error("Database operation failed", e);
        } finally {
            MDC.clear();
        }
    }
    
    private static void demonstrateLogLevels() {
        try {
            riskyOperation();
        } catch (Exception e) {
            // Debug - detailed information for debugging
            logger.debug("Detailed error information", e);
            
            // Info - general information about application operation
            logger.info("Operation failed and will be retried");
            
            // Warn - potentially harmful situations
            logger.warn("Operation failed but application can continue", e);
            
            // Error - error events that might still allow the application to continue
            logger.error("Serious problem occurred", e);
            
            // Fatal - very severe error events that will presumably lead to application failure
            if (e instanceof OutOfMemoryError) {
                logger.error("Fatal error occurred", e);
                System.exit(1);
            }
        }
    }
    
    private static void processComplexOperation() throws Exception {
        double random = Math.random();
        if (random < 0.3) {
            throw new IllegalArgumentException("Invalid input");
        } else if (random < 0.6) {
            throw new IOException("IO error");
        } else {
            throw new RuntimeException("Unknown error");
        }
    }
    
    private static void riskyOperation() throws Exception {
        throw new Exception("Something went wrong");
    }
} 