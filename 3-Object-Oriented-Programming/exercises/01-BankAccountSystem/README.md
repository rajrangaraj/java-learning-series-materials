# Bank Account System Exercise

## Objective
Create a banking system that demonstrates inheritance, encapsulation, and polymorphism through different types of bank accounts and transactions.

## Requirements

### 1. Account Class (Abstract)
- Base class for all account types
- Common attributes:
  - Account number
  - Account holder
  - Balance
  - Transaction history
- Abstract methods:
  - deposit()
  - withdraw()
  - calculateInterest()

### 2. SavingsAccount Class
- Extends Account
- Features:
  - Minimum balance requirement
  - Interest calculation
  - Limited withdrawals per month
  - Higher interest rate

### 3. Transaction Class
- Attributes:
  - Transaction ID
  - Timestamp
  - Amount
  - Type (deposit/withdrawal)
  - Balance after transaction

### Implementation Details
1. Account Management:
   - Generate unique account numbers
   - Track all transactions
   - Validate operations

2. Interest Calculation:
   - Monthly interest computation
   - Different rates for different balances

3. Transaction Handling:
   - Record all account activities
   - Maintain transaction history
   - Generate transaction receipts

## Test Cases
See test_cases.txt for specific scenarios and expected outputs.

## Bonus Challenges
1. Add CheckingAccount class
2. Implement account transfers
3. Add monthly statements
4. Support multiple currencies 