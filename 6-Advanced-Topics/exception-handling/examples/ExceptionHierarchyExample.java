/**
 * Demonstrates custom exception hierarchy and handling patterns
 */
public class ExceptionHierarchyExample {
    
    public static void main(String[] args) {
        demonstrateExceptionHierarchy();
        demonstrateExceptionTranslation();
        demonstrateExceptionEnrichment();
        demonstrateExceptionHandlingPatterns();
    }
    
    // Base exception for all application exceptions
    public static class ApplicationException extends Exception {
        public ApplicationException(String message) {
            super(message);
        }
        
        public ApplicationException(String message, Throwable cause) {
            super(message, cause);
        }
    }
    
    // Domain-specific exceptions
    public static class ValidationException extends ApplicationException {
        private final Map<String, String> validationErrors;
        
        public ValidationException(String message, Map<String, String> errors) {
            super(message);
            this.validationErrors = new HashMap<>(errors);
        }
        
        public Map<String, String> getValidationErrors() {
            return Collections.unmodifiableMap(validationErrors);
        }
    }
    
    public static class BusinessException extends ApplicationException {
        private final String errorCode;
        
        public BusinessException(String message, String errorCode) {
            super(message);
            this.errorCode = errorCode;
        }
        
        public String getErrorCode() {
            return errorCode;
        }
    }
    
    // Technical exceptions
    public static class DatabaseException extends ApplicationException {
        private final String sqlState;
        
        public DatabaseException(String message, String sqlState, Throwable cause) {
            super(message, cause);
            this.sqlState = sqlState;
        }
        
        public String getSqlState() {
            return sqlState;
        }
    }
    
    public static class ServiceException extends ApplicationException {
        private final int statusCode;
        
        public ServiceException(String message, int statusCode, Throwable cause) {
            super(message, cause);
            this.statusCode = statusCode;
        }
        
        public int getStatusCode() {
            return statusCode;
        }
    }
    
    private static void demonstrateExceptionHierarchy() {
        System.out.println("\n=== Exception Hierarchy ===");
        
        try {
            // Simulate user registration
            registerUser(new User("", "invalid@email", -25));
        } catch (ValidationException e) {
            System.out.println("Validation failed: " + e.getMessage());
            e.getValidationErrors().forEach((field, error) -> 
                System.out.println(field + ": " + error));
        } catch (BusinessException e) {
            System.out.println("Business rule violated: " + e.getMessage());
            System.out.println("Error code: " + e.getErrorCode());
        } catch (ApplicationException e) {
            System.out.println("Application error: " + e.getMessage());
        }
    }
    
    private static void demonstrateExceptionTranslation() {
        System.out.println("\n=== Exception Translation ===");
        
        try {
            // Simulate database operation
            saveUser(new User("john", "john@example.com", 30));
        } catch (DatabaseException e) {
            System.out.println("Database error: " + e.getMessage());
            System.out.println("SQL State: " + e.getSqlState());
            System.out.println("Original error: " + e.getCause().getMessage());
        } catch (ApplicationException e) {
            System.out.println("Application error: " + e.getMessage());
        }
    }
    
    private static void demonstrateExceptionEnrichment() {
        System.out.println("\n=== Exception Enrichment ===");
        
        try {
            // Simulate external service call
            processUserRequest("invalid-request");
        } catch (ServiceException e) {
            System.out.println("Service error: " + e.getMessage());
            System.out.println("Status code: " + e.getStatusCode());
            if (e.getCause() != null) {
                System.out.println("Caused by: " + e.getCause().getMessage());
            }
        } catch (ApplicationException e) {
            System.out.println("Application error: " + e.getMessage());
        }
    }
    
