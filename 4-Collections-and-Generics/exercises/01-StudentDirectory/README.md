# Student Directory Exercise

Implement a student directory system using appropriate collections to manage student records.

## Requirements

1. Create a `Student` class with:
   - ID
   - Name
   - Grade
   - List of courses

2. Implement a `StudentDirectory` class that:
   - Stores students efficiently
   - Allows quick lookup by ID
   - Supports filtering by grade
   - Maintains course enrollment lists

3. Support operations:
   - Add/remove students
   - Update student information
   - Search by various criteria
   - Generate reports

## Implementation Tips

- Use `HashMap` for O(1) lookup by ID
- Consider `TreeMap` for sorted views
- Use `Set` for unique course lists
- Implement proper equals/hashCode 