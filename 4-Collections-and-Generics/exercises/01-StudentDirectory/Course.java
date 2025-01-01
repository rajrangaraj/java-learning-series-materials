/**
 * Represents a course with its basic information
 */
public class Course {
    private final String code;
    private final String name;
    private final int credits;
    
    public Course(String code, String name, int credits) {
        this.code = code;
        this.name = name;
        this.credits = credits;
    }
    
    // Getters
    public String getCode() { return code; }
    public String getName() { return name; }
    public int getCredits() { return credits; }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Course)) return false;
        Course course = (Course) o;
        return code.equals(course.code);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(code);
    }
    
    @Override
    public String toString() {
        return String.format("Course{code='%s', name='%s', credits=%d}",
            code, name, credits);
    }
} 