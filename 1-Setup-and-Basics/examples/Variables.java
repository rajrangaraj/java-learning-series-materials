/**
 * This class demonstrates:
 * - Different variable types in Java
 * - Variable declaration and initialization
 * - Type conversion and casting
 * - Constants (final variables)
 * - Variable naming conventions
 */
public class Variables {
    public static void main(String[] args) {
        // Primitive Data Types
        // Integer types
        byte smallNumber = 127;                // 8-bit, range: -128 to 127
        short mediumNumber = 32000;            // 16-bit, range: -32,768 to 32,767
        int standardNumber = 2147483647;       // 32-bit, most commonly used
        long largeNumber = 9223372036854775807L; // 64-bit, note the 'L' suffix

        // Floating-point types
        float decimalNumber = 3.14159f;        // 32-bit, note the 'f' suffix
        double preciseNumber = 3.14159265359;  // 64-bit, more precise

        // Character type
        char singleCharacter = 'A';            // 16-bit Unicode character

        // Boolean type
        boolean isJavaFun = true;              // true or false

        // Constants
        final double PI = 3.14159;             // Constants are declared with 'final'
        final int MAX_USERS = 100;

        // Reference Types
        String message = "Hello, Java!";        // String is a class
        Integer wrappedInt = 42;               // Wrapper class for int

        // Type conversion and casting
        // Implicit conversion (widening)
        int myInt = 100;
        long myLong = myInt;    // Automatically converts int to long

        // Explicit conversion (narrowing)
        double myDouble = 3.14159;
        int roundedNumber = (int) myDouble;    // Explicitly cast double to int

        // Printing variables
        System.out.println("=== Numeric Types ===");
        System.out.println("byte: " + smallNumber);
        System.out.println("short: " + mediumNumber);
        System.out.println("int: " + standardNumber);
        System.out.println("long: " + largeNumber);
        System.out.println("float: " + decimalNumber);
        System.out.println("double: " + preciseNumber);

        System.out.println("\n=== Other Types ===");
        System.out.println("char: " + singleCharacter);
        System.out.println("boolean: " + isJavaFun);
        System.out.println("String: " + message);
        System.out.println("Wrapped Integer: " + wrappedInt);

        System.out.println("\n=== Type Conversion ===");
        System.out.println("Double to Int: " + roundedNumber);
        
        // Variable scope demonstration
        {
            int scopedVariable = 42;  // Only accessible within these braces
            System.out.println("\nScoped variable: " + scopedVariable);
        }
        // scopedVariable is not accessible here
    }
} 