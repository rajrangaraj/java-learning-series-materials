/**
 * Demonstrates exception handling in async operations
 */
public class AsyncExceptionHandling {
    private static final Logger logger = LoggerFactory.getLogger(AsyncExceptionHandling.class);
    private final ExecutorService executor;
    private final AsyncExceptionHandler exceptionHandler;
    
    public AsyncExceptionHandling(AsyncExceptionHandler exceptionHandler) {
        this.executor = Executors.newFixedThreadPool(4);
        this.exceptionHandler = exceptionHandler;
    }
    
    // CompletableFuture with exception handling
    public <T> CompletableFuture<T> executeAsync(Supplier<T> task) {
        return CompletableFuture.supplyAsync(task, executor)
            .exceptionally(throwable -> {
                exceptionHandler.handleException(throwable);
                return null;
            })
            .whenComplete((result, throwable) -> {
                if (throwable != null) {
                    logger.error("Task failed", throwable);
                }
            });
    }
    
    // Async task with retry
    public <T> CompletableFuture<T> executeWithRetry(Supplier<T> task, 
            int maxRetries, Duration delay) {
        return CompletableFuture.supplyAsync(() -> {
            int attempts = 0;
            while (true) {
                try {
                    return task.get();
                } catch (Exception e) {
                    attempts++;
                    if (attempts > maxRetries) {
                        throw new RetryExhaustedException("Max retries reached", e);
                    }
                    try {
                        Thread.sleep(delay.toMillis());
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                        throw new CompletionException(ie);
                    }
                    logger.warn("Retry attempt {} after error: {}", 
                        attempts, e.getMessage());
                }
            }
        }, executor);
    }
    
    // Circuit breaker pattern
    public <T> CompletableFuture<T> executeWithCircuitBreaker(
            Supplier<T> task, CircuitBreaker breaker) {
        return CompletableFuture.supplyAsync(() -> {
            if (!breaker.isAllowed()) {
                throw new CircuitBreakerOpenException(
                    "Circuit breaker is open");
            }
            try {
                T result = task.get();
                breaker.recordSuccess();
                return result;
            } catch (Exception e) {
                breaker.recordFailure();
                throw e;
            }
        }, executor);
    }
    
    // Async task with timeout
    public <T> CompletableFuture<T> executeWithTimeout(
            Supplier<T> task, Duration timeout) {
        CompletableFuture<T> future = CompletableFuture.supplyAsync(
            task, executor);
            
        return future.orTimeout(timeout.toMillis(), TimeUnit.MILLISECONDS)
            .exceptionally(throwable -> {
                if (throwable instanceof TimeoutException) {
                    logger.error("Task timed out after {}ms", 
                        timeout.toMillis());
                }
                throw new CompletionException(throwable);
            });
    }
    
    // Bulk operation with partial failure handling
    public <T> CompletableFuture<BulkResult<T>> executeBulk(
            List<Supplier<T>> tasks) {
        List<CompletableFuture<T>> futures = tasks.stream()
            .map(task -> CompletableFuture.supplyAsync(task, executor)
                .handle((result, throwable) -> {
                    if (throwable != null) {
                        return new TaskResult<>(null, throwable);
                    }
                    return new TaskResult<>(result, null);
                }))
            .collect(Collectors.toList());
            
        return CompletableFuture.allOf(
            futures.toArray(new CompletableFuture[0]))
            .thenApply(v -> {
                List<TaskResult<T>> results = futures.stream()
                    .map(CompletableFuture::join)
                    .collect(Collectors.toList());
                    
                return new BulkResult<>(results);
            });
    }
    
    private static class TaskResult<T> {
        final T result;
        final Throwable error;
        
        TaskResult(T result, Throwable error) {
            this.result = result;
            this.error = error;
        }
    }
    
    private static class BulkResult<T> {
        final List<TaskResult<T>> results;
        
        BulkResult(List<TaskResult<T>> results) {
            this.results = results;
        }
        
        public List<T> getSuccessfulResults() {
            return results.stream()
                .filter(r -> r.error == null)
                .map(r -> r.result)
                .collect(Collectors.toList());
        }
        
        public List<Throwable> getErrors() {
            return results.stream()
                .filter(r -> r.error != null)
                .map(r -> r.error)
                .collect(Collectors.toList());
        }
        
        public boolean hasErrors() {
            return results.stream().anyMatch(r -> r.error != null);
        }
    }
} 