    private static void demonstrateExceptionHandlingPatterns() {
        System.out.println("\n=== Exception Handling Patterns ===");
        
        // Pattern 1: Exception aggregation
        List<String> userIds = Arrays.asList("user1", "user2", "user3");
        Map<String, Exception> errors = new HashMap<>();
        
        for (String userId : userIds) {
            try {
                processUser(userId);
            } catch (Exception e) {
                errors.put(userId, e);
            }
        }
        
        if (!errors.isEmpty()) {
            System.out.println("Some operations failed:");
            errors.forEach((userId, error) -> 
                System.out.println(userId + ": " + error.getMessage()));
        }
        
        // Pattern 2: Retry with exponential backoff
        int maxRetries = 3;
        long initialDelay = 1000L;
        
        for (int attempt = 1; attempt <= maxRetries; attempt++) {
            try {
                unreliableOperation();
                break;
            } catch (Exception e) {
                if (attempt == maxRetries) {
                    System.out.println("Operation failed after " + maxRetries + " attempts");
                    break;
                }
                long delay = initialDelay * (long) Math.pow(2, attempt - 1);
                System.out.println("Attempt " + attempt + " failed, retrying in " + delay + "ms");
                try {
                    Thread.sleep(delay);
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        }
        
        // Pattern 3: Circuit breaker
        CircuitBreaker breaker = new CircuitBreaker(3, Duration.ofSeconds(10));
        try {
            breaker.execute(() -> riskyOperation());
        } catch (Exception e) {
            System.out.println("Circuit breaker: " + e.getMessage());
        }
    }
    
    // Helper classes and methods
    static class User {
        String name;
        String email;
        int age;
        
        User(String name, String email, int age) {
            this.name = name;
            this.email = email;
            this.age = age;
        }
    }
    
    private static void registerUser(User user) throws ApplicationException {
        Map<String, String> errors = new HashMap<>();
        
        if (user.name.isEmpty()) {
            errors.put("name", "Name is required");
        }
        if (!user.email.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            errors.put("email", "Invalid email format");
        }
        if (user.age < 0) {
            errors.put("age", "Age cannot be negative");
        }
        
        if (!errors.isEmpty()) {
            throw new ValidationException("Invalid user data", errors);
        }
        
        if (user.age < 18) {
            throw new BusinessException("User must be 18 or older", "AGE_RESTRICTION");
        }
    }
    
    private static void saveUser(User user) throws ApplicationException {
        try {
            throw new SQLException("Database connection failed", "SQL-28000");
        } catch (SQLException e) {
            throw new DatabaseException("Failed to save user", e.getSQLState(), e);
        }
    }
    
    private static void processUserRequest(String request) throws ApplicationException {
        try {
            if ("invalid-request".equals(request)) {
                throw new IOException("Invalid request format");
            }
        } catch (IOException e) {
            throw new ServiceException("Failed to process request", 400, e);
        }
    }
    
    private static void processUser(String userId) throws Exception {
        if ("user2".equals(userId)) {
            throw new Exception("Failed to process user: " + userId);
        }
    }
    
    private static void unreliableOperation() throws Exception {
        if (Math.random() < 0.7) {
            throw new Exception("Operation failed");
        }
    }
    
    private static void riskyOperation() throws Exception {
        throw new Exception("Risky operation failed");
    }
    
    // Simple circuit breaker implementation
    static class CircuitBreaker {
        private final int failureThreshold;
        private final Duration resetTimeout;
        private int failures;
        private LocalDateTime lastFailure;
        
        public CircuitBreaker(int failureThreshold, Duration resetTimeout) {
            this.failureThreshold = failureThreshold;
            this.resetTimeout = resetTimeout;
        }
        
        public void execute(Runnable operation) throws Exception {
            if (isOpen()) {
                throw new Exception("Circuit breaker is open");
            }
            
            try {
                operation.run();
                reset();
            } catch (Exception e) {
                recordFailure();
                throw e;
            }
        }
        
        private boolean isOpen() {
            if (failures >= failureThreshold) {
                if (lastFailure.plus(resetTimeout).isBefore(LocalDateTime.now())) {
                    reset();
                    return false;
                }
                return true;
            }
            return false;
        }
        
        private void recordFailure() {
            failures++;
            lastFailure = LocalDateTime.now();
        }
        
        private void reset() {
            failures = 0;
            lastFailure = null;
        }
    }
} 