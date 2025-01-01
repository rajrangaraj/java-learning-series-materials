# Grade Calculator Exercise

## Objective
Create a program that converts numerical scores to letter grades using various control structures and provides appropriate feedback for each grade.

## Requirements

### 1. Score Validation
- Check if score is between 0 and 100
- Print "Invalid Score" for scores outside this range

### 2. Basic Grade Conversion (using if-else)
- 90-100: A
- 80-89:  B
- 70-79:  C
- 60-69:  D
- 0-59:   F

### 3. Plus/Minus Grades
Last digit determines plus/minus:
- 7-9: + (plus)
- 4-6: (no symbol)
- 0-3: - (minus)
Special cases:
- 100 is A+
- No F+ or F-

### 4. Grade Comments (using switch)
- A: "Excellent!"
- B: "Good job!"
- C: "Satisfactory"
- D: "Needs improvement"
- F: "Failed"

### 5. Output Format 
Score: 95 | Grade: A | Comment: Excellent!
Score: 83 | Grade: B- | Comment: Good job!

## Tips
1. Use nested if-else for plus/minus grades
2. Remember to handle edge cases (100, 0)
3. Keep code organized and well-commented
4. Test with boundary values

## Test Cases
See test_cases.txt for specific examples and expected output.

## Bonus Challenges
1. Add grade point calculation (A = 4.0, A- = 3.7, etc.)
2. Calculate class average for multiple scores
3. Add grade distribution statistics

