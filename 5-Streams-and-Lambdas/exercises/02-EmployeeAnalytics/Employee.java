/**
 * Represents an employee with various attributes
 */
public class Employee {
    private final String id;
    private final String name;
    private final String department;
    private final double salary;
    private final LocalDate hireDate;
    private final Set<String> skills;
    private final List<Project> projects;
    
    public Employee(String id, String name, String department, double salary, 
                   LocalDate hireDate, Set<String> skills) {
        this.id = id;
        this.name = name;
        this.department = department;
        this.salary = salary;
        this.hireDate = hireDate;
        this.skills = new HashSet<>(skills);
        this.projects = new ArrayList<>();
    }
    
    // Getters
    public String getId() { return id; }
    public String getName() { return name; }
    public String getDepartment() { return department; }
    public double getSalary() { return salary; }
    public LocalDate getHireDate() { return hireDate; }
    public Set<String> getSkills() { return Collections.unmodifiableSet(skills); }
    public List<Project> getProjects() { return Collections.unmodifiableList(projects); }
    
    // Project management
    public void addProject(Project project) {
        projects.add(project);
    }
    
    public void removeProject(Project project) {
        projects.remove(project);
    }
    
    // Utility methods
    public long getTenure() {
        return ChronoUnit.YEARS.between(hireDate, LocalDate.now());
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Employee)) return false;
        Employee employee = (Employee) o;
        return id.equals(employee.id);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
    
    @Override
    public String toString() {
        return String.format("Employee{id='%s', name='%s', department='%s', salary=%.2f}",
            id, name, department, salary);
    }
} 