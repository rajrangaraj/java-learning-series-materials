/**
 * Utility class for handling exceptions in functional code
 */
public class FunctionalExceptionHandler {
    
    @FunctionalInterface
    public interface ThrowingFunction<T, R> {
        R apply(T t) throws Exception;
    }
    
    @FunctionalInterface
    public interface ThrowingConsumer<T> {
        void accept(T t) throws Exception;
    }
    
    @FunctionalInterface
    public interface ThrowingSupplier<T> {
        T get() throws Exception;
    }
    
    public static <T, R> Function<T, Result<R>> lift(ThrowingFunction<T, R> f) {
        return t -> {
            try {
                return Result.success(f.apply(t));
            } catch (Exception e) {
                return Result.failure(e);
            }
        };
    }
    
    public static <T> Consumer<T> unchecked(ThrowingConsumer<T> consumer) {
        return t -> {
            try {
                consumer.accept(t);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        };
    }
    
    public static <T> Supplier<T> unchecked(ThrowingSupplier<T> supplier) {
        return () -> {
            try {
                return supplier.get();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        };
    }
    
    public static <T, R> Function<T, R> unchecked(ThrowingFunction<T, R> f) {
        return t -> {
            try {
                return f.apply(t);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        };
    }
    
    public static <T> Result<T> retry(ThrowingSupplier<T> supplier, 
            int maxAttempts, Duration delay) {
        Exception lastException = null;
        
        for (int attempt = 1; attempt <= maxAttempts; attempt++) {
            try {
                return Result.success(supplier.get());
            } catch (Exception e) {
                lastException = e;
                if (attempt < maxAttempts) {
                    try {
                        Thread.sleep(delay.toMillis() * attempt); // Exponential backoff
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                        return Result.failure(ie);
                    }
                }
            }
        }
        
        return Result.failure(lastException);
    }
    
    // Example usage
    public static void main(String[] args) {
        // Example 1: Handling checked exceptions in streams
        List<String> files = Arrays.asList("file1.txt", "file2.txt");
        
        List<Result<String>> contents = files.stream()
            .map(lift(Files::readString))
            .collect(Collectors.toList());
            
        // Example 2: Using retry mechanism
        Result<String> result = retry(
            () -> callExternalService(),
            3,
            Duration.ofSeconds(1)
        );
        
        // Example 3: Recovery and fallback
        String processed = result
            .map(String::trim)
            .flatMap(str -> Result.success(processString(str)))
            .recover(ex -> "fallback")
            .getOrElse("default");
    }
    
    private static String callExternalService() throws Exception {
        // Simulate external service call
        if (Math.random() < 0.5) {
            throw new IOException("Service unavailable");
        }
        return "response";
    }
    
    private static String processString(String input) {
        return input + "_processed";
    }
} 