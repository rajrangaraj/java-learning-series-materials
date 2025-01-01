# Number Guessing Game

## Objective
Create an interactive number guessing game that generates a random number and provides feedback to the player.

## Requirements

### 1. Game Setup
- Generate random number between 1 and 100
- Track number of attempts
- Provide clear instructions to player

### 2. Game Logic
- Accept user input for guesses
- Provide feedback (higher/lower)
- Track and display attempts
- Allow multiple games

### 3. User Interface 
2-Control-Structures/exercises/02-MultiplicationTable/README.md
Welcome to the Number Guessing Game!
I'm thinking of a number between 1 and 100...
Enter your guess: 50
Too low! Try again.
Enter your guess: 75
Too high! Try again.
Enter your guess: 63
Correct! You got it in 3 attempts!
Play again? (y/n):

### 4. Input Validation
- Handle non-numeric input
- Validate guess range (1-100)
- Proper game exit handling

## Tips
1. Use Scanner for user input
2. Remember to close Scanner
3. Use Random class for number generation
4. Keep track of best score

## Test Cases
See test_cases.txt for example gameplay scenarios.

## Bonus Challenges
1. Add difficulty levels
2. Implement score system
3. Save high scores
4. Add time limit
