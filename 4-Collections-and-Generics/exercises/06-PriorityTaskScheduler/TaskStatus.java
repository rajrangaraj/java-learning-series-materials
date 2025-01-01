/**
 * Enum representing possible task states
 */
public enum TaskStatus {
    PENDING("Task is waiting to be executed"),
    RUNNING("Task is currently executing"),
    COMPLETED("Task has completed successfully"),
    FAILED("Task execution failed");
    
    private final String description;
    
    TaskStatus(String description) {
        this.description = description;
    }
    
    public String getDescription() {
        return description;
    }
} 