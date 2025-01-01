/**
 * Class to track account activities and maintain transaction history
 */
public class Transaction {
    private String transactionId;
    private LocalDateTime timestamp;
    private TransactionType type;
    private double amount;
    private String description;
    private double balanceAfter;
    
    private static int transactionCounter = 1;
    
    public Transaction(TransactionType type, double amount, String description, 
                      double balanceAfter) {
        this.transactionId = generateTransactionId();
        this.timestamp = LocalDateTime.now();
        this.type = type;
        this.amount = amount;
        this.description = description;
        this.balanceAfter = balanceAfter;
    }
    
    private String generateTransactionId() {
        return String.format("TXN%07d", transactionCounter++);
    }
    
    // Getters
    public String getTransactionId() {
        return transactionId;
    }
    
    public LocalDateTime getTimestamp() {
        return timestamp;
    }
    
    public TransactionType getType() {
        return type;
    }
    
    public double getAmount() {
        return amount;
    }
    
    public String getDescription() {
        return description;
    }
    
    public double getBalanceAfter() {
        return balanceAfter;
    }
    
    /**
     * Returns a formatted string representation of the transaction amount
     * with appropriate sign based on transaction type
     */
    public String getFormattedAmount() {
        String sign = switch(type) {
            case DEPOSIT, TRANSFER_IN, INTEREST -> "+";
            case WITHDRAWAL, TRANSFER_OUT, FEE -> "-";
        };
        return String.format("%s$%.2f", sign, amount);
    }
    
    /**
     * Returns a formatted date string for the transaction
     */
    public String getFormattedDate() {
        return timestamp.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }
    
    @Override
    public String toString() {
        return String.format("[%s] %s | %-15s | %10s | Balance: $%.2f | %s",
            getFormattedDate(),
            transactionId,
            type.getDescription(),
            getFormattedAmount(),
            balanceAfter,
            description
        );
    }
    
    /**
     * Returns a compact single-line summary of the transaction
     */
    public String toSummary() {
        return String.format("%s %-15s %10s",
            timestamp.format(DateTimeFormatter.ofPattern("MM/dd")),
            type.getDescription(),
            getFormattedAmount()
        );
    }
    
    /**
     * Returns a detailed multi-line description of the transaction
     */
    public String toDetailedString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Transaction Details\n");
        sb.append("==================\n");
        sb.append(String.format("ID:          %s\n", transactionId));
        sb.append(String.format("Date/Time:   %s\n", getFormattedDate()));
        sb.append(String.format("Type:        %s\n", type.getDescription()));
        sb.append(String.format("Amount:      %s\n", getFormattedAmount()));
        sb.append(String.format("Balance:     $%.2f\n", balanceAfter));
        sb.append(String.format("Description: %s\n", description));
        return sb.toString();
    }
    
    /**
     * Compares two transactions for equality
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Transaction that = (Transaction) o;
        return transactionId.equals(that.transactionId);
    }
    
    /**
     * Generates hash code based on transaction ID
     */
    @Override
    public int hashCode() {
        return Objects.hash(transactionId);
    }
    
    /**
     * Creates a copy of the transaction
     */
    public Transaction clone() {
        Transaction clone = new Transaction(type, amount, description, balanceAfter);
        clone.transactionId = this.transactionId;
        clone.timestamp = this.timestamp;
        return clone;
    }
} 