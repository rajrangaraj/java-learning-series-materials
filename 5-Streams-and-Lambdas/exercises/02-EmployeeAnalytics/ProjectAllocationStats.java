/**
 * Statistics about project allocations
 */
public class ProjectAllocationStats {
    private final double averageProjectsPerEmployee;
    private final double maxProjectsPerEmployee;
    private final long unallocatedEmployees;
    private final Map<String, Long> projectsByDepartment;
    
    public ProjectAllocationStats(double averageProjectsPerEmployee,
                                double maxProjectsPerEmployee,
                                long unallocatedEmployees,
                                Map<String, Long> projectsByDepartment) {
        this.averageProjectsPerEmployee = averageProjectsPerEmployee;
        this.maxProjectsPerEmployee = maxProjectsPerEmployee;
        this.unallocatedEmployees = unallocatedEmployees;
        this.projectsByDepartment = new HashMap<>(projectsByDepartment);
    }
    
    // Getters
    public double getAverageProjectsPerEmployee() { 
        return averageProjectsPerEmployee; 
    }
    
    public double getMaxProjectsPerEmployee() { 
        return maxProjectsPerEmployee; 
    }
    
    public long getUnallocatedEmployees() { 
        return unallocatedEmployees; 
    }
    
    public Map<String, Long> getProjectsByDepartment() { 
        return Collections.unmodifiableMap(projectsByDepartment); 
    }
    
    @Override
    public String toString() {
        return String.format(
            "ProjectAllocationStats{avg=%.2f, max=%.2f, unallocated=%d, byDept=%s}",
            averageProjectsPerEmployee, maxProjectsPerEmployee,
            unallocatedEmployees, projectsByDepartment);
    }
} 