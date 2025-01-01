/**
 * Generic task scheduler with priority and dependency management
 */
public class TaskScheduler<T> {
    private final PriorityQueue<Task<T>> taskQueue;
    private final Map<Task<T>, Set<Task<T>>> dependencyGraph;
    private final ExecutorService executor;
    private final TaskStats stats;
    
    public TaskScheduler(int threadCount) {
        this.taskQueue = new PriorityQueue<>();
        this.dependencyGraph = new ConcurrentHashMap<>();
        this.executor = Executors.newFixedThreadPool(threadCount);
        this.stats = new TaskStats();
    }
    
    public void schedule(Task<T> task) {
        dependencyGraph.put(task, new HashSet<>());
        if (task.areDependenciesComplete()) {
            taskQueue.offer(task);
        }
        stats.recordScheduled();
    }
    
    public void addDependency(Task<T> task, Task<T> dependency) {
        dependencyGraph.computeIfAbsent(task, k -> new HashSet<>()).add(dependency);
        task.addDependency(dependency);
    }
    
    public void start() {
        while (!taskQueue.isEmpty()) {
            Task<T> task = taskQueue.poll();
            if (task.areDependenciesComplete()) {
                executeTask(task);
            } else {
                // Re-queue with lower priority
                taskQueue.offer(task);
            }
        }
    }
    
    private void executeTask(Task<T> task) {
        executor.submit(() -> {
            try {
                task.setStatus(TaskStatus.RUNNING);
                stats.recordStarted();
                
                // Simulate task execution
                processTask(task);
                
                task.setStatus(TaskStatus.COMPLETED);
                stats.recordCompleted();
                
                // Check dependent tasks
                updateDependentTasks(task);
                
            } catch (Exception e) {
                task.setStatus(TaskStatus.FAILED);
                stats.recordFailed();
            }
        });
    }
    
    private void processTask(Task<T> task) {
        // Implement actual task processing here
        try {
            Thread.sleep(100); // Simulate work
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
    
    private void updateDependentTasks(Task<T> completedTask) {
        dependencyGraph.forEach((task, dependencies) -> {
            if (dependencies.contains(completedTask) && task.areDependenciesComplete()) {
                taskQueue.offer(task);
            }
        });
    }
    
    public TaskStats getStats() {
        return stats;
    }
    
    public void shutdown() {
        executor.shutdown();
    }
} 