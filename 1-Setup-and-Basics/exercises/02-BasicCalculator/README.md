# Exercise 2: Basic Calculator

## Objective
Create a calculator program that performs basic arithmetic operations on two numbers and displays the results in a formatted manner.

## Requirements
1. Create variables for:
   - Two numbers (use double for decimal support)
   - Results of each operation

2. Perform these operations:
   - Addition (+)
   - Subtraction (-)
   - Multiplication (*)
   - Division (/)

3. Handle special cases:
   - Division by zero
   - Decimal number formatting

4. Display results in the specified format

## Expected Format 
1-Setup-and-Basics/exercises/01-FirstProgram/README.md
Addition: [number1] + [number2] = [result]
Subtraction: [number1] - [number2] = [result]
Multiplication: [number1] [number2] = [result]
Division: [number1] / [number2] = [result]

## Tips
1. Division handling:
   - Check if second number is zero
   - Print "Cannot divide by zero" for division by zero
   - Round division results to 6 decimal places

2. Number formatting:
   - Use printf or DecimalFormat for consistent decimal places
   - Whole numbers should not show decimal places
   - Decimal numbers should show appropriate precision

## Example Code Structure

java
double number1 = 10.0;
double number2 = 5.0;
// Addition
double sum = number1 + number2;
System.out.println("Addition: " + number1 + " + " + number2 + " = " + sum);

## Testing
1. Test with provided test cases:
   - Whole numbers
   - Decimal numbers
   - Zero values
   - Negative numbers

2. Verify output:
   - Matches expected format exactly
   - Handles division by zero
   - Shows appropriate decimal places

## Common Issues
1. Division by zero:
   - Always check before dividing
   - Provide appropriate error message

2. Decimal formatting:
   - Be consistent with decimal places
   - Use proper formatting methods

3. Output format:
   - Match exactly with test cases
   - Include spaces and symbols as shown

## Additional Challenge
- Add input validation
- Support more operations
- Implement continuous calculation mode

