/**
 * Custom thread pool implementation with work stealing
 */
public class CustomThreadPool implements ExecutorService {
    private final WorkerThread[] workers;
    private final BlockingDeque<Runnable>[] queues;
    private final AtomicBoolean isShutdown;
    private final ThreadPoolStats stats;
    private final Random random;
    
    @SuppressWarnings("unchecked")
    public CustomThreadPool(int numThreads) {
        if (numThreads < 1) throw new IllegalArgumentException("Thread count must be positive");
        
        this.workers = new WorkerThread[numThreads];
        this.queues = new BlockingDeque[numThreads];
        this.isShutdown = new AtomicBoolean(false);
        this.stats = new ThreadPoolStats();
        this.random = new Random();
        
        // Initialize queues and workers
        for (int i = 0; i < numThreads; i++) {
            queues[i] = new LinkedBlockingDeque<>();
            workers[i] = new WorkerThread(i);
            workers[i].start();
        }
    }
    
    @Override
    public void execute(Runnable command) {
        if (isShutdown.get()) {
            throw new RejectedExecutionException("ThreadPool is shutdown");
        }
        
        stats.recordSubmission();
        boolean added = false;
        
        // Try to add to a random queue first
        int index = random.nextInt(workers.length);
        if (queues[index].offer(command)) {
            added = true;
        } else {
            // Try other queues if the first one is full
            for (int i = 0; i < workers.length && !added; i++) {
                if (queues[i].offer(command)) {
                    added = true;
                }
            }
        }
        
        if (!added) {
            stats.recordRejection();
            throw new RejectedExecutionException("All queues are full");
        }
    }
    
    @Override
    public void shutdown() {
        if (isShutdown.compareAndSet(false, true)) {
            for (WorkerThread worker : workers) {
                worker.shutdown();
            }
        }
    }
    
    @Override
    public List<Runnable> shutdownNow() {
        shutdown();
        List<Runnable> remainingTasks = new ArrayList<>();
        for (BlockingDeque<Runnable> queue : queues) {
            queue.drainTo(remainingTasks);
        }
        return remainingTasks;
    }
    
    @Override
    public boolean isShutdown() {
        return isShutdown.get();
    }
    
    @Override
    public boolean isTerminated() {
        if (!isShutdown()) {
            return false;
        }
        for (WorkerThread worker : workers) {
            if (!worker.isTerminated()) {
                return false;
            }
        }
        return true;
    }
    
    @Override
    public boolean awaitTermination(long timeout, TimeUnit unit) 
            throws InterruptedException {
        long deadline = System.nanoTime() + unit.toNanos(timeout);
        for (WorkerThread worker : workers) {
            long remaining = deadline - System.nanoTime();
            if (remaining <= 0) {
                return false;
            }
            if (!worker.awaitTermination(remaining, TimeUnit.NANOSECONDS)) {
                return false;
            }
        }
        return true;
    }
    
    public ThreadPoolStats getStats() {
        return stats;
    }
    
    // Worker thread implementation
    private class WorkerThread extends Thread {
        private final int index;
        private final AtomicBoolean terminated;
        private final CountDownLatch terminationLatch;
        
        public WorkerThread(int index) {
            super("Worker-" + index);
            this.index = index;
            this.terminated = new AtomicBoolean(false);
            this.terminationLatch = new CountDownLatch(1);
        }
        
        @Override
        public void run() {
            try {
                while (!terminated.get() || !queues[index].isEmpty()) {
                    Runnable task = getNextTask();
                    if (task != null) {
                        try {
                            stats.recordTaskStart();
                            long startTime = System.nanoTime();
                            task.run();
                            stats.recordTaskCompletion(System.nanoTime() - startTime);
                        } catch (Exception e) {
                            stats.recordTaskFailure();
                        }
                    }
                }
            } finally {
                terminationLatch.countDown();
            }
        }
        
        private Runnable getNextTask() {
            try {
                // First try own queue
                Runnable task = queues[index].pollFirst(100, TimeUnit.MILLISECONDS);
                if (task != null) {
                    return task;
                }
                
                // Then try stealing from other queues
                if (!terminated.get()) {
                    for (int i = 0; i < queues.length; i++) {
                        if (i != index) {
                            task = queues[i].pollLast();
                            if (task != null) {
                                stats.recordTaskSteal();
                                return task;
                            }
                        }
                    }
                }
                
                return null;
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return null;
            }
        }
        
