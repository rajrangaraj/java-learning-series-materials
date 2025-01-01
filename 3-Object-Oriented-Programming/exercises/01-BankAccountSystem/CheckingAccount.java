/**
 * CheckingAccount implementation with optional overdraft protection
 */
public class CheckingAccount extends Account {
    private boolean hasOverdraftProtection;
    private double overdraftLimit;
    private double overdraftFee;
    private double monthlyMaintenanceFee;
    private LocalDate lastMaintenanceFeeDate;
    
    private static final double DEFAULT_OVERDRAFT_LIMIT = 100.0;
    private static final double DEFAULT_OVERDRAFT_FEE = 35.0;
    private static final double DEFAULT_MAINTENANCE_FEE = 12.0;
    private static final double MINIMUM_BALANCE_FOR_FREE_MAINTENANCE = 1500.0;

    public CheckingAccount(String accountNumber, String accountHolder, 
                         double initialDeposit, boolean hasOverdraftProtection) {
        super(accountNumber, accountHolder, initialDeposit);
        
        this.hasOverdraftProtection = hasOverdraftProtection;
        this.overdraftLimit = hasOverdraftProtection ? DEFAULT_OVERDRAFT_LIMIT : 0.0;
        this.overdraftFee = DEFAULT_OVERDRAFT_FEE;
        this.monthlyMaintenanceFee = DEFAULT_MAINTENANCE_FEE;
        this.lastMaintenanceFeeDate = LocalDate.now();
    }

    @Override
    public boolean deposit(double amount) throws AccountException {
        validateStatus();
        
        if (amount <= 0) {
            throw new IllegalArgumentException("Deposit amount must be positive");
        }

        balance += amount;
        
        addTransaction(new Transaction(
            TransactionType.DEPOSIT,
            amount,
            "Checking deposit",
            balance
        ));
        
        return true;
    }

    @Override
    public boolean withdraw(double amount) throws AccountException {
        validateStatus();
        
        if (amount <= 0) {
            throw new IllegalArgumentException("Withdrawal amount must be positive");
        }

        // Check if withdrawal is possible with available balance
        if (balance >= amount) {
            balance -= amount;
            addTransaction(new Transaction(
                TransactionType.WITHDRAWAL,
                amount,
                "Checking withdrawal",
                balance
            ));
            return true;
        }

        // Check overdraft protection
        if (hasOverdraftProtection) {
            double shortfall = amount - balance;
            if (shortfall <= overdraftLimit) {
                // Apply overdraft fee
                balance = -shortfall;
                addTransaction(new Transaction(
                    TransactionType.WITHDRAWAL,
                    amount,
                    "Checking withdrawal with overdraft",
                    balance
                ));
                
                // Apply overdraft fee
                balance -= overdraftFee;
                addTransaction(new Transaction(
                    TransactionType.FEE,
                    overdraftFee,
                    "Overdraft fee",
                    balance
                ));
                
                return true;
            }
        }

        throw new AccountException.InsufficientFundsException(
            accountNumber, amount, balance
        );
    }

    @Override
    public double calculateInterest() {
        return 0.0; // Checking accounts don't earn interest
    }

    /**
     * Applies monthly maintenance fee if applicable
     */
    public void applyMaintenanceFee() {
        LocalDate now = LocalDate.now();
        
        // Check if it's time for monthly fee
        if (now.getMonth() == lastMaintenanceFeeDate.getMonth() && 
            now.getYear() == lastMaintenanceFeeDate.getYear()) {
            return;
        }

        // Check if minimum balance requirement is met
        if (getAverageMonthlyBalance() < MINIMUM_BALANCE_FOR_FREE_MAINTENANCE) {
            balance -= monthlyMaintenanceFee;
            addTransaction(new Transaction(
                TransactionType.FEE,
                monthlyMaintenanceFee,
                "Monthly maintenance fee",
                balance
            ));
        }

        lastMaintenanceFeeDate = now;
    }

    /**
     * Calculates average monthly balance
     */
    private double getAverageMonthlyBalance() {
        LocalDate startOfMonth = LocalDate.now().withDayOfMonth(1);
        
        return transactions.stream()
            .filter(t -> !t.getTimestamp().toLocalDate().isBefore(startOfMonth))
            .mapToDouble(Transaction::getBalanceAfter)
            .average()
            .orElse(0.0);
    }

    /**
     * Overdraft protection management
     */
    public void enableOverdraftProtection() {
        this.hasOverdraftProtection = true;
        this.overdraftLimit = DEFAULT_OVERDRAFT_LIMIT;
    }

    public void disableOverdraftProtection() {
        if (balance < 0) {
            throw new IllegalStateException(
                "Cannot disable overdraft protection while account is overdrawn"
            );
        }
        this.hasOverdraftProtection = false;
        this.overdraftLimit = 0.0;
    }

    public void setOverdraftLimit(double limit) {
        if (!hasOverdraftProtection) {
            throw new IllegalStateException(
                "Cannot set overdraft limit without overdraft protection"
            );
        }
        if (limit < 0) {
            throw new IllegalArgumentException(
                "Overdraft limit cannot be negative"
            );
        }
        this.overdraftLimit = limit;
    }

    /**
     * Fee management
     */
    public void setOverdraftFee(double fee) {
        if (fee < 0) {
            throw new IllegalArgumentException("Fee cannot be negative");
        }
        this.overdraftFee = fee;
    }

    public void setMaintenanceFee(double fee) {
        if (fee < 0) {
            throw new IllegalArgumentException("Fee cannot be negative");
        }
        this.monthlyMaintenanceFee = fee;
    }

    /**
     * Getters
     */
    public boolean hasOverdraftProtection() {
        return hasOverdraftProtection;
    }

    public double getOverdraftLimit() {
        return overdraftLimit;
    }

    public double getOverdraftFee() {
        return overdraftFee;
    }

    public double getMaintenanceFee() {
        return monthlyMaintenanceFee;
    }

    public double getAvailableBalance() {
        return balance + (hasOverdraftProtection ? overdraftLimit : 0);
    }

    @Override
    public String toString() {
        return String.format("CheckingAccount[number=%s, holder=%s, balance=%.2f, " +
                           "overdraft=%b, limit=%.2f]",
            accountNumber, accountHolder, balance, 
            hasOverdraftProtection, overdraftLimit);
    }

    /**
     * Creates a detailed account summary
     */
    public String getAccountSummary() {
        StringBuilder summary = new StringBuilder();
        summary.append("Checking Account Summary\n");
        summary.append("=======================\n");
        summary.append(String.format("Account Number: %s\n", accountNumber));
        summary.append(String.format("Account Holder: %s\n", accountHolder));
        summary.append(String.format("Current Balance: $%.2f\n", balance));
        summary.append(String.format("Available Balance: $%.2f\n", getAvailableBalance()));
        summary.append("\nAccount Features:\n");
        summary.append(String.format("- Overdraft Protection: %s\n", 
            hasOverdraftProtection ? "Enabled" : "Disabled"));
        if (hasOverdraftProtection) {
            summary.append(String.format("  - Overdraft Limit: $%.2f\n", overdraftLimit));
            summary.append(String.format("  - Overdraft Fee: $%.2f\n", overdraftFee));
        }
        summary.append(String.format("- Monthly Maintenance Fee: $%.2f\n", 
            monthlyMaintenanceFee));
        summary.append(String.format("  (Waived if balance >= $%.2f)\n", 
            MINIMUM_BALANCE_FOR_FREE_MAINTENANCE));
        
        return summary.toString();
    }
} 