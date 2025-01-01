/**
 * Demonstrates different types of loops and their use cases in Java
 */
public class LoopExamples {
    public static void main(String[] args) {
        // For loop with counter
        System.out.println("=== Basic For Loop ===");
        for (int i = 1; i <= 5; i++) {
            System.out.println("Count: " + i);
        }

        // While loop
        System.out.println("\n=== While Loop ===");
        int count = 0;
        while (count < 3) {
            System.out.println("While count: " + count);
            count++;
        }

        // Do-while loop (executes at least once)
        System.out.println("\n=== Do-While Loop ===");
        int doCount = 0;
        do {
            System.out.println("Do-While count: " + doCount);
            doCount++;
        } while (doCount < 3);

        // Enhanced for loop (for-each)
        System.out.println("\n=== Enhanced For Loop ===");
        String[] fruits = {"Apple", "Banana", "Orange"};
        for (String fruit : fruits) {
            System.out.println("Fruit: " + fruit);
        }

        // Nested loops (multiplication table)
        System.out.println("\n=== Nested Loops ===");
        for (int i = 1; i <= 3; i++) {
            for (int j = 1; j <= 3; j++) {
                System.out.printf("%d x %d = %d\t", i, j, i * j);
            }
            System.out.println();
        }

        // Break statement
        System.out.println("\n=== Break Statement ===");
        for (int i = 1; i <= 10; i++) {
            if (i == 6) {
                break;
            }
            System.out.print(i + " ");
        }
        System.out.println();

        // Continue statement
        System.out.println("\n=== Continue Statement ===");
        for (int i = 1; i <= 5; i++) {
            if (i == 3) {
                continue;
            }
            System.out.print(i + " ");
        }
        System.out.println();

        // Infinite loop with break
        System.out.println("\n=== Controlled Infinite Loop ===");
        int infiniteCount = 0;
        while (true) {
            System.out.print(infiniteCount + " ");
            infiniteCount++;
            if (infiniteCount > 4) {
                break;
            }
        }
        System.out.println();
    }
} 