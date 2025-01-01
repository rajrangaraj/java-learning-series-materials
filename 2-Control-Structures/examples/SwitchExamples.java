/**
 * Demonstrates different types of switch statements and expressions in Java
 */
public class SwitchExamples {
    public static void main(String[] args) {
        // Basic switch statement
        System.out.println("=== Basic Switch ===");
        int day = 3;
        switch (day) {
            case 1:
                System.out.println("Monday");
                break;
            case 2:
                System.out.println("Tuesday");
                break;
            case 3:
                System.out.println("Wednesday");
                break;
            default:
                System.out.println("Other day");
        }

        // Switch without break (fall-through)
        System.out.println("\n=== Fall-through Example ===");
        int month = 2;
        switch (month) {
            case 12:
            case 1:
            case 2:
                System.out.println("Winter");
                break;
            case 3:
            case 4:
            case 5:
                System.out.println("Spring");
                break;
            default:
                System.out.println("Other season");
        }

        // Switch with String
        System.out.println("\n=== String Switch ===");
        String command = "start";
        switch (command) {
            case "start":
                System.out.println("Starting...");
                break;
            case "stop":
                System.out.println("Stopping...");
                break;
            default:
                System.out.println("Unknown command");
        }

        // Modern switch expression (Java 14+)
        System.out.println("\n=== Modern Switch Expression ===");
        String grade = "B";
        String feedback = switch (grade) {
            case "A" -> "Excellent!";
            case "B" -> "Good job!";
            case "C" -> "Acceptable";
            default -> "Need improvement";
        };
        System.out.println("Feedback: " + feedback);

        // Switch with multiple cases
        System.out.println("\n=== Multiple Cases ===");
        char choice = 'Y';
        switch (choice) {
            case 'y':
            case 'Y':
                System.out.println("You agreed");
                break;
            case 'n':
            case 'N':
                System.out.println("You declined");
                break;
            default:
                System.out.println("Invalid choice");
        }

        // Switch with enum (common use case)
        System.out.println("\n=== Enum Switch ===");
        DayOfWeek today = DayOfWeek.FRIDAY;
        switch (today) {
            case MONDAY:
                System.out.println("Start of work week");
                break;
            case FRIDAY:
                System.out.println("TGIF!");
                break;
            case SATURDAY:
            case SUNDAY:
                System.out.println("Weekend!");
                break;
            default:
                System.out.println("Midweek");
        }
    }

    // Enum for switch example
    enum DayOfWeek {
        MONDAY, TUESDAY, WEDNESDAY, THURSDAY, FRIDAY, SATURDAY, SUNDAY
    }
} 