/**
 * Statistics tracking for file processing
 */
public class ProcessingStats {
    private final AtomicInteger processedFiles;
    private final AtomicInteger validationErrors;
    private final AtomicInteger processingErrors;
    private final AtomicInteger unexpectedErrors;
    private final AtomicLong processedBytes;
    private final AtomicLong startTime;
    private final Map<String, AtomicInteger> errorCodes;
    
    public ProcessingStats() {
        this.processedFiles = new AtomicInteger();
        this.validationErrors = new AtomicInteger();
        this.processingErrors = new AtomicInteger();
        this.unexpectedErrors = new AtomicInteger();
        this.processedBytes = new AtomicLong();
        this.startTime = new AtomicLong(System.currentTimeMillis());
        this.errorCodes = new ConcurrentHashMap<>();
    }
    
    public void recordSuccess() {
        processedFiles.incrementAndGet();
    }
    
    public void recordValidationError() {
        validationErrors.incrementAndGet();
    }
    
    public void recordProcessingError() {
        processingErrors.incrementAndGet();
    }
    
    public void recordUnexpectedError() {
        unexpectedErrors.incrementAndGet();
    }
    
    public void recordBytes(long bytes) {
        processedBytes.addAndGet(bytes);
    }
    
    public void recordErrorCode(String code) {
        errorCodes.computeIfAbsent(code, k -> new AtomicInteger())
                 .incrementAndGet();
    }
    
    public int getProcessedFiles() {
        return processedFiles.get();
    }
    
    public int getValidationErrors() {
        return validationErrors.get();
    }
    
    public int getProcessingErrors() {
        return processingErrors.get();
    }
    
    public int getUnexpectedErrors() {
        return unexpectedErrors.get();
    }
    
    public long getProcessedBytes() {
        return processedBytes.get();
    }
    
    public long getProcessingTime() {
        return System.currentTimeMillis() - startTime.get();
    }
    
    public Map<String, Integer> getErrorCodeCounts() {
        return errorCodes.entrySet().stream()
            .collect(Collectors.toMap(
                Map.Entry::getKey,
                e -> e.getValue().get()
            ));
    }
    
    public double getSuccessRate() {
        int total = processedFiles.get() + validationErrors.get() + 
                   processingErrors.get() + unexpectedErrors.get();
        return total == 0 ? 0 : (double) processedFiles.get() / total;
    }
    
    public double getProcessingSpeed() {
        long time = getProcessingTime() / 1000; // Convert to seconds
        return time == 0 ? 0 : (double) processedBytes.get() / time;
    }
    
    @Override
    public String toString() {
        return String.format(
            "ProcessingStats{processed=%d, validationErrors=%d, " +
            "processingErrors=%d, unexpectedErrors=%d, " +
            "bytes=%d, time=%dms, successRate=%.2f%%, " +
            "speed=%.2f bytes/sec, errorCodes=%s}",
            processedFiles.get(),
            validationErrors.get(),
            processingErrors.get(),
            unexpectedErrors.get(),
            processedBytes.get(),
            getProcessingTime(),
            getSuccessRate() * 100,
            getProcessingSpeed(),
            getErrorCodeCounts()
        );
    }
} 