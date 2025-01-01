/**
 * SavingsAccount implementation with interest and withdrawal limits
 */
public class SavingsAccount extends Account implements InterestBearing {
    private double interestRate;
    private double minimumBalance;
    private int withdrawalsThisMonth;
    private LocalDate lastWithdrawalReset;
    
    private static final int MAX_WITHDRAWALS = 3;
    private static final double DEFAULT_MINIMUM_BALANCE = 100.0;

    public SavingsAccount(String accountNumber, String accountHolder, 
                         double initialDeposit, double interestRate) {
        super(accountNumber, accountHolder, initialDeposit);
        
        if (initialDeposit < DEFAULT_MINIMUM_BALANCE) {
            throw new IllegalArgumentException(
                "Initial deposit must be at least $" + DEFAULT_MINIMUM_BALANCE
            );
        }
        
        this.interestRate = interestRate;
        this.minimumBalance = DEFAULT_MINIMUM_BALANCE;
        this.withdrawalsThisMonth = 0;
        this.lastWithdrawalReset = LocalDate.now();
    }

    @Override
    public boolean withdraw(double amount) throws AccountException {
        validateStatus();
        validateWithdrawalLimit();
        
        if (amount <= 0) {
            throw new IllegalArgumentException("Withdrawal amount must be positive");
        }

        if (balance - amount < minimumBalance) {
            throw new AccountException.InsufficientFundsException(
                accountNumber, amount, balance - minimumBalance
            );
        }

        balance -= amount;
        withdrawalsThisMonth++;
        
        addTransaction(new Transaction(
            TransactionType.WITHDRAWAL,
            amount,
            "Withdrawal",
            balance
        ));
        
        return true;
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
            "Deposit",
            balance
        ));
        
        return true;
    }

    @Override
    public double calculateMonthlyInterest() {
        return balance * (interestRate / 12.0 / 100.0);
    }

    @Override
    public void applyInterest() {
        double interest = calculateMonthlyInterest();
        balance += interest;
        
        addTransaction(new Transaction(
            TransactionType.INTEREST,
            interest,
            "Monthly interest",
            balance
        ));
    }

    private void validateWithdrawalLimit() throws AccountException {
        LocalDate now = LocalDate.now();
        
        // Reset withdrawal count if we're in a new month
        if (now.getMonth() != lastWithdrawalReset.getMonth() || 
            now.getYear() != lastWithdrawalReset.getYear()) {
            withdrawalsThisMonth = 0;
            lastWithdrawalReset = now;
        }

        if (withdrawalsThisMonth >= MAX_WITHDRAWALS) {
            throw new AccountException.WithdrawalLimitExceededException(
                accountNumber, MAX_WITHDRAWALS
            );
        }
    }

    // Getters and setters
    @Override
    public double getInterestRate() { return interestRate; }

    @Override
    public void setInterestRate(double rate) {
        if (rate < 0) {
            throw new IllegalArgumentException("Interest rate cannot be negative");
        }
        this.interestRate = rate;
    }

    public double getMinimumBalance() { return minimumBalance; }
    
    public void setMinimumBalance(double minimumBalance) {
        if (minimumBalance < 0) {
            throw new IllegalArgumentException("Minimum balance cannot be negative");
        }
        this.minimumBalance = minimumBalance;
    }

    public int getRemainingWithdrawals() {
        return MAX_WITHDRAWALS - withdrawalsThisMonth;
    }

    @Override
    public String toString() {
        return String.format("SavingsAccount[number=%s, holder=%s, balance=%.2f, " +
                           "interestRate=%.2f%%, withdrawalsLeft=%d]",
            accountNumber, accountHolder, balance, interestRate, 
            MAX_WITHDRAWALS - withdrawalsThisMonth);
    }
} 