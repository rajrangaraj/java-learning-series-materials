/**
 * Custom exceptions for bank account operations
 */
public class AccountException extends Exception {
    private final String accountNumber;
    private final String errorCode;

    public AccountException(String message, String accountNumber, String errorCode) {
        super(message);
        this.accountNumber = accountNumber;
        this.errorCode = errorCode;
    }

    public static class InsufficientFundsException extends AccountException {
        public InsufficientFundsException(String accountNumber, double requested, double available) {
            super(String.format("Insufficient funds: Requested %.2f, Available %.2f", 
                  requested, available), accountNumber, "ERR_INSUFFICIENT_FUNDS");
        }
    }

    public static class WithdrawalLimitExceededException extends AccountException {
        public WithdrawalLimitExceededException(String accountNumber, int limit) {
            super(String.format("Monthly withdrawal limit of %d exceeded", limit), 
                  accountNumber, "ERR_WITHDRAWAL_LIMIT");
        }
    }

    // Getters
    public String getAccountNumber() { return accountNumber; }
    public String getErrorCode() { return errorCode; }
} 