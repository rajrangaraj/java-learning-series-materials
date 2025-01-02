/**
 * Demonstrates basic exception handling patterns and best practices
 */
public class BasicExceptionHandling {
    
    public static void main(String[] args) {
        demonstrateBasicTryCatch();
        demonstrateMultipleExceptions();
        demonstrateTryWithResources();
        demonstrateCustomExceptions();
        demonstrateExceptionChaining();
        demonstrateFinally();
    }
    
    private static void demonstrateBasicTryCatch() {
        System.out.println("\n=== Basic Try-Catch ===");
        
        try {
            // Attempt to parse an invalid number
            int number = Integer.parseInt("abc");
            System.out.println("Number: " + number); // Won't be executed
        } catch (NumberFormatException e) {
            System.out.println("Error parsing number: " + e.getMessage());
            // Log the full stack trace
            e.printStackTrace();
        }
    }
    
    private static void demonstrateMultipleExceptions() {
        System.out.println("\n=== Multiple Exceptions ===");
        
        try {
            // Potentially throwing different exceptions
            String text = readFromFile("nonexistent.txt");
            int number = Integer.parseInt(text);
            System.out.println("Number: " + number);
        } catch (IOException e) {
            System.out.println("File error: " + e.getMessage());
        } catch (NumberFormatException e) {
            System.out.println("Number format error: " + e.getMessage());
        } catch (Exception e) {
            // Generic catch block should be last
            System.out.println("Unexpected error: " + e.getMessage());
        }
        
        // Alternative using multi-catch
        try {
            String text = readFromFile("nonexistent.txt");
            int number = Integer.parseInt(text);
            System.out.println("Number: " + number);
        } catch (IOException | NumberFormatException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
    
    private static void demonstrateTryWithResources() {
        System.out.println("\n=== Try-With-Resources ===");
        
        // Old way
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader("test.txt"));
            String line = reader.readLine();
            System.out.println("Read: " + line);
        } catch (IOException e) {
            System.out.println("Error reading file: " + e.getMessage());
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    System.out.println("Error closing reader: " + e.getMessage());
                }
            }
        }
        
        // New way with try-with-resources
        try (BufferedReader newReader = new BufferedReader(new FileReader("test.txt"))) {
            String line = newReader.readLine();
            System.out.println("Read: " + line);
        } catch (IOException e) {
            System.out.println("Error reading file: " + e.getMessage());
        }
    }
    
    private static void demonstrateCustomExceptions() {
        System.out.println("\n=== Custom Exceptions ===");
        
        try {
            validateAge(-5);
        } catch (InvalidAgeException e) {
            System.out.println("Validation error: " + e.getMessage());
            System.out.println("Invalid value was: " + e.getInvalidAge());
        }
    }
    
    private static void demonstrateExceptionChaining() {
        System.out.println("\n=== Exception Chaining ===");
        
        try {
            processData("invalid data");
        } catch (DataProcessingException e) {
            System.out.println("Processing error: " + e.getMessage());
            System.out.println("Caused by: " + e.getCause().getMessage());
        }
    }
    
    private static void demonstrateFinally() {
        System.out.println("\n=== Finally Block ===");
        
        try {
            System.out.println("Attempting risky operation...");
            if (Math.random() < 0.5) {
                throw new RuntimeException("Random failure");
            }
            System.out.println("Operation succeeded");
        } catch (RuntimeException e) {
            System.out.println("Operation failed: " + e.getMessage());
            return; // Finally block will still execute
        } finally {
            System.out.println("Cleanup in finally block");
        }
    }
    
    // Helper methods and custom exceptions
    private static String readFromFile(String filename) throws IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            return reader.readLine();
        }
    }
    
    private static void validateAge(int age) throws InvalidAgeException {
        if (age < 0) {
            throw new InvalidAgeException("Age cannot be negative", age);
        }
    }
    
    private static void processData(String data) throws DataProcessingException {
        try {
            if (data.equals("invalid data")) {
                throw new IllegalArgumentException("Invalid data format");
            }
            // Process data...
        } catch (IllegalArgumentException e) {
            throw new DataProcessingException("Could not process data", e);
        }
    }
    
    // Custom exception classes
    static class InvalidAgeException extends Exception {
        private final int invalidAge;
        
        public InvalidAgeException(String message, int invalidAge) {
            super(message);
            this.invalidAge = invalidAge;
        }
        
        public int getInvalidAge() {
            return invalidAge;
        }
    }
    
    static class DataProcessingException extends Exception {
        public DataProcessingException(String message, Throwable cause) {
            super(message, cause);
        }
    }
} 