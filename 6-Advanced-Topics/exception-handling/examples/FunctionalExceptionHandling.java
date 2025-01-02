/**
 * Demonstrates functional exception handling patterns
 */
public class FunctionalExceptionHandling {
    
    // Wrap checked exceptions in unchecked for functional interfaces
    @FunctionalInterface
    interface ThrowingFunction<T, R> {
        R apply(T t) throws Exception;
        
        static <T, R> Function<T, R> unchecked(ThrowingFunction<T, R> f) {
            return t -> {
                try {
                    return f.apply(t);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            };
        }
    }
    
    // Try monad for functional error handling
    public static class Try<T> {
        private final T value;
        private final Exception error;
        
        private Try(T value, Exception error) {
            this.value = value;
            this.error = error;
        }
        
        public static <T> Try<T> success(T value) {
            return new Try<>(value, null);
        }
        
        public static <T> Try<T> failure(Exception error) {
            return new Try<>(null, error);
        }
        
        public static <T> Try<T> of(ThrowingSupplier<T> supplier) {
            try {
                return success(supplier.get());
            } catch (Exception e) {
                return failure(e);
            }
        }
        
        public <R> Try<R> map(Function<T, R> mapper) {
            if (error != null) return failure(error);
            try {
                return success(mapper.apply(value));
            } catch (Exception e) {
                return failure(e);
            }
        }
        
        public <R> Try<R> flatMap(Function<T, Try<R>> mapper) {
            if (error != null) return failure(error);
            try {
                return mapper.apply(value);
            } catch (Exception e) {
                return failure(e);
            }
        }
        
        public T getOrElse(T defaultValue) {
            return error == null ? value : defaultValue;
        }
        
        public T getOrThrow() throws Exception {
            if (error != null) throw error;
            return value;
        }
    }
    
    // Example usage
    public static void main(String[] args) {
        // Using ThrowingFunction
        List<String> files = Arrays.asList("file1.txt", "file2.txt");
        
        List<String> contents = files.stream()
            .map(ThrowingFunction.unchecked(Files::readString))
            .collect(Collectors.toList());
            
        // Using Try monad
        Try<String> result = Try.of(() -> Files.readString(Path.of("file.txt")))
            .map(String::trim)
            .map(String::toUpperCase)
            .flatMap(str -> Try.of(() -> processString(str)));
            
        // Handle result
        String processed = result.getOrElse("default");
    }
    
    private static String processString(String input) throws Exception {
        if (input.isEmpty()) {
            throw new IllegalArgumentException("Input cannot be empty");
        }
        return input + "_processed";
    }
} 