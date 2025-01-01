/**
 * Enum for different types of transactions
 */
public enum TransactionType {
    DEPOSIT("Deposit"),
    WITHDRAWAL("Withdrawal"),
    INTEREST("Interest Applied"),
    TRANSFER_IN("Transfer In"),
    TRANSFER_OUT("Transfer Out"),
    FEE("Service Fee");

    private final String description;

    TransactionType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
} 