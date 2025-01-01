/**
 * Exercise: Grade Calculator
 * 
 * Create a program that converts numerical scores to letter grades using
 * different control structures.
 * 
 * Requirements:
 * 1. Use if-else statements for basic grade conversion
 * 2. Use switch statement for grade comments
 * 3. Handle invalid scores (less than 0 or greater than 100)
 * 4. Add plus/minus grades (e.g., B+, B, B-)
 */
public class GradeCalculator {
    public static void main(String[] args) {
        // Test scores - feel free to modify these for testing
        int[] scores = {95, 85, 72, 65, 100, -5, 105};

        for (int score : scores) {
            // TODO: Validate the score first
            // Hint: Check if score is between 0 and 100

            // TODO: Convert score to basic letter grade
            // 90-100: A
            // 80-89:  B
            // 70-79:  C
            // 60-69:  D
            // 0-59:   F
            String letterGrade = "";

            // TODO: Add plus/minus to the grade
            // 7-9: +
            // 4-6: (no symbol)
            // 0-3: -
            // Exception: A+ for 100, no F+ or F-
            String finalGrade = "";

            // TODO: Add grade comments using switch
            // A: "Excellent!"
            // B: "Good job!"
            // C: "Satisfactory"
            // D: "Needs improvement"
            // F: "Failed"
            String comment = "";

            // TODO: Print the results
            // Format: "Score: X | Grade: X+ | Comment: X"
        }
    }
} 