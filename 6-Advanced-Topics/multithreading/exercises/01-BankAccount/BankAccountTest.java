/**
 * Tests for thread-safe bank account implementation
 */
public class BankAccountTest {
    private BankAccount account;
    private ExecutorService executor;
    
    @BeforeEach
    void setUp() {
        account = new BankAccount("TEST-001", 1000.0);
        executor = Executors.newFixedThreadPool(10);
    }
    
    @AfterEach
    void tearDown() {
        executor.shutdown();
    }
    
    @Test
    void testConcurrentDeposits() throws InterruptedException {
        int numThreads = 100;
        double depositAmount = 100.0;
        CountDownLatch latch = new CountDownLatch(numThreads);
        
        for (int i = 0; i < numThreads; i++) {
            executor.submit(() -> {
                try {
                    account.deposit(depositAmount);
                } finally {
                    latch.countDown();
                }
            });
        }
        
        latch.await(5, TimeUnit.SECONDS);
        assertEquals(1000.0 + (numThreads * depositAmount), account.getBalance());
    }
    
    @Test
    void testConcurrentWithdrawals() throws InterruptedException {
        int numThreads = 5;
        double withdrawAmount = 200.0;
        CountDownLatch latch = new CountDownLatch(numThreads);
        AtomicInteger successCount = new AtomicInteger(0);
        
        for (int i = 0; i < numThreads; i++) {
            executor.submit(() -> {
                try {
                    if (account.withdraw(withdrawAmount)) {
                        successCount.incrementAndGet();
                    }
                } finally {
                    latch.countDown();
                }
            });
        }
        
        latch.await(5, TimeUnit.SECONDS);
        assertEquals(1000.0 - (successCount.get() * withdrawAmount), account.getBalance());
    }
    
    @Test
    void testConcurrentTransfers() throws InterruptedException {
        BankAccount account2 = new BankAccount("TEST-002", 1000.0);
        int numThreads = 10;
        double transferAmount = 100.0;
        CountDownLatch latch = new CountDownLatch(numThreads * 2);
        
        for (int i = 0; i < numThreads; i++) {
            executor.submit(() -> {
                try {
                    account.transfer(account2, transferAmount);
                } finally {
                    latch.countDown();
                }
            });
            
            executor.submit(() -> {
                try {
                    account2.transfer(account, transferAmount);
                } finally {
                    latch.countDown();
                }
            });
        }
        
        latch.await(5, TimeUnit.SECONDS);
        assertEquals(1000.0, account.getBalance());
        assertEquals(1000.0, account2.getBalance());
    }
    
    @Test
    void testTransactionHistory() throws InterruptedException {
        account.deposit(100.0);
        account.withdraw(50.0);
        
        BankAccount account2 = new BankAccount("TEST-002", 1000.0);
        account.transfer(account2, 200.0);
        
        List<Transaction> history = account.getTransactionHistory();
        assertEquals(3, history.size());
        
        assertEquals("DEPOSIT", history.get(0).getType());
        assertEquals(100.0, history.get(0).getAmount());
        
        assertEquals("WITHDRAW", history.get(1).getType());
        assertEquals(-50.0, history.get(1).getAmount());
        
        assertEquals("TRANSFER_OUT", history.get(2).getType());
        assertEquals(-200.0, history.get(2).getAmount());
        assertEquals("TEST-002", history.get(2).getRelatedAccount());
    }
} 