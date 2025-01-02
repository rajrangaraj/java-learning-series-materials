/**
 * Exercise: Implement a resilient data processing pipeline with comprehensive error handling
 */
public class DataProcessor {
    private static final Logger logger = LoggerFactory.getLogger(DataProcessor.class);
    private final DataSource source;
    private final DataTransformer transformer;
    private final DataValidator validator;
    private final DataSink sink;
    private final ErrorHandler errorHandler;
    private final ProcessingStats stats;
    private final CircuitBreaker circuitBreaker;
    private volatile boolean running;
    
    public DataProcessor(DataSource source, DataTransformer transformer,
                        DataValidator validator, DataSink sink,
                        ErrorHandler errorHandler) {
        this.source = source;
        this.transformer = transformer;
        this.validator = validator;
        this.sink = sink;
        this.errorHandler = errorHandler;
        this.stats = new ProcessingStats();
        this.circuitBreaker = new CircuitBreaker(5, Duration.ofMinutes(1));
        this.running = true;
    }
    
    public void startProcessing() {
        ExecutorService executor = Executors.newFixedThreadPool(4);
        BlockingQueue<DataRecord> queue = new LinkedBlockingQueue<>(1000);
        
        // Start producer thread
        executor.submit(() -> produceRecords(queue));
        
        // Start consumer threads
        for (int i = 0; i < 3; i++) {
            executor.submit(() -> processRecords(queue));
        }
        
        // Monitor thread
        executor.submit(this::monitorProcessing);
        
        Runtime.getRuntime().addShutdownHook(new Thread(this::shutdown));
    }
    
    private void produceRecords(BlockingQueue<DataRecord> queue) {
        while (running) {
            try {
                if (circuitBreaker.isAllowed()) {
                    List<DataRecord> records = source.fetchRecords();
                    for (DataRecord record : records) {
                        if (!queue.offer(record, 1, TimeUnit.SECONDS)) {
                            logger.warn("Queue full, backing off...");
                            Thread.sleep(1000);
                        }
                    }
                    circuitBreaker.recordSuccess();
                    stats.recordBatchFetch();
                } else {
                    logger.warn("Circuit breaker open, waiting...");
                    Thread.sleep(5000);
                }
            } catch (Exception e) {
                circuitBreaker.recordFailure();
                handleProducerError(e);
            }
        }
    }
    
    private void processRecords(BlockingQueue<DataRecord> queue) {
        while (running) {
            try {
                DataRecord record = queue.poll(1, TimeUnit.SECONDS);
                if (record != null) {
                    processRecord(record);
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            } catch (Exception e) {
                logger.error("Unexpected error in consumer thread", e);
                stats.recordError();
            }
        }
    }
    
    private void processRecord(DataRecord record) {
        try {
            // Transform data
            DataRecord transformed = transformer.transform(record);
            
            // Validate
            ValidationResult validation = validator.validate(transformed);
            if (!validation.isValid()) {
                handleValidationError(record, validation);
                return;
            }
            
            // Write to sink with retry
            retry(3, () -> sink.write(transformed));
            
            stats.recordSuccess();
            
        } catch (TransformationException e) {
            handleTransformationError(record, e);
        } catch (ValidationException e) {
            handleValidationError(record, e.getValidationResult());
        } catch (SinkException e) {
            handleSinkError(record, e);
        } catch (Exception e) {
            handleUnexpectedError(record, e);
        }
    }
    
    private <T> T retry(int maxAttempts, Supplier<T> operation) 
            throws Exception {
        Exception lastException = null;
        
        for (int attempt = 1; attempt <= maxAttempts; attempt++) {
            try {
                return operation.get();
            } catch (Exception e) {
                lastException = e;
                if (attempt < maxAttempts) {
                    long delay = (long) Math.pow(2, attempt) * 100; // Exponential backoff
                    Thread.sleep(delay);
                    logger.warn("Retry attempt {} after error: {}", 
                        attempt, e.getMessage());
                }
            }
        }
        
        throw new RetryExhaustedException("Max retry attempts reached", 
            lastException);
    }
    
    private void handleProducerError(Exception e) {
        stats.recordSourceError();
        if (e instanceof SourceUnavailableException) {
            logger.error("Data source unavailable: {}", e.getMessage());
            errorHandler.handleSourceError((SourceUnavailableException) e);
        } else {
            logger.error("Error fetching records", e);
            errorHandler.handleUnexpectedError(e);
        }
    }
    
    private void handleTransformationError(DataRecord record, 
            TransformationException e) {
        stats.recordTransformationError();
        logger.error("Transformation error for record {}: {}", 
            record.getId(), e.getMessage());
        errorHandler.handleTransformationError(record, e);
    }
    
    private void handleValidationError(DataRecord record, 
            ValidationResult validation) {
        stats.recordValidationError();
        logger.warn("Validation failed for record {}: {}", 
            record.getId(), validation.getErrors());
        errorHandler.handleValidationError(record, validation);
    }
    
    private void handleSinkError(DataRecord record, SinkException e) {
        stats.recordSinkError();
        logger.error("Error writing record {} to sink: {}", 
            record.getId(), e.getMessage());
        errorHandler.handleSinkError(record, e);
    }
    
    private void handleUnexpectedError(DataRecord record, Exception e) {
        stats.recordUnexpectedError();
        logger.error("Unexpected error processing record {}", 
            record.getId(), e);
        errorHandler.handleUnexpectedError(e);
    }
    
    private void monitorProcessing() {
        while (running) {
            try {
                Thread.sleep(60000); // Monitor every minute
                
                ProcessingStats currentStats = stats.snapshot();
                logger.info("Processing stats: {}", currentStats);
                
                if (currentStats.getErrorRate() > 0.25) { // >25% error rate
                    logger.error("High error rate detected: {}%", 
                        currentStats.getErrorRate() * 100);
                    errorHandler.handleHighErrorRate(currentStats);
                }
                
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
    }
    
    public void shutdown() {
        running = false;
        try {
            source.close();
            sink.close();
        } catch (Exception e) {
            logger.error("Error during shutdown", e);
        }
    }
    
    public ProcessingStats getStats() {
        return stats.snapshot();
    }
} 