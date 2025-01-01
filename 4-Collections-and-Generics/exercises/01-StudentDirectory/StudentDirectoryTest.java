/**
 * Test cases for StudentDirectory implementation
 */
public class StudentDirectoryTest {
    private StudentDirectory directory;
    private Course mathCourse;
    private Course physicsCourse;
    
    @BeforeEach
    void setUp() {
        directory = new StudentDirectory();
        mathCourse = new Course("MATH101", "Introduction to Mathematics", 3);
        physicsCourse = new Course("PHYS101", "Introduction to Physics", 4);
    }
    
    @Test
    void testAddAndFindStudent() {
        Student student = new Student("S001", "John Doe", 3.5);
        directory.addStudent(student);
        
        Optional<Student> found = directory.findById("S001");
        assertTrue(found.isPresent());
        assertEquals("John Doe", found.get().getName());
    }
    
    @Test
    void testUpdateStudent() {
        Student student = new Student("S001", "John Doe", 3.5);
        directory.addStudent(student);
        
        Student updated = new Student("S001", "John Smith", 3.8);
        directory.updateStudent(updated);
        
        Optional<Student> found = directory.findById("S001");
        assertTrue(found.isPresent());
        assertEquals("John Smith", found.get().getName());
        assertEquals(3.8, found.get().getGrade());
    }
    
    @Test
    void testFindByGradeRange() {
        directory.addStudent(new Student("S001", "John", 3.5));
        directory.addStudent(new Student("S002", "Jane", 3.8));
        directory.addStudent(new Student("S003", "Bob", 3.2));
        
        List<Student> students = directory.findByGradeRange(3.4, 3.9);
        assertEquals(2, students.size());
    }
    
    @Test
    void testCourseEnrollment() {
        Student student = new Student("S001", "John Doe", 3.5);
        directory.addStudent(student);
        
        directory.enrollStudentInCourse("S001", mathCourse);
        directory.enrollStudentInCourse("S001", physicsCourse);
        
        Set<Student> mathStudents = directory.findByCourse(mathCourse);
        assertEquals(1, mathStudents.size());
        
        directory.dropStudentFromCourse("S001", mathCourse);
        mathStudents = directory.findByCourse(mathCourse);
        assertTrue(mathStudents.isEmpty());
    }
    
    @Test
    void testGenerateReport() {
        directory.addStudent(new Student("S001", "John", 3.5));
        directory.addStudent(new Student("S002", "Jane", 3.8));
        
        String report = directory.generateReport();
        assertTrue(report.contains("Total Students: 2"));
        assertTrue(report.contains("Average Grade: 3.65"));
    }
} 