        public void shutdown() {
            terminated.set(true);
        }
        
        public boolean isTerminated() {
            return terminationLatch.getCount() == 0;
        }
        
        public boolean awaitTermination(long timeout, TimeUnit unit) 
                throws InterruptedException {
            return terminationLatch.await(timeout, unit);
        }
    }
    
    // Future-related methods
    @Override
    public <T> Future<T> submit(Callable<T> task) {
        CustomFuture<T> future = new CustomFuture<>(task);
        execute(future);
        return future;
    }
    
    @Override
    public <T> Future<T> submit(Runnable task, T result) {
        CustomFuture<T> future = new CustomFuture<>(task, result);
        execute(future);
        return future;
    }
    
    @Override
    public Future<?> submit(Runnable task) {
        return submit(task, null);
    }
    
    @Override
    public <T> List<Future<T>> invokeAll(Collection<? extends Callable<T>> tasks)
            throws InterruptedException {
        List<Future<T>> futures = new ArrayList<>(tasks.size());
        for (Callable<T> task : tasks) {
            futures.add(submit(task));
        }
        
        for (Future<T> future : futures) {
            try {
                future.get();
            } catch (ExecutionException e) {
                // Ignore execution exceptions
            }
        }
        
        return futures;
    }
    
    @Override
    public <T> List<Future<T>> invokeAll(Collection<? extends Callable<T>> tasks,
                                        long timeout, TimeUnit unit)
            throws InterruptedException {
        long deadline = System.nanoTime() + unit.toNanos(timeout);
        List<Future<T>> futures = new ArrayList<>(tasks.size());
        
        try {
            for (Callable<T> task : tasks) {
                futures.add(submit(task));
            }
            
            for (Future<T> future : futures) {
                long remaining = deadline - System.nanoTime();
                if (remaining <= 0) {
                    for (Future<T> f : futures) {
                        f.cancel(true);
                    }
                    throw new InterruptedException("Timeout elapsed");
                }
                try {
                    future.get(remaining, TimeUnit.NANOSECONDS);
                } catch (ExecutionException | TimeoutException e) {
                    // Ignore execution and timeout exceptions
                }
            }
            
            return futures;
        } catch (InterruptedException e) {
            for (Future<T> future : futures) {
                future.cancel(true);
            }
            throw e;
        }
    }
    
    @Override
    public <T> T invokeAny(Collection<? extends Callable<T>> tasks)
            throws InterruptedException, ExecutionException {
        try {
            return doInvokeAny(tasks, false, 0);
        } catch (TimeoutException e) {
            throw new IllegalStateException("Unexpected TimeoutException");
        }
    }
    
    @Override
    public <T> T invokeAny(Collection<? extends Callable<T>> tasks,
                          long timeout, TimeUnit unit)
            throws InterruptedException, ExecutionException, TimeoutException {
        return doInvokeAny(tasks, true, unit.toNanos(timeout));
    }
    
    private <T> T doInvokeAny(Collection<? extends Callable<T>> tasks,
                             boolean timed, long nanos)
            throws InterruptedException, ExecutionException, TimeoutException {
        List<Future<T>> futures = new ArrayList<>(tasks.size());
        ExecutionException ee = null;
        
        try {
            long deadline = timed ? System.nanoTime() + nanos : 0;
            
            for (Callable<T> task : tasks) {
                futures.add(submit(task));
            }
            
            int remaining = futures.size();
            while (remaining > 0) {
                for (Future<T> future : futures) {
                    if (!future.isDone()) continue;
                    
                    try {
                        return future.get();
                    } catch (ExecutionException e) {
                        ee = e;
                        remaining--;
                    }
                }
                
                if (timed && System.nanoTime() > deadline) {
                    throw new TimeoutException();
                }
                
                Thread.yield();
            }
            
            throw ee != null ? ee : new ExecutionException("No task completed", null);
        } finally {
            for (Future<T> future : futures) {
                future.cancel(true);
            }
        }
    }
} 