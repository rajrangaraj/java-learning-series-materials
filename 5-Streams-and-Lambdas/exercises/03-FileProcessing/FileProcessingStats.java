/**
 * Statistics for file processing operations
 */
public class FileProcessingStats {
    private final AtomicLong processedFiles;
    private final AtomicLong failedFiles;
    private final AtomicLong watchedFiles;
    private final AtomicLong totalBytes;
    
    public FileProcessingStats() {
        this.processedFiles = new AtomicLong();
        this.failedFiles = new AtomicLong();
        this.watchedFiles = new AtomicLong();
        this.totalBytes = new AtomicLong();
    }
    
    public void recordProcessedFile() {
        processedFiles.incrementAndGet();
    }
    
    public void recordFailedFile() {
        failedFiles.incrementAndGet();
    }
    
    public void recordWatchedFile() {
        watchedFiles.incrementAndGet();
    }
    
    public void addBytes(long bytes) {
        totalBytes.addAndGet(bytes);
    }
    
    public long getProcessedFiles() {
        return processedFiles.get();
    }
    
    public long getFailedFiles() {
        return failedFiles.get();
    }
    
    public long getWatchedFiles() {
        return watchedFiles.get();
    }
    
    public long getTotalBytes() {
        return totalBytes.get();
    }
    
    @Override
    public String toString() {
        return String.format(
            "FileProcessingStats{processed=%d, failed=%d, watched=%d, bytes=%d}",
            processedFiles.get(), failedFiles.get(), 
            watchedFiles.get(), totalBytes.get());
    }
} 