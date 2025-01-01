# Shape Calculator Exercise

## Objective
Create a shape calculation system that demonstrates inheritance, abstraction, and interfaces through geometric shapes.

## Requirements

### 1. Shape Class (Abstract)
- Base class for all shapes
- Common attributes:
  - Name
  - Color
- Abstract methods:
  - calculateArea()
  - calculatePerimeter()
- Implements Comparable interface

### 2. Specific Shape Classes
1. Circle:
   - Radius
   - PI constant
   - Area and perimeter calculations

2. Rectangle:
   - Length and width
   - Area and perimeter calculations

### Implementation Details
1. Shape Calculations:
   - Accurate mathematical formulas
   - Proper handling of units
   - Validation of dimensions

2. Comparison:
   - Compare shapes by area
   - Sort collections of shapes
   - Find largest/smallest shapes

3. Display:
   - Format measurements
   - Visual representation
   - Conversion between units

## Test Cases
See test_cases.txt for specific measurements and expected results.

## Bonus Challenges
1. Add more shapes (Triangle, Square)
2. Implement shape scaling
3. Add color properties
4. Calculate combined areas 