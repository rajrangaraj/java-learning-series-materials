/**
 * This is a simple Hello World program that demonstrates:
 * - Basic class structure
 * - Main method declaration
 * - Print statements
 * - Basic comments (single-line and multi-line)
 */
public class HelloWorld {
    // The main method is the entry point of the program
    public static void main(String[] args) {
        // Print a message to the console without a new line
        System.out.print("Hello, ");
        
        // Print a message to the console with a new line
        System.out.println("World!");

        // Demonstrate string concatenation
        String name = "Java Programmer";
        System.out.println("Welcome, " + name + "!");

        /*
         * Different ways to print:
         * 1. System.out.print() - prints without new line
         * 2. System.out.println() - prints with new line
         * 3. System.out.printf() - formatted printing
         */
        System.out.printf("You are running Java version %s%n", System.getProperty("java.version"));
    }
} 