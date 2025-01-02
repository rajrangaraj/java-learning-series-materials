/**
 * Demonstrates basic class structure, encapsulation, and method implementation
 */
public class Person {
    // Private instance variables (encapsulation)
    private String name;
    private int age;
    private String email;
    
    // Static variable (shared across all instances)
    private static int totalPersons = 0;
    
    // Constants
    public static final int MIN_AGE = 0;
    public static final int MAX_AGE = 150;

    // Default constructor
    public Person() {
        this("Unknown", 0, "");
    }

    // Parameterized constructor
    public Person(String name, int age, String email) {
        this.name = name;
        setAge(age);  // Using setter for validation
        this.email = email;
        totalPersons++;
    }

    // Getter methods
    public String getName() {
        return name;
    }

    public int getAge() {
        return age;
    }

    public String getEmail() {
        return email;
    }

    // Setter methods with validation
    public void setName(String name) {
        if (name != null && !name.trim().isEmpty()) {
            this.name = name;
        }
    }

    public void setAge(int age) {
        if (age >= MIN_AGE && age <= MAX_AGE) {
            this.age = age;
        } else {
            throw new IllegalArgumentException(
                "Age must be between " + MIN_AGE + " and " + MAX_AGE
            );
        }
    }

    public void setEmail(String email) {
        if (email != null && email.contains("@")) {
            this.email = email;
        }
    }

    // Static method
    public static int getTotalPersons() {
        return totalPersons;
    }

    // Instance method
    public boolean isAdult() {
        return age >= 18;
    }

    // Method overriding
    @Override
    public String toString() {
        return String.format(
            "Person[name=%s, age=%d, email=%s]",
            name, age, email
        );
    }

    // Method overloading
    public void updateInfo(String name) {
        setName(name);
    }

    public void updateInfo(String name, int age) {
        setName(name);
        setAge(age);
    }

    public void updateInfo(String name, int age, String email) {
        setName(name);
        setAge(age);
        setEmail(email);
    }
} 