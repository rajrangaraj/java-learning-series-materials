/**
 * Represents a student with their basic information and courses
 */
public class Student {
    private final String id;
    private String name;
    private double grade;
    private final Set<Course> courses;
    
    public Student(String id, String name, double grade) {
        this.id = id;
        this.name = name;
        this.grade = grade;
        this.courses = new HashSet<>();
    }
    
    // Getters and Setters
    public String getId() { return id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public double getGrade() { return grade; }
    public void setGrade(double grade) { this.grade = grade; }
    public Set<Course> getCourses() { 
        return Collections.unmodifiableSet(courses); 
    }
    
    // Course management
    public void enrollCourse(Course course) {
        courses.add(course);
    }
    
    public void dropCourse(Course course) {
        courses.remove(course);
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Student)) return false;
        Student student = (Student) o;
        return id.equals(student.id);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
    
    @Override
    public String toString() {
        return String.format("Student{id='%s', name='%s', grade=%.2f, courses=%s}",
            id, name, grade, courses);
    }
} 