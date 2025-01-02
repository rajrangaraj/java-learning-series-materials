/**
 * Analyzes employee data using streams and functional operations
 */
public class EmployeeAnalytics {
    
    /**
     * Finds employees with salary above threshold grouped by department
     */
    public Map<String, List<Employee>> findHighEarnersByDepartment(
            List<Employee> employees, double threshold) {
        return employees.stream()
                       .filter(e -> e.getSalary() > threshold)
                       .collect(Collectors.groupingBy(Employee::getDepartment));
    }
    
    /**
     * Calculates average salary by department
     */
    public Map<String, Double> calculateAverageSalaryByDepartment(
            List<Employee> employees) {
        return employees.stream()
                       .collect(Collectors.groupingBy(
                           Employee::getDepartment,
                           Collectors.averagingDouble(Employee::getSalary)
                       ));
    }
    
    /**
     * Finds employees with specific skills
     */
    public List<Employee> findEmployeesWithSkills(
            List<Employee> employees, Set<String> requiredSkills) {
        return employees.stream()
                       .filter(e -> e.getSkills().containsAll(requiredSkills))
                       .collect(Collectors.toList());
    }
    
    /**
     * Finds most experienced employees by department
     */
    public Map<String, Employee> findMostExperiencedByDepartment(
            List<Employee> employees) {
        return employees.stream()
                       .collect(Collectors.groupingBy(
                           Employee::getDepartment,
                           Collectors.collectingAndThen(
                               Collectors.maxBy(Comparator.comparing(Employee::getTenure)),
                               opt -> opt.orElse(null)
                           )
                       ));
    }
    
    /**
     * Analyzes project allocation
     */
    public ProjectAllocationStats analyzeProjectAllocation(List<Employee> employees) {
        DoubleSummaryStatistics projectStats = employees.stream()
            .mapToDouble(e -> e.getProjects().size())
            .summaryStatistics();
            
        Map<String, Long> projectsByDepartment = employees.stream()
            .collect(Collectors.groupingBy(
                Employee::getDepartment,
                Collectors.summingLong(e -> e.getProjects().size())
            ));
            
        return new ProjectAllocationStats(
            projectStats.getAverage(),
            projectStats.getMax(),
            employees.stream()
                    .filter(e -> e.getProjects().isEmpty())
                    .count(),
            projectsByDepartment
        );
    }
    
    /**
     * Calculates total project budget by department
     */
    public Map<String, Double> calculateProjectBudgetByDepartment(
            List<Employee> employees) {
        return employees.stream()
                       .collect(Collectors.groupingBy(
                           Employee::getDepartment,
                           Collectors.summingDouble(e -> e.getProjects().stream()
                               .filter(Project::isActive)
                               .mapToDouble(Project::getBudget)
                               .sum())
                       ));
    }
    
    /**
     * Finds skill gaps in departments
     */
    public Map<String, Set<String>> findDepartmentSkillGaps(
            List<Employee> employees, Map<String, Set<String>> requiredSkills) {
        return employees.stream()
                       .collect(Collectors.groupingBy(
                           Employee::getDepartment,
                           Collectors.collectingAndThen(
                               Collectors.mapping(
                                   Employee::getSkills,
                                   Collectors.reducing(
                                       new HashSet<>(),
                                       (a, b) -> {
                                           Set<String> union = new HashSet<>(a);
                                           union.addAll(b);
                                           return union;
                                       }
                                   )
                               ),
                               skills -> {
                                   Set<String> required = requiredSkills.get(skills);
                                   if (required == null) return new HashSet<>();
                                   required.removeAll(skills);
                                   return required;
                               }
                           )
                       ));
    }
    
    /**
     * Generates comprehensive department report
     */
    public DepartmentReport generateDepartmentReport(
            String department, List<Employee> employees) {
        List<Employee> deptEmployees = employees.stream()
                                              .filter(e -> e.getDepartment().equals(department))
                                              .collect(Collectors.toList());
        
        return new DepartmentReport(
            department,
            deptEmployees.size(),
            deptEmployees.stream()
                        .mapToDouble(Employee::getSalary)
                        .average()
                        .orElse(0.0),
            deptEmployees.stream()
                        .flatMap(e -> e.getSkills().stream())
                        .collect(Collectors.toSet()),
            deptEmployees.stream()
                        .flatMap(e -> e.getProjects().stream())
                        .filter(Project::isActive)
                        .count(),
            deptEmployees.stream()
                        .mapToDouble(Employee::getTenure)
                        .average()
                        .orElse(0.0)
        );
    }
} 