# Library Management System Exercise

## Objective
Create a library management system that demonstrates OOP principles through handling books, members, and borrowing processes.

## Requirements

### 1. LibraryItem Class (Abstract)
- Base class for all library items
- Common attributes:
  - Title
  - Item ID
  - Availability status
  - Borrow history
- Abstract methods:
  - calculateLateFee()
  - getMaxBorrowDays()

### 2. Book Class
- Extends LibraryItem
- Additional attributes:
  - Author
  - ISBN
  - Pages
  - Genre
- Specific borrowing rules

### 3. Member Class
- Member information
- Borrowed items tracking
- Fine management
- Borrowing history

### Implementation Details
1. Borrowing System:
   - Check item availability
   - Track due dates
   - Handle returns
   - Calculate fines

2. Member Management:
   - Track borrowed items
   - Manage fine payments
   - Borrowing limits
   - Member status

3. Fine System:
   - Daily rate calculation
   - Grace period
   - Payment processing
   - Fine history

## Test Cases
See test_cases.txt for specific scenarios and expected behaviors.

## Bonus Challenges
1. Add Magazine class
2. Implement reservations
3. Add librarian interface
4. Generate reports 