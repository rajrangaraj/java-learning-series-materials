/**
 * Represents a project with its details
 */
public class Project {
    private final String id;
    private final String name;
    private final LocalDate startDate;
    private final LocalDate endDate;
    private final String status;
    private final double budget;
    
    public Project(String id, String name, LocalDate startDate, 
                  LocalDate endDate, String status, double budget) {
        this.id = id;
        this.name = name;
        this.startDate = startDate;
        this.endDate = endDate;
        this.status = status;
        this.budget = budget;
    }
    
    // Getters
    public String getId() { return id; }
    public String getName() { return name; }
    public LocalDate getStartDate() { return startDate; }
    public LocalDate getEndDate() { return endDate; }
    public String getStatus() { return status; }
    public double getBudget() { return budget; }
    
    // Utility methods
    public long getDuration() {
        return ChronoUnit.DAYS.between(startDate, 
            endDate != null ? endDate : LocalDate.now());
    }
    
    public boolean isActive() {
        return "ACTIVE".equalsIgnoreCase(status);
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Project)) return false;
        Project project = (Project) o;
        return id.equals(project.id);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
    
    @Override
    public String toString() {
        return String.format("Project{id='%s', name='%s', status='%s', budget=%.2f}",
            id, name, status, budget);
    }
} 