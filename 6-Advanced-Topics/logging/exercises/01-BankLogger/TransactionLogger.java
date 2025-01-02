/**
 * Specialized logger for financial transactions
 */
public class TransactionLogger implements AutoCloseable {
    private static final Logger logger = LoggerFactory.getLogger(TransactionLogger.class);
    private final String bankId;
    private final Path transactionDir;
    private final ObjectMapper objectMapper;
    private final BlockingQueue<Transaction> transactionQueue;
    private final ExecutorService executor;
    private volatile boolean running;
    
    public TransactionLogger(String bankId) {
        this.bankId = bankId;
        this.transactionDir = Paths.get("logs", "transactions", bankId);
        this.objectMapper = new ObjectMapper();
        this.transactionQueue = new LinkedBlockingQueue<>(10000);
        this.executor = Executors.newSingleThreadExecutor();
        this.running = true;
        
        initialize();
    }
    
    private void initialize() {
        try {
            Files.createDirectories(transactionDir);
            
            // Start transaction processing thread
            executor.submit(this::processTransactionQueue);
            
        } catch (IOException e) {
            logger.error("Failed to initialize transaction logger", e);
        }
    }
    
    public void logDetailedTransaction(Transaction transaction) {
        try {
            // Add detailed tracking information
            transaction.setDetailedLogging(true);
            transaction.setTrackingData(generateTrackingData());
            
            // Queue for processing
            if (!transactionQueue.offer(transaction, 5, TimeUnit.SECONDS)) {
                logger.error("Failed to queue transaction: queue full");
            }
            
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            logger.error("Interrupted while queueing transaction", e);
        }
    }
    
    public void logBasicTransaction(Transaction transaction) {
        try {
            // Queue for processing
            if (!transactionQueue.offer(transaction, 1, TimeUnit.SECONDS)) {
                logger.error("Failed to queue transaction: queue full");
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            logger.error("Interrupted while queueing transaction", e);
        }
    }
    
    private void processTransactionQueue() {
        while (running || !transactionQueue.isEmpty()) {
            List<Transaction> batch = new ArrayList<>();
            try {
                // Wait for first transaction
                Transaction transaction = transactionQueue.poll(1, TimeUnit.SECONDS);
                if (transaction != null) {
                    batch.add(transaction);
                    
                    // Drain queue up to batch size
                    transactionQueue.drainTo(batch, 999);
                    
                    if (!batch.isEmpty()) {
                        writeTransactionBatch(batch);
                    }
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                logger.error("Transaction processing interrupted", e);
                break;
            } catch (Exception e) {
                logger.error("Error processing transaction batch", e);
            }
        }
    }
    
    private void writeTransactionBatch(List<Transaction> batch) {
        Path transactionFile = transactionDir.resolve(
            String.format("transactions_%s.json",
                LocalDate.now().format(DateTimeFormatter.ISO_DATE)));
        
        try {
            synchronized (this) {
                if (Files.exists(transactionFile)) {
                    objectMapper.writeValue(
                        Files.newBufferedWriter(transactionFile,
                            StandardOpenOption.APPEND),
                        batch);
                } else {
                    objectMapper.writeValue(transactionFile.toFile(), batch);
                }
            }
        } catch (IOException e) {
            logger.error("Failed to write transaction batch", e);
            
            // Attempt to requeue failed transactions
            batch.forEach(t -> {
                try {
                    transactionQueue.put(t);
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                }
            });
        }
    }
    
    private Map<String, Object> generateTrackingData() {
        Map<String, Object> trackingData = new HashMap<>();
        trackingData.put("timestamp", LocalDateTime.now());
        trackingData.put("systemInfo", getSystemInfo());
        trackingData.put("processingNode", getProcessingNode());
        return trackingData;
    }
    
    private Map<String, String> getSystemInfo() {
        Map<String, String> systemInfo = new HashMap<>();
        systemInfo.put("javaVersion", System.getProperty("java.version"));
        systemInfo.put("osName", System.getProperty("os.name"));
        systemInfo.put("osVersion", System.getProperty("os.version"));
        return systemInfo;
    }
    
    private String getProcessingNode() {
        try {
            return InetAddress.getLocalHost().getHostName();
        } catch (UnknownHostException e) {
            return "unknown";
        }
    }
    
    @Override
    public void close() {
        running = false;
        executor.shutdown();
        try {
            if (!executor.awaitTermination(1, TimeUnit.MINUTES)) {
                executor.shutdownNow();
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            executor.shutdownNow();
        }
    }
} 