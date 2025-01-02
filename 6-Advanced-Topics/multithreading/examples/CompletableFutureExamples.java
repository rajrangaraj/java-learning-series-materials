/**
 * Demonstrates CompletableFuture usage patterns
 */
public class CompletableFutureExamples {
    
    public static void main(String[] args) {
        demonstrateBasicOperations();
        demonstrateChaining();
        demonstrateCombining();
        demonstrateErrorHandling();
        demonstrateAsyncOperations();
    }
    
    private static void demonstrateBasicOperations() {
        System.out.println("\nBasic CompletableFuture Operations:");
        
        // Create completed future
        CompletableFuture<String> completed = CompletableFuture.completedFuture("Done");
        System.out.println("Completed future: " + completed.join());
        
        // Create and complete manually
        CompletableFuture<String> manual = new CompletableFuture<>();
        manual.complete("Manually completed");
        System.out.println("Manual future: " + manual.join());
        
        // Run async computation
        CompletableFuture<String> async = CompletableFuture.supplyAsync(() -> {
            try {
                Thread.sleep(100);
                return "Async result";
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return "Interrupted";
            }
        });
        System.out.println("Async future: " + async.join());
    }
    
    private static void demonstrateChaining() {
        System.out.println("\nChaining Operations:");
        
        CompletableFuture.supplyAsync(() -> "Hello")
            .thenApply(String::toUpperCase)
            .thenApply(s -> s + " World")
            .thenAccept(System.out::println)
            .join();
        
        CompletableFuture.supplyAsync(() -> 1)
            .thenApply(i -> i * 2)
            .thenApply(i -> i + 1)
            .thenAccept(result -> System.out.println("Result: " + result))
            .join();
    }
    
    private static void demonstrateCombining() {
        System.out.println("\nCombining Futures:");
        
        CompletableFuture<String> future1 = CompletableFuture.supplyAsync(() -> {
            sleep(100);
            return "Hello";
        });
        
        CompletableFuture<String> future2 = CompletableFuture.supplyAsync(() -> {
            sleep(50);
            return "World";
        });
        
        // Combine results
        future1.thenCombine(future2, (s1, s2) -> s1 + " " + s2)
               .thenAccept(System.out::println)
               .join();
        
        // Run after both complete
        CompletableFuture.allOf(future1, future2)
                        .thenRun(() -> System.out.println("Both completed"))
                        .join();
        
        // Get first result
        CompletableFuture.anyOf(
            CompletableFuture.supplyAsync(() -> {
                sleep(100);
                return "Slow";
            }),
            CompletableFuture.supplyAsync(() -> {
                sleep(50);
                return "Fast";
            })
        ).thenAccept(System.out::println)
         .join();
    }
    
    private static void demonstrateErrorHandling() {
        System.out.println("\nError Handling:");
        
        // Handle exception
        CompletableFuture.supplyAsync(() -> {
            if (true) throw new RuntimeException("Oops!");
            return "Success";
        })
        .exceptionally(throwable -> "Error: " + throwable.getMessage())
        .thenAccept(System.out::println)
        .join();
        
        // Handle both success and failure
        CompletableFuture.supplyAsync(() -> {
            if (Math.random() > 0.5) throw new RuntimeException("Random failure");
            return "Success";
        })
        .handle((result, throwable) -> 
            throwable != null ? "Error: " + throwable.getMessage() : result)
        .thenAccept(System.out::println)
        .join();
        
        // Recover with another future
        CompletableFuture.supplyAsync(() -> {
            throw new RuntimeException("Primary failed");
        })
        .exceptionallyCompose(throwable -> 
            CompletableFuture.completedFuture("Backup result"))
        .thenAccept(System.out::println)
        .join();
    }
    
    private static void demonstrateAsyncOperations() {
        System.out.println("\nAsync Operations:");
        
        ExecutorService executor = Executors.newFixedThreadPool(2);
        
        try {
            // Custom executor
            CompletableFuture.supplyAsync(() -> {
                System.out.println("Running in: " + Thread.currentThread().getName());
                return "Custom executor result";
            }, executor)
            .thenAcceptAsync(result -> {
                System.out.println("Processing in: " + Thread.currentThread().getName());
                System.out.println(result);
            }, executor)
            .join();
            
            // Async composition
            CompletableFuture.supplyAsync(() -> {
                sleep(100);
                return "First";
            })
            .thenComposeAsync(s -> CompletableFuture.supplyAsync(() -> {
                sleep(50);
                return s + " Second";
            }))
            .thenAccept(System.out::println)
            .join();
            
        } finally {
            executor.shutdown();
        }
    }
    
    private static void sleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
} 