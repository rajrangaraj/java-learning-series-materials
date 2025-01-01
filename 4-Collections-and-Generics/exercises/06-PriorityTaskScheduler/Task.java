/**
 * Generic task class with priority support
 */
public class Task<T> implements Comparable<Task<T>> {
    private final T data;
    private final int priority;
    private final long creationTime;
    private final Set<Task<T>> dependencies;
    private TaskStatus status;
    
    public Task(T data, int priority) {
        this.data = data;
        this.priority = priority;
        this.creationTime = System.currentTimeMillis();
        this.dependencies = new HashSet<>();
        this.status = TaskStatus.PENDING;
    }
    
    public T getData() { return data; }
    public int getPriority() { return priority; }
    public long getCreationTime() { return creationTime; }
    public TaskStatus getStatus() { return status; }
    
    public void setStatus(TaskStatus status) {
        this.status = status;
    }
    
    public void addDependency(Task<T> task) {
        dependencies.add(task);
    }
    
    public boolean areDependenciesComplete() {
        return dependencies.stream()
            .allMatch(task -> task.getStatus() == TaskStatus.COMPLETED);
    }
    
    @Override
    public int compareTo(Task<T> other) {
        return Integer.compare(other.priority, this.priority); // Higher priority first
    }
} 