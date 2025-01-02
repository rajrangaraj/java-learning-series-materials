/**
 * Specialized logger for audit trail requirements
 */
public class AuditLogger implements AutoCloseable {
    private static final Logger logger = LoggerFactory.getLogger(AuditLogger.class);
    private final String bankId;
    private final Path auditDir;
    private final ObjectMapper objectMapper;
    private final Queue<AuditEntry> auditQueue;
    private final ScheduledExecutorService scheduler;
    
    public AuditLogger(String bankId) {
        this.bankId = bankId;
        this.auditDir = Paths.get("logs", "audit", bankId);
        this.objectMapper = new ObjectMapper();
        this.auditQueue = new ConcurrentLinkedQueue<>();
        this.scheduler = Executors.newSingleThreadScheduledExecutor();
        
        initialize();
    }
    
    private void initialize() {
        try {
            Files.createDirectories(auditDir);
            
            // Schedule periodic flush of audit queue
            scheduler.scheduleAtFixedRate(
                this::flushAuditQueue,
                1, 1, TimeUnit.MINUTES
            );
            
        } catch (IOException e) {
            logger.error("Failed to initialize audit logger", e);
        }
    }
    
    public void recordTransaction(Transaction transaction) {
        AuditEntry entry = new AuditEntry(
            "TRANSACTION",
            transaction.getId(),
            LocalDateTime.now(),
            objectMapper.valueToTree(transaction)
        );
        
        queueAuditEntry(entry);
    }
    
    public void recordSecurityEvent(SecurityEvent event) {
        AuditEntry entry = new AuditEntry(
            "SECURITY",
            event.getId(),
            LocalDateTime.now(),
            objectMapper.valueToTree(event)
        );
        
        queueAuditEntry(entry);
    }
    
    public void recordSystemEvent(SystemEvent event) {
        AuditEntry entry = new AuditEntry(
            "SYSTEM",
            event.getId(),
            LocalDateTime.now(),
            objectMapper.valueToTree(event)
        );
        
        queueAuditEntry(entry);
    }
    
    public void recordError(String message, Exception e, Object context) {
        Map<String, Object> errorDetails = new HashMap<>();
        errorDetails.put("message", message);
        errorDetails.put("exception", e.getClass().getName());
        errorDetails.put("stackTrace", getStackTraceAsString(e));
        errorDetails.put("context", context);
        
        AuditEntry entry = new AuditEntry(
            "ERROR",
            UUID.randomUUID().toString(),
            LocalDateTime.now(),
            objectMapper.valueToTree(errorDetails)
        );
        
        queueAuditEntry(entry);
    }
    
    private void queueAuditEntry(AuditEntry entry) {
        auditQueue.offer(entry);
        
        // Flush if queue is getting too large
        if (auditQueue.size() >= 1000) {
            flushAuditQueue();
        }
    }
    
    private void flushAuditQueue() {
        if (auditQueue.isEmpty()) {
            return;
        }
        
        Path auditFile = auditDir.resolve(
            String.format("audit_%s.json",
                LocalDate.now().format(DateTimeFormatter.ISO_DATE)));
        
        try {
            List<AuditEntry> entries = new ArrayList<>();
            AuditEntry entry;
            while ((entry = auditQueue.poll()) != null) {
                entries.add(entry);
            }
            
            // Write entries to file
            if (!entries.isEmpty()) {
                synchronized (this) {
                    if (Files.exists(auditFile)) {
                        objectMapper.writeValue(
                            Files.newBufferedWriter(auditFile,
                                StandardOpenOption.APPEND),
                            entries);
                    } else {
                        objectMapper.writeValue(auditFile.toFile(), entries);
                    }
                }
            }
            
        } catch (IOException e) {
            logger.error("Failed to flush audit entries", e);
            
            // Re-queue failed entries
            entries.forEach(auditQueue::offer);
        }
    }
    
    private String getStackTraceAsString(Exception e) {
        StringWriter sw = new StringWriter();
        e.printStackTrace(new PrintWriter(sw));
        return sw.toString();
    }
    
    @Override
    public void close() {
        scheduler.shutdown();
        try {
            scheduler.awaitTermination(1, TimeUnit.MINUTES);
            flushAuditQueue();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            logger.error("Interrupted while closing audit logger", e);
        }
    }
    
    private static class AuditEntry {
        private final String type;
        private final String id;
        private final LocalDateTime timestamp;
        private final JsonNode data;
        
        public AuditEntry(String type, String id, 
                         LocalDateTime timestamp, JsonNode data) {
            this.type = type;
            this.id = id;
            this.timestamp = timestamp;
            this.data = data;
        }
        
        // Getters omitted for brevity
    }
} 