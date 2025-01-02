/**
 * Demonstrates encapsulation, validation, and transaction handling
 */
public class BankAccount {
    // Instance variables
    private String accountNumber;
    private String accountHolder;
    private double balance;
    private boolean isLocked;
    
    // Transaction history using composition
    private TransactionHistory history;

    // Static variables for account management
    private static int accountCount = 0;
    private static final double MINIMUM_BALANCE = 0.0;
    private static final double DAILY_WITHDRAWAL_LIMIT = 1000.0;

    // Nested class for tracking transactions
    private class TransactionHistory {
        private static final int MAX_TRANSACTIONS = 100;
        private String[] transactions;
        private int index;

        public TransactionHistory() {
            transactions = new String[MAX_TRANSACTIONS];
            index = 0;
        }

        public void addTransaction(String type, double amount) {
            if (index < MAX_TRANSACTIONS) {
                transactions[index++] = String.format("%s: $%.2f", type, amount);
            }
        }

        public String[] getRecentTransactions() {
            String[] recent = new String[Math.min(index, 10)];
            for (int i = 0; i < recent.length; i++) {
                recent[i] = transactions[index - 1 - i];
            }
            return recent;
        }
    }

    // Constructor
    public BankAccount(String accountHolder, double initialDeposit) {
        this.accountNumber = generateAccountNumber();
        this.accountHolder = accountHolder;
        this.balance = 0.0;
        this.isLocked = false;
        this.history = new TransactionHistory();
        
        if (initialDeposit > 0) {
            deposit(initialDeposit);
        }
    }

    // Private helper method
    private String generateAccountNumber() {
        return "ACC" + (++accountCount) + System.currentTimeMillis() % 10000;
    }

    // Public methods
    public void deposit(double amount) {
        if (isLocked) {
            throw new IllegalStateException("Account is locked");
        }
        
        if (amount <= 0) {
            throw new IllegalArgumentException("Invalid deposit amount");
        }

        balance += amount;
        history.addTransaction("Deposit", amount);
    }

    public boolean withdraw(double amount) {
        if (isLocked) {
            throw new IllegalStateException("Account is locked");
        }

        if (amount <= 0) {
            throw new IllegalArgumentException("Invalid withdrawal amount");
        }

        if (amount > DAILY_WITHDRAWAL_LIMIT) {
            throw new IllegalArgumentException("Exceeds daily withdrawal limit");
        }

        if (balance - amount < MINIMUM_BALANCE) {
            return false;
        }

        balance -= amount;
        history.addTransaction("Withdrawal", -amount);
        return true;
    }

    public void lock() {
        isLocked = true;
    }

    public void unlock() {
        isLocked = false;
    }

    // Getters
    public String getAccountNumber() { return accountNumber; }
    public String getAccountHolder() { return accountHolder; }
    public double getBalance() { return balance; }
    public boolean isLocked() { return isLocked; }
    
    public String[] getRecentTransactions() {
        return history.getRecentTransactions();
    }

    @Override
    public String toString() {
        return String.format("Account[%s] - Holder: %s, Balance: $%.2f %s",
                accountNumber, accountHolder, balance,
                isLocked ? "(LOCKED)" : "");
    }
} 