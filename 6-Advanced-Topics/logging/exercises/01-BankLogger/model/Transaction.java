/**
 * Represents a banking transaction
 */
public class Transaction {
    private String id;
    private String accountNumber;
    private BigDecimal amount;
    private TransactionType type;
    private LocalDateTime timestamp;
    private String bankId;
    private String processingEnvironment;
    private LocalDateTime loggedAt;
    private boolean detailedLogging;
    private Map<String, Object> trackingData;
    
    // Constructor, getters, and setters omitted for brevity
} 