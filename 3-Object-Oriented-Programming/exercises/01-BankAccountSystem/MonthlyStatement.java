/**
 * Class to generate and manage monthly account statements
 */
public class MonthlyStatement {
    private String accountNumber;
    private LocalDate startDate;
    private LocalDate endDate;
    private double openingBalance;
    private double closingBalance;
    private List<Transaction> transactions;
    private double totalDeposits;
    private double totalWithdrawals;
    private double interestEarned;

    public MonthlyStatement(String accountNumber, LocalDate month) {
        this.accountNumber = accountNumber;
        this.startDate = month.withDayOfMonth(1);
        this.endDate = month.withDayOfMonth(month.lengthOfMonth());
        this.transactions = new ArrayList<>();
    }

    public void addTransaction(Transaction transaction) {
        transactions.add(transaction);
        updateTotals(transaction);
    }

    private void updateTotals(Transaction transaction) {
        switch (transaction.getType()) {
            case DEPOSIT:
            case TRANSFER_IN:
                totalDeposits += transaction.getAmount();
                break;
            case WITHDRAWAL:
            case TRANSFER_OUT:
                totalWithdrawals += transaction.getAmount();
                break;
            case INTEREST:
                interestEarned += transaction.getAmount();
                break;
        }
    }

    public String generateStatement() {
        StringBuilder statement = new StringBuilder();
        statement.append(String.format("Monthly Statement for Account: %s\n", accountNumber));
        statement.append(String.format("Period: %s to %s\n\n", startDate, endDate));
        statement.append(String.format("Opening Balance: $%.2f\n", openingBalance));
        statement.append("\nTransactions:\n");
        statement.append("Date       | Type          | Amount    | Balance\n");
        statement.append("------------------------------------------------\n");
        
        for (Transaction t : transactions) {
            statement.append(String.format("%s | %-12s | $%8.2f | $%8.2f\n",
                t.getTimestamp().toLocalDate(),
                t.getType().getDescription(),
                t.getAmount(),
                t.getBalanceAfter()));
        }

        statement.append("\nSummary:\n");
        statement.append(String.format("Total Deposits:    $%.2f\n", totalDeposits));
        statement.append(String.format("Total Withdrawals: $%.2f\n", totalWithdrawals));
        statement.append(String.format("Interest Earned:   $%.2f\n", interestEarned));
        statement.append(String.format("Closing Balance:   $%.2f\n", closingBalance));

        return statement.toString();
    }

    // Getters and setters
    public void setOpeningBalance(double openingBalance) {
        this.openingBalance = openingBalance;
    }

    public void setClosingBalance(double closingBalance) {
        this.closingBalance = closingBalance;
    }
} 