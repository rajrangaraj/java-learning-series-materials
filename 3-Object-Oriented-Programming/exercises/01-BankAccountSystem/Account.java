/**
 * Abstract base class for all bank accounts
 */
public abstract class Account {
    protected String accountNumber;
    protected String accountHolder;
    protected double balance;
    protected List<Transaction> transactions;
    protected LocalDate dateCreated;
    protected AccountStatus status;

    // Constructor
    public Account(String accountNumber, String accountHolder, double initialDeposit) {
        if (initialDeposit < 0) {
            throw new IllegalArgumentException("Initial deposit cannot be negative");
        }
        
        this.accountNumber = accountNumber;
        this.accountHolder = accountHolder;
        this.balance = initialDeposit;
        this.transactions = new ArrayList<>();
        this.dateCreated = LocalDate.now();
        this.status = AccountStatus.ACTIVE;

        // Record initial deposit transaction
        if (initialDeposit > 0) {
            addTransaction(new Transaction(
                TransactionType.DEPOSIT,
                initialDeposit,
                "Initial deposit",
                balance
            ));
        }
    }

    // Abstract methods
    public abstract boolean withdraw(double amount) throws AccountException;
    public abstract boolean deposit(double amount) throws AccountException;
    public abstract double calculateInterest();

    // Common methods
    public void addTransaction(Transaction transaction) {
        transactions.add(transaction);
    }

    public List<Transaction> getTransactionHistory() {
        return new ArrayList<>(transactions); // Return copy to preserve encapsulation
    }

    public List<Transaction> getTransactionHistory(LocalDate from, LocalDate to) {
        return transactions.stream()
            .filter(t -> {
                LocalDate date = t.getTimestamp().toLocalDate();
                return !date.isBefore(from) && !date.isAfter(to);
            })
            .collect(Collectors.toList());
    }

    public MonthlyStatement getMonthlyStatement(LocalDate month) {
        MonthlyStatement statement = new MonthlyStatement(accountNumber, month);
        statement.setOpeningBalance(getOpeningBalance(month));
        
        List<Transaction> monthTransactions = getTransactionHistory(
            month.withDayOfMonth(1),
            month.withDayOfMonth(month.lengthOfMonth())
        );
        
        monthTransactions.forEach(statement::addTransaction);
        statement.setClosingBalance(balance);
        
        return statement;
    }

    private double getOpeningBalance(LocalDate month) {
        LocalDate firstOfMonth = month.withDayOfMonth(1);
        return transactions.stream()
            .filter(t -> t.getTimestamp().toLocalDate().isBefore(firstOfMonth))
            .mapToDouble(Transaction::getBalanceAfter)
            .max()
            .orElse(0.0);
    }

    // Getters
    public String getAccountNumber() { return accountNumber; }
    public String getAccountHolder() { return accountHolder; }
    public double getBalance() { return balance; }
    public AccountStatus getStatus() { return status; }
    public LocalDate getDateCreated() { return dateCreated; }

    // Status management
    public void freeze() {
        this.status = AccountStatus.FROZEN;
    }

    public void activate() {
        this.status = AccountStatus.ACTIVE;
    }

    public void close() {
        this.status = AccountStatus.CLOSED;
    }

    protected void validateStatus() throws AccountException {
        if (status != AccountStatus.ACTIVE) {
            throw new AccountException(
                "Account is " + status.toString().toLowerCase(),
                accountNumber,
                "ERR_INVALID_STATUS"
            );
        }
    }

    @Override
    public String toString() {
        return String.format("Account[number=%s, holder=%s, balance=%.2f, status=%s]",
            accountNumber, accountHolder, balance, status);
    }
} 