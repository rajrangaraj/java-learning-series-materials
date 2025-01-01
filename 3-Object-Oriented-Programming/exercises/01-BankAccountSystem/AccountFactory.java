/**
 * Factory class for creating different types of accounts
 */
public class AccountFactory {
    private static final AccountManager accountManager = new AccountManager();

    public static Account createAccount(String type, String holder, double initialDeposit) 
            throws AccountException {
        String accountNumber = accountManager.generateAccountNumber();
        
        Account account = switch (type.toUpperCase()) {
            case "SAVINGS" -> new SavingsAccount(accountNumber, holder, initialDeposit, 2.5);
            case "CHECKING" -> new CheckingAccount(accountNumber, holder, initialDeposit, true);
            default -> throw new IllegalArgumentException("Invalid account type: " + type);
        };

        accountManager.addAccount(account);
        return account;
    }

    public static Account createSavingsAccount(String holder, double initialDeposit, 
            double interestRate) throws AccountException {
        String accountNumber = accountManager.generateAccountNumber();
        SavingsAccount account = new SavingsAccount(accountNumber, holder, 
                                                  initialDeposit, interestRate);
        accountManager.addAccount(account);
        return account;
    }

    public static Account createCheckingAccount(String holder, double initialDeposit, 
            boolean overdraftProtection) throws AccountException {
        String accountNumber = accountManager.generateAccountNumber();
        CheckingAccount account = new CheckingAccount(accountNumber, holder, 
                                                    initialDeposit, overdraftProtection);
        accountManager.addAccount(account);
        return account;
    }
} 