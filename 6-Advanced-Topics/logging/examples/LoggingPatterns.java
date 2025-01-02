/**
 * Demonstrates various logging patterns and best practices using SLF4J and Logback
 */
public class LoggingPatterns {
    private static final Logger logger = LoggerFactory.getLogger(LoggingPatterns.class);
    
    public static void main(String[] args) {
        demonstrateBasicLogging();
        demonstrateParameterizedLogging();
        demonstrateContextualLogging();
        demonstratePerformanceLogging();
        demonstrateStructuredLogging();
        demonstrateConditionalLogging();
        demonstrateLogLevels();
    }
    
    private static void demonstrateBasicLogging() {
        logger.info("Starting basic logging demonstration");
        
        try {
            // Simulate some work
            processTask("sample-task");
        } catch (Exception e) {
            // Log full exception with stack trace
            logger.error("Error processing task", e);
        }
        
        // Log with different levels
        logger.debug("Debug message with details: {}", getCurrentState());
        logger.info("Operation completed successfully");
    }
    
    private static void demonstrateParameterizedLogging() {
        String user = "john_doe";
        int attempts = 3;
        long duration = 1500L;
        
        // Single parameter
        logger.info("User {} logged in", user);
        
        // Multiple parameters
        logger.info("Login attempt {} for user {} took {}ms", 
            attempts, user, duration);
        
        // With conditional logic
        if (logger.isDebugEnabled()) {
            // Expensive operation only performed if debug is enabled
            logger.debug("Detailed state: {}", generateDetailedState());
        }
        
        // Array parameters
        Object[] params = {user, attempts, duration};
        logger.info("Login summary: user={}, attempts={}, duration={}ms", params);
    }
    
    private static void demonstrateContextualLogging() {
        // Using MDC (Mapped Diagnostic Context)
        String requestId = UUID.randomUUID().toString();
        String userId = "12345";
        
        MDC.put("requestId", requestId);
        MDC.put("userId", userId);
        
        try {
            logger.info("Starting request processing");
            
            // Nested context
            MDC.put("operation", "validation");
            logger.debug("Validating request parameters");
            MDC.remove("operation");
            
            // Process request
            processRequest();
            
            logger.info("Request processing completed");
        } finally {
            MDC.clear(); // Clean up MDC
        }
    }
    
    private static void demonstratePerformanceLogging() {
        logger.info("Starting performance logging demonstration");
        
        // Using StopWatch for timing
        StopWatch watch = new StopWatch();
        watch.start("initialization");
        
        try {
            // Simulate initialization
            Thread.sleep(100);
            watch.stop();
            
            watch.start("processing");
            // Simulate processing
            Thread.sleep(200);
            watch.stop();
            
            watch.start("cleanup");
            // Simulate cleanup
            Thread.sleep(50);
            watch.stop();
            
            logger.info("Performance summary:\n{}", watch.prettyPrint());
        } catch (InterruptedException e) {
            logger.error("Operation interrupted", e);
            Thread.currentThread().interrupt();
        }
        
        // Log memory usage
        Runtime runtime = Runtime.getRuntime();
        long usedMemory = (runtime.totalMemory() - runtime.freeMemory()) / 1024 / 1024;
        logger.info("Memory usage: {}MB", usedMemory);
    }
    
    private static void demonstrateStructuredLogging() {
        // Create structured log entry
        Map<String, Object> logEntry = new HashMap<>();
        logEntry.put("timestamp", Instant.now());
        logEntry.put("level", "INFO");
        logEntry.put("event", "USER_LOGIN");
        logEntry.put("userId", "12345");
        logEntry.put("ipAddress", "192.168.1.1");
        logEntry.put("browser", "Chrome");
        
        // Log as JSON
        try {
            String json = new ObjectMapper().writeValueAsString(logEntry);
            logger.info("Structured log: {}", json);
        } catch (JsonProcessingException e) {
            logger.error("Error creating structured log", e);
        }
        
        // Using Markers for structured logging
        Marker confidential = MarkerFactory.getMarker("CONFIDENTIAL");
        confidential.add(MarkerFactory.getMarker("PII")); // Nested marker
        
        logger.info(confidential, "Processing sensitive data for user: {}", "john_doe");
    }
    
    private static void demonstrateConditionalLogging() {
        // Guard expensive operations
        if (logger.isTraceEnabled()) {
            logger.trace("Expensive trace: {}", generateDetailedDebugInfo());
        }
        
        // Log different levels based on condition
        int errorCount = 5;
        if (errorCount > 10) {
            logger.error("High error count: {}", errorCount);
        } else if (errorCount > 5) {
            logger.warn("Elevated error count: {}", errorCount);
        } else {
            logger.info("Normal error count: {}", errorCount);
        }
        
        // Using suppliers for lazy evaluation
        logger.debug("Expensive operation result: {}", 
            (Supplier<?>) () -> performExpensiveOperation());
    }
    
    private static void demonstrateLogLevels() {
        // TRACE - most detailed information
        logger.trace("Database query executed: SELECT * FROM users WHERE id = ?");
        
        // DEBUG - diagnostic information
        logger.debug("User authentication attempt from IP: {}", "192.168.1.1");
        
        // INFO - general information about application progress
        logger.info("Application started successfully");
        
        // WARN - potentially harmful situations
        logger.warn("Database connection pool at 80% capacity");
        
        // ERROR - error events that might still allow the application to continue
        logger.error("Failed to process transaction", new RuntimeException("Database timeout"));
        
        try {
            throw new OutOfMemoryError("Memory full");
        } catch (Error e) {
            // ERROR with fatal marker - severe errors that prevent normal operation
            logger.error(MarkerFactory.getMarker("FATAL"), "Critical system error", e);
        }
    }
    
    // Helper methods
    private static void processTask(String taskId) throws Exception {
        logger.debug("Processing task: {}", taskId);
        if (Math.random() < 0.5) {
            throw new Exception("Random task failure");
        }
    }
    
    private static String getCurrentState() {
        return "Current application state";
    }
    
    private static String generateDetailedState() {
        return "Detailed state information";
    }
    
    private static void processRequest() {
        logger.debug("Processing request");
    }
    
    private static String generateDetailedDebugInfo() {
        return "Detailed debug information";
    }
    
    private static String performExpensiveOperation() {
        return "Expensive operation result";
    }
} 