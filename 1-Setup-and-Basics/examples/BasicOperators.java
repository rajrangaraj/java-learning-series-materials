/**
 * This class demonstrates:
 * - Arithmetic operators
 * - Assignment operators
 * - Comparison operators
 * - Logical operators
 * - Increment/Decrement operators
 * - Bitwise operators
 * - Operator precedence
 */
public class BasicOperators {
    public static void main(String[] args) {
        // Arithmetic Operators
        System.out.println("=== Arithmetic Operators ===");
        int a = 10;
        int b = 3;
        
        System.out.println("a = " + a + ", b = " + b);
        System.out.println("Addition: " + (a + b));        // 13
        System.out.println("Subtraction: " + (a - b));     // 7
        System.out.println("Multiplication: " + (a * b));  // 30
        System.out.println("Division: " + (a / b));        // 3
        System.out.println("Modulus: " + (a % b));        // 1

        // Assignment Operators
        System.out.println("\n=== Assignment Operators ===");
        int c = 20;
        System.out.println("Original c: " + c);
        c += 5;  // c = c + 5
        System.out.println("After c += 5: " + c);
        c *= 2;  // c = c * 2
        System.out.println("After c *= 2: " + c);

        // Comparison Operators
        System.out.println("\n=== Comparison Operators ===");
        int x = 5, y = 8;
        System.out.println("x = " + x + ", y = " + y);
        System.out.println("x == y: " + (x == y));  // false
        System.out.println("x != y: " + (x != y));  // true
        System.out.println("x < y: " + (x < y));    // true
        System.out.println("x >= y: " + (x >= y));  // false

        // Logical Operators
        System.out.println("\n=== Logical Operators ===");
        boolean isTrue = true;
        boolean isFalse = false;
        System.out.println("AND (&&): " + (isTrue && isFalse));  // false
        System.out.println("OR (||): " + (isTrue || isFalse));   // true
        System.out.println("NOT (!): " + (!isTrue));             // false

        // Increment/Decrement Operators
        System.out.println("\n=== Increment/Decrement Operators ===");
        int count = 5;
        System.out.println("Original count: " + count);
        System.out.println("Prefix increment (++count): " + (++count));  // 6
        System.out.println("Postfix increment (count++): " + (count++)); // 6
        System.out.println("After postfix increment: " + count);         // 7
        System.out.println("Prefix decrement (--count): " + (--count));  // 6

        // Bitwise Operators
        System.out.println("\n=== Bitwise Operators ===");
        int num1 = 5;  // binary: 0101
        int num2 = 3;  // binary: 0011
        System.out.println("num1 = " + num1 + ", num2 = " + num2);
        System.out.println("Bitwise AND (&): " + (num1 & num2));    // 1 (0001)
        System.out.println("Bitwise OR (|): " + (num1 | num2));     // 7 (0111)
        System.out.println("Bitwise XOR (^): " + (num1 ^ num2));    // 6 (0110)
        System.out.println("Bitwise NOT (~): " + (~num1));          // -6

        // Operator Precedence Example
        System.out.println("\n=== Operator Precedence ===");
        int result = 10 + 5 * 2;
        System.out.println("10 + 5 * 2 = " + result);  // 20 (not 30)
        
        result = (10 + 5) * 2;
        System.out.println("(10 + 5) * 2 = " + result);  // 30
    }
} 