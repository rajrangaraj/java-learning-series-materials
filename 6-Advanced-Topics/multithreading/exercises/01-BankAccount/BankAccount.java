/**
 * Thread-safe bank account implementation
 */
public class BankAccount {
    private final String accountId;
    private final AtomicDouble balance;
    private final ReentrantLock lock;
    private final Condition sufficientFunds;
    private final List<Transaction> transactions;
    
    public BankAccount(String accountId, double initialBalance) {
        this.accountId = accountId;
        this.balance = new AtomicDouble(initialBalance);
        this.lock = new ReentrantLock();
        this.sufficientFunds = lock.newCondition();
        this.transactions = new CopyOnWriteArrayList<>();
    }
    
    /**
     * Deposits money into the account
     */
    public void deposit(double amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("Amount must be positive");
        }
        
        lock.lock();
        try {
            balance.addAndGet(amount);
            transactions.add(new Transaction("DEPOSIT", amount));
            sufficientFunds.signalAll();
        } finally {
            lock.unlock();
        }
    }
    
    /**
     * Withdraws money from the account
     */
    public boolean withdraw(double amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("Amount must be positive");
        }
        
        lock.lock();
        try {
            while (balance.get() < amount) {
                if (!sufficientFunds.await(1, TimeUnit.SECONDS)) {
                    return false;
                }
            }
            
            balance.addAndGet(-amount);
            transactions.add(new Transaction("WITHDRAW", -amount));
            return true;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return false;
        } finally {
            lock.unlock();
        }
    }
    
    /**
     * Transfers money to another account
     */
    public boolean transfer(BankAccount target, double amount) {
        if (this == target) {
            throw new IllegalArgumentException("Cannot transfer to same account");
        }
        
        // Prevent deadlocks by always locking accounts in the same order
        BankAccount first = this.accountId.compareTo(target.accountId) < 0 ? this : target;
        BankAccount second = first == this ? target : this;
        
        first.lock.lock();
        try {
            second.lock.lock();
            try {
                if (balance.get() < amount) {
                    return false;
                }
                
                balance.addAndGet(-amount);
                target.balance.addAndGet(amount);
                
                transactions.add(new Transaction("TRANSFER_OUT", -amount, target.accountId));
                target.transactions.add(new Transaction("TRANSFER_IN", amount, this.accountId));
                
                return true;
            } finally {
                second.lock.unlock();
            }
        } finally {
            first.lock.unlock();
        }
    }
    
    /**
     * Gets current balance
     */
    public double getBalance() {
        return balance.get();
    }
    
    /**
     * Gets transaction history
     */
    public List<Transaction> getTransactionHistory() {
        return new ArrayList<>(transactions);
    }
    
    /**
     * Represents a single transaction
     */
    public static class Transaction {
        private final String type;
        private final double amount;
        private final String relatedAccount;
        private final LocalDateTime timestamp;
        
        public Transaction(String type, double amount) {
            this(type, amount, null);
        }
        
        public Transaction(String type, double amount, String relatedAccount) {
            this.type = type;
            this.amount = amount;
            this.relatedAccount = relatedAccount;
            this.timestamp = LocalDateTime.now();
        }
        
        @Override
        public String toString() {
            return String.format("%s: %.2f%s at %s", 
                type, amount, 
                relatedAccount != null ? " with " + relatedAccount : "",
                timestamp);
        }
    }
} 