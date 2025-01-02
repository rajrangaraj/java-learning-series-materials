/**
 * Demonstrates various thread pool implementations and executors
 */
public class ThreadPoolExamples {
    
    public static void main(String[] args) {
        demonstrateFixedThreadPool();
        demonstrateCachedThreadPool();
        demonstrateScheduledThreadPool();
        demonstrateCustomThreadPool();
        demonstrateWorkStealingPool();
    }
    
    private static void demonstrateFixedThreadPool() {
        System.out.println("\nFixed Thread Pool Example:");
        ExecutorService executor = Executors.newFixedThreadPool(3);
        
        try {
            for (int i = 0; i < 5; i++) {
                final int taskId = i;
                executor.submit(() -> {
                    System.out.printf("Task %d executing on thread %s%n", 
                        taskId, Thread.currentThread().getName());
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                    return "Task " + taskId + " completed";
                });
            }
        } finally {
            shutdownAndAwaitTermination(executor);
        }
    }
    
    private static void demonstrateCachedThreadPool() {
        System.out.println("\nCached Thread Pool Example:");
        ExecutorService executor = Executors.newCachedThreadPool();
        
        try {
            List<Future<String>> futures = new ArrayList<>();
            
            for (int i = 0; i < 5; i++) {
                final int taskId = i;
                futures.add(executor.submit(() -> {
                    System.out.printf("Task %d executing on thread %s%n", 
                        taskId, Thread.currentThread().getName());
                    Thread.sleep(100);
                    return "Task " + taskId + " completed";
                }));
            }
            
            for (Future<String> future : futures) {
                try {
                    System.out.println(future.get());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } finally {
            shutdownAndAwaitTermination(executor);
        }
    }
    
    private static void demonstrateScheduledThreadPool() {
        System.out.println("\nScheduled Thread Pool Example:");
        ScheduledExecutorService executor = Executors.newScheduledThreadPool(2);
        
        try {
            // Schedule task to run after delay
            ScheduledFuture<?> future1 = executor.schedule(
                () -> System.out.println("Delayed task executed"),
                2, TimeUnit.SECONDS
            );
            
            // Schedule task to run periodically
            ScheduledFuture<?> future2 = executor.scheduleAtFixedRate(
                () -> System.out.println("Periodic task executed"),
                0, 1, TimeUnit.SECONDS
            );
            
            Thread.sleep(5000); // Let tasks run for 5 seconds
            future2.cancel(false);
            
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } finally {
            shutdownAndAwaitTermination(executor);
        }
    }
    
    private static void demonstrateCustomThreadPool() {
        System.out.println("\nCustom Thread Pool Example:");
        ThreadPoolExecutor executor = new ThreadPoolExecutor(
            2, // core pool size
            4, // maximum pool size
            60L, TimeUnit.SECONDS, // keep alive time
            new ArrayBlockingQueue<>(2), // work queue
            new CustomThreadFactory(), // thread factory
            new CustomRejectionHandler() // rejection handler
        );
        
        try {
            for (int i = 0; i < 8; i++) {
                final int taskId = i;
                try {
                    executor.submit(() -> {
                        System.out.printf("Task %d executing on thread %s%n", 
                            taskId, Thread.currentThread().getName());
                        Thread.sleep(100);
                        return taskId;
                    });
                } catch (RejectedExecutionException e) {
                    System.out.println("Task " + taskId + " was rejected");
                }
            }
        } finally {
            shutdownAndAwaitTermination(executor);
        }
    }
    
    private static void demonstrateWorkStealingPool() {
        System.out.println("\nWork Stealing Pool Example:");
        ExecutorService executor = Executors.newWorkStealingPool();
        
        try {
            List<Callable<String>> tasks = IntStream.range(0, 10)
                .mapToObj(i -> (Callable<String>) () -> {
                    System.out.printf("Task %d executing on thread %s%n", 
                        i, Thread.currentThread().getName());
                    Thread.sleep(100);
                    return "Task " + i + " completed";
                })
                .collect(Collectors.toList());
            
            List<Future<String>> futures = executor.invokeAll(tasks);
            
            for (Future<String> future : futures) {
                System.out.println(future.get());
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            shutdownAndAwaitTermination(executor);
        }
    }
    
    private static void shutdownAndAwaitTermination(ExecutorService executor) {
        executor.shutdown();
        try {
            if (!executor.awaitTermination(60, TimeUnit.SECONDS)) {
                executor.shutdownNow();
                if (!executor.awaitTermination(60, TimeUnit.SECONDS))
                    System.err.println("Executor did not terminate");
            }
        } catch (InterruptedException ie) {
            executor.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }
    
    static class CustomThreadFactory implements ThreadFactory {
        private final AtomicInteger threadNumber = new AtomicInteger(1);
        
        @Override
        public Thread newThread(Runnable r) {
            Thread thread = new Thread(r, "CustomThread-" + threadNumber.getAndIncrement());
            thread.setDaemon(true);
            return thread;
        }
    }
    
    static class CustomRejectionHandler implements RejectedExecutionHandler {
        @Override
        public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
            System.out.println("Task rejected: " + r.toString());
        }
    }
} 