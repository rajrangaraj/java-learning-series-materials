/**
 * Helper class to manage multiple accounts and their interactions
 */
public class AccountManager {
    private Map<String, Account> accounts;
    private static int accountCounter = 1000;
    private List<Transaction> globalTransactionLog;
    
    public AccountManager() {
        accounts = new HashMap<>();
        globalTransactionLog = new ArrayList<>();
    }
    
    public String generateAccountNumber() {
        return "ACC" + (++accountCounter);
    }
    
    public void addAccount(Account account) {
        if (accounts.containsKey(account.getAccountNumber())) {
            throw new IllegalArgumentException(
                "Account number already exists: " + account.getAccountNumber()
            );
        }
        accounts.put(account.getAccountNumber(), account);
    }
    
    public Account getAccount(String accountNumber) {
        Account account = accounts.get(accountNumber);
        if (account == null) {
            throw new IllegalArgumentException(
                "Account not found: " + accountNumber
            );
        }
        return account;
    }
    
    public boolean transfer(String fromAccountNumber, String toAccountNumber, 
                          double amount) throws AccountException {
        Account fromAccount = getAccount(fromAccountNumber);
        Account toAccount = getAccount(toAccountNumber);
        
        // Validate accounts
        if (fromAccount == null || toAccount == null) {
            throw new AccountException(
                "Invalid account number(s)",
                fromAccountNumber,
                "ERR_INVALID_ACCOUNT"
            );
        }
        
        // Validate amount
        if (amount <= 0) {
            throw new IllegalArgumentException("Transfer amount must be positive");
        }
        
        // Perform transfer
        try {
            // First withdraw from source account
            fromAccount.withdraw(amount);
            
            // Then deposit to destination account
            toAccount.deposit(amount);
            
            // Record transfer transactions
            Transaction withdrawalTrans = new Transaction(
                TransactionType.TRANSFER_OUT,
                amount,
                "Transfer to " + toAccountNumber,
                fromAccount.getBalance()
            );
            
            Transaction depositTrans = new Transaction(
                TransactionType.TRANSFER_IN,
                amount,
                "Transfer from " + fromAccountNumber,
                toAccount.getBalance()
            );
            
            fromAccount.addTransaction(withdrawalTrans);
            toAccount.addTransaction(depositTrans);
            globalTransactionLog.add(withdrawalTrans);
            globalTransactionLog.add(depositTrans);
            
            return true;
        } catch (AccountException e) {
            // If withdrawal fails, throw the exception up
            throw e;
        }
    }
    
    public void applyMonthlyInterest() {
        accounts.values().stream()
            .filter(account -> account instanceof InterestBearing)
            .forEach(account -> {
                InterestBearing interestAccount = (InterestBearing) account;
                interestAccount.applyInterest();
            });
    }
    
    public List<Transaction> getMonthlyStatement(String accountNumber) {
        Account account = getAccount(accountNumber);
        LocalDate now = LocalDate.now();
        LocalDate startOfMonth = now.withDayOfMonth(1);
        
        return account.getTransactionHistory().stream()
            .filter(t -> !t.getTimestamp().toLocalDate().isBefore(startOfMonth))
            .collect(Collectors.toList());
    }
    
    // Account management methods
    public void freezeAccount(String accountNumber) {
        getAccount(accountNumber).freeze();
    }
    
    public void unfreezeAccount(String accountNumber) {
        getAccount(accountNumber).activate();
    }
    
    public void closeAccount(String accountNumber) {
        Account account = getAccount(accountNumber);
        if (account.getBalance() > 0) {
            throw new IllegalStateException(
                "Cannot close account with positive balance"
            );
        }
        account.close();
    }
    
    // Reporting methods
    public Map<String, Double> getAccountBalances() {
        return accounts.entrySet().stream()
            .collect(Collectors.toMap(
                Map.Entry::getKey,
                e -> e.getValue().getBalance()
            ));
    }
    
    public List<Account> getOverdrawnAccounts() {
        return accounts.values().stream()
            .filter(account -> account.getBalance() < 0)
            .collect(Collectors.toList());
    }
    
    public List<Account> getInactiveAccounts(int months) {
        LocalDate cutoffDate = LocalDate.now().minusMonths(months);
        return accounts.values().stream()
            .filter(account -> {
                Optional<Transaction> lastTransaction = account.getTransactionHistory()
                    .stream()
                    .max(Comparator.comparing(Transaction::getTimestamp));
                    
                return lastTransaction.map(t -> 
                    t.getTimestamp().toLocalDate().isBefore(cutoffDate)
                ).orElse(true);
            })
            .collect(Collectors.toList());
    }
    
    public double getTotalDeposits() {
        return globalTransactionLog.stream()
            .filter(t -> t.getType() == TransactionType.DEPOSIT)
            .mapToDouble(Transaction::getAmount)
            .sum();
    }
    
    public double getTotalWithdrawals() {
        return globalTransactionLog.stream()
            .filter(t -> t.getType() == TransactionType.WITHDRAWAL)
            .mapToDouble(Transaction::getAmount)
            .sum();
    }
    
    // Generate summary report
    public String generateSummaryReport() {
        StringBuilder report = new StringBuilder();
        report.append("Account Manager Summary Report\n");
        report.append("============================\n\n");
        
        report.append(String.format("Total Accounts: %d\n", accounts.size()));
        report.append(String.format("Total Balance: $%.2f\n", 
            accounts.values().stream()
                .mapToDouble(Account::getBalance)
                .sum()
        ));
        
        // Account type distribution
        Map<String, Long> accountTypes = accounts.values().stream()
            .collect(Collectors.groupingBy(
                account -> account.getClass().getSimpleName(),
                Collectors.counting()
            ));
            
        report.append("\nAccount Distribution:\n");
        accountTypes.forEach((type, count) -> 
            report.append(String.format("- %s: %d\n", type, count))
        );
        
        // Overdrawn accounts
        List<Account> overdrawn = getOverdrawnAccounts();
        report.append(String.format("\nOverdrawn Accounts: %d\n", overdrawn.size()));
        
        // Transaction summary
        report.append("\nTransaction Summary:\n");
        report.append(String.format("- Total Deposits: $%.2f\n", getTotalDeposits()));
        report.append(String.format("- Total Withdrawals: $%.2f\n", getTotalWithdrawals()));
        
        return report.toString();
    }
} 