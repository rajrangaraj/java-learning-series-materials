/**
 * Tests for BankLogger implementation
 */
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class BankLoggerTest {
    private Path tempDir;
    private BankLogger logger;
    private String bankId;
    
    @BeforeAll
    void setUp() throws IOException {
        tempDir = Files.createTempDirectory("bank-logger-test");
        bankId = "TEST_BANK";
        logger = new BankLogger(bankId, SecurityLevel.HIGH);
    }
    
    @AfterAll
    void tearDown() throws IOException {
        logger.close();
        deleteDirectory(tempDir);
    }
    
    @Test
    void testLogTransaction() throws IOException {
        // Create test transaction
        Transaction transaction = new Transaction();
        transaction.setId(UUID.randomUUID().toString());
        transaction.setAccountNumber("1234567890");
        transaction.setAmount(new BigDecimal("1000.00"));
        transaction.setType(TransactionType.DEPOSIT);
        
        // Log transaction
        logger.logTransaction(transaction);
        
        // Verify transaction log file exists
        Path transactionFile = Paths.get("logs", "transactions", bankId,
            "transactions_" + LocalDate.now().format(DateTimeFormatter.ISO_DATE) + ".json");
        assertTrue(Files.exists(transactionFile));
        
        // Verify transaction was logged correctly
        JsonNode root = new ObjectMapper().readTree(transactionFile.toFile());
        assertTrue(root.isArray());
        JsonNode loggedTransaction = root.get(0);
        assertEquals(transaction.getId(), loggedTransaction.get("id").asText());
        assertEquals("****7890", loggedTransaction.get("accountNumber").asText());
    }
    
    @Test
    void testLogLargeTransaction() throws IOException {
        // Create large transaction
        Transaction transaction = new Transaction();
        transaction.setId(UUID.randomUUID().toString());
        transaction.setAccountNumber("1234567890");
        transaction.setAmount(new BigDecimal("20000.00"));
        transaction.setType(TransactionType.TRANSFER);
        
        // Log transaction
        logger.logTransaction(transaction);
        
        // Verify audit log exists
        Path auditFile = Paths.get("logs", "audit", bankId,
            "audit_" + LocalDate.now().format(DateTimeFormatter.ISO_DATE) + ".json");
        assertTrue(Files.exists(auditFile));
        
        // Verify audit entry
        JsonNode root = new ObjectMapper().readTree(auditFile.toFile());
        assertTrue(root.isArray());
        JsonNode auditEntry = root.get(0);
        assertEquals("TRANSACTION", auditEntry.get("type").asText());
        assertTrue(auditEntry.has("data"));
    }
    
    @Test
    void testLogSecurityEvent() {
        // Create security event
        SecurityEvent event = new SecurityEvent();
        event.setId(UUID.randomUUID().toString());
        event.setType(SecurityEventType.UNAUTHORIZED_ACCESS);
        event.setSeverity(Severity.HIGH);
        event.setLocation("192.168.1.1");
        event.setUserId("user123");
        
        // Log security event
        logger.logSecurityEvent(event);
        
        // Verify alert was triggered (check logs)
        // Note: In a real test, we might want to mock the AlertLogger
        // and verify it was called with the correct parameters
    }
    
    @Test
    void testLogSystemEvent() {
        // Create system event
        SystemEvent event = new SystemEvent(
            SystemEventType.MAINTENANCE,
            "DATABASE",
            "BACKUP"
        );
        event.setRequiresAudit(true);
        event.addParameter("backupType", "FULL");
        
        // Log system event
        logger.logSystemEvent(event);
        
        // Verify event was logged and audit trail created
        // Note: In a real test, we might want to verify the actual log files
    }
    
    @Test
    void testConcurrentLogging() throws InterruptedException {
        int threadCount = 10;
        int eventsPerThread = 100;
        CountDownLatch latch = new CountDownLatch(threadCount);
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        
        // Submit logging tasks
        for (int i = 0; i < threadCount; i++) {
            executor.submit(() -> {
                try {
                    for (int j = 0; j < eventsPerThread; j++) {
                        // Log different types of events
                        logRandomEvent();
                    }
                } finally {
                    latch.countDown();
                }
            });
        }
        
        // Wait for all threads to complete
        assertTrue(latch.await(30, TimeUnit.SECONDS));
        executor.shutdown();
        
        // Verify logs were created correctly
        // Note: In a real test, we might want to verify file integrity
        // and count the number of logged events
    }
    
    @Test
    void testErrorHandling() {
        // Test logging with null values
        assertDoesNotThrow(() -> logger.logTransaction(null));
        
        // Test logging with invalid data
        Transaction invalidTransaction = new Transaction();
        assertDoesNotThrow(() -> logger.logTransaction(invalidTransaction));
        
        // Test logging during file system errors
        // Note: In a real test, we might want to simulate file system errors
        // and verify error handling behavior
    }
    
    private void logRandomEvent() {
        switch (new Random().nextInt(3)) {
            case 0:
                Transaction tx = createRandomTransaction();
                logger.logTransaction(tx);
                break;
            case 1:
                SecurityEvent secEvent = createRandomSecurityEvent();
                logger.logSecurityEvent(secEvent);
                break;
            case 2:
                SystemEvent sysEvent = createRandomSystemEvent();
                logger.logSystemEvent(sysEvent);
                break;
        }
    }
    
    private Transaction createRandomTransaction() {
        Transaction tx = new Transaction();
        tx.setId(UUID.randomUUID().toString());
        tx.setAccountNumber("ACC" + new Random().nextInt(10000));
        tx.setAmount(new BigDecimal(new Random().nextInt(10000)));
        tx.setType(TransactionType.values()[
            new Random().nextInt(TransactionType.values().length)]);
        return tx;
    }
    
    private SecurityEvent createRandomSecurityEvent() {
        SecurityEvent event = new SecurityEvent();
        event.setId(UUID.randomUUID().toString());
        event.setType(SecurityEventType.values()[
            new Random().nextInt(SecurityEventType.values().length)]);
        event.setSeverity(Severity.values()[
            new Random().nextInt(Severity.values().length)]);
        return event;
    }
    
    private SystemEvent createRandomSystemEvent() {
        return new SystemEvent(
            SystemEventType.values()[
                new Random().nextInt(SystemEventType.values().length)],
            "COMPONENT_" + new Random().nextInt(5),
            "OPERATION_" + new Random().nextInt(5)
        );
    }
    
    private void deleteDirectory(Path dir) throws IOException {
        if (Files.exists(dir)) {
            Files.walk(dir)
                 .sorted(Comparator.reverseOrder())
                 .forEach(path -> {
                     try {
                         Files.delete(path);
                     } catch (IOException e) {
                         System.err.println("Failed to delete: " + path);
                     }
                 });
        }
    }
} 