/**
 * Demonstrates various conditional statements in Java
 */
public class ConditionalExamples {
    public static void main(String[] args) {
        // Basic if-else
        int age = 18;
        if (age >= 18) {
            System.out.println("You are an adult");
        } else {
            System.out.println("You are a minor");
        }

        // else-if chain
        int score = 85;
        if (score >= 90) {
            System.out.println("Grade: A");
        } else if (score >= 80) {
            System.out.println("Grade: B");
        } else if (score >= 70) {
            System.out.println("Grade: C");
        } else {
            System.out.println("Grade: F");
        }

        // Nested if statements
        boolean hasLicense = true;
        if (age >= 16) {
            if (hasLicense) {
                System.out.println("You can drive alone");
            } else {
                System.out.println("You need a licensed driver");
            }
        } else {
            System.out.println("Too young to drive");
        }

        // Ternary operator
        int number = 7;
        String result = (number % 2 == 0) ? "Even" : "Odd";
        System.out.println(number + " is " + result);

        // Multiple conditions
        boolean isWeekend = true;
        boolean isHoliday = false;
        if (isWeekend || isHoliday) {
            System.out.println("You can sleep in");
        }

        // Complex conditions with parentheses
        int temperature = 75;
        boolean isRaining = false;
        if ((temperature > 70 && !isRaining) || temperature > 80) {
            System.out.println("Good day for outdoor activities");
        }
    }
} 