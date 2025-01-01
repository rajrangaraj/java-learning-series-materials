/**
 * Manages student records with efficient lookup and filtering capabilities
 */
public class StudentDirectory {
    private final Map<String, Student> studentsById;
    private final NavigableMap<Double, Set<Student>> studentsByGrade;
    private final Map<Course, Set<Student>> enrollmentsByCourse;
    
    public StudentDirectory() {
        this.studentsById = new HashMap<>();
        this.studentsByGrade = new TreeMap<>();
        this.enrollmentsByCourse = new HashMap<>();
    }
    
    /**
     * Adds a new student to the directory
     */
    public void addStudent(Student student) {
        studentsById.put(student.getId(), student);
        updateGradeIndex(student);
        updateCourseEnrollments(student);
    }
    
    /**
     * Updates an existing student's information
     */
    public void updateStudent(Student student) {
        Student existing = studentsById.get(student.getId());
        if (existing != null) {
            // Remove from old indexes
            removeFromGradeIndex(existing);
            removeFromCourseEnrollments(existing);
            
            // Update student
            existing.setName(student.getName());
            existing.setGrade(student.getGrade());
            
            // Update indexes
            updateGradeIndex(existing);
            updateCourseEnrollments(existing);
        }
    }
    
    /**
     * Removes a student from the directory
     */
    public void removeStudent(String studentId) {
        Student student = studentsById.remove(studentId);
        if (student != null) {
            removeFromGradeIndex(student);
            removeFromCourseEnrollments(student);
        }
    }
    
    /**
     * Finds a student by their ID
     */
    public Optional<Student> findById(String id) {
        return Optional.ofNullable(studentsById.get(id));
    }
    
    /**
     * Finds all students within a grade range
     */
    public List<Student> findByGradeRange(double minGrade, double maxGrade) {
        return studentsByGrade.subMap(minGrade, true, maxGrade, true)
                            .values()
                            .stream()
                            .flatMap(Set::stream)
                            .collect(Collectors.toList());
    }
    
    /**
     * Finds all students enrolled in a specific course
     */
    public Set<Student> findByCourse(Course course) {
        return enrollmentsByCourse.getOrDefault(course, Collections.emptySet());
    }
    
    /**
     * Enrolls a student in a course
     */
    public void enrollStudentInCourse(String studentId, Course course) {
        Student student = studentsById.get(studentId);
        if (student != null) {
            student.enrollCourse(course);
            enrollmentsByCourse.computeIfAbsent(course, k -> new HashSet<>())
                             .add(student);
        }
    }
    
    /**
     * Drops a student from a course
     */
    public void dropStudentFromCourse(String studentId, Course course) {
        Student student = studentsById.get(studentId);
        if (student != null) {
            student.dropCourse(course);
            Set<Student> enrolled = enrollmentsByCourse.get(course);
            if (enrolled != null) {
                enrolled.remove(student);
                if (enrolled.isEmpty()) {
                    enrollmentsByCourse.remove(course);
                }
            }
        }
    }
    
    /**
     * Generates a report of all students
     */
    public String generateReport() {
        StringBuilder report = new StringBuilder();
        report.append("Student Directory Report\n");
        report.append("======================\n\n");
        
        report.append("Total Students: ").append(studentsById.size()).append("\n");
        report.append("Average Grade: ").append(calculateAverageGrade()).append("\n\n");
        
        report.append("Students by Grade:\n");
        studentsByGrade.descendingMap().forEach((grade, students) -> {
            report.append(String.format("Grade %.2f: %d students\n", 
                grade, students.size()));
        });
        
        report.append("\nCourse Enrollments:\n");
        enrollmentsByCourse.forEach((course, students) -> {
            report.append(String.format("%s: %d students\n", 
                course.getName(), students.size()));
        });
        
        return report.toString();
    }
    
    private void updateGradeIndex(Student student) {
        studentsByGrade.computeIfAbsent(student.getGrade(), k -> new HashSet<>())
                      .add(student);
    }
    
    private void removeFromGradeIndex(Student student) {
        Set<Student> gradeSet = studentsByGrade.get(student.getGrade());
        if (gradeSet != null) {
            gradeSet.remove(student);
            if (gradeSet.isEmpty()) {
                studentsByGrade.remove(student.getGrade());
            }
        }
    }
    
    private void updateCourseEnrollments(Student student) {
        for (Course course : student.getCourses()) {
            enrollmentsByCourse.computeIfAbsent(course, k -> new HashSet<>())
                             .add(student);
        }
    }
    
    private void removeFromCourseEnrollments(Student student) {
        for (Course course : student.getCourses()) {
            Set<Student> enrolled = enrollmentsByCourse.get(course);
            if (enrolled != null) {
                enrolled.remove(student);
                if (enrolled.isEmpty()) {
                    enrollmentsByCourse.remove(course);
                }
            }
        }
    }
    
    private double calculateAverageGrade() {
        return studentsById.values().stream()
                          .mapToDouble(Student::getGrade)
                          .average()
                          .orElse(0.0);
    }
} 