/**
 * Statistics for backup operations
 */
public class BackupStats {
    private final AtomicLong backedUpFiles;
    private final AtomicLong backedUpBytes;
    private final AtomicLong skippedFiles;
    private final AtomicLong failedFiles;
    private final AtomicLong restoredFiles;
    private final AtomicLong restoredBytes;
    private final AtomicLong failedRestores;
    private final AtomicLong deletedVersions;
    
    public BackupStats() {
        this.backedUpFiles = new AtomicLong();
        this.backedUpBytes = new AtomicLong();
        this.skippedFiles = new AtomicLong();
        this.failedFiles = new AtomicLong();
        this.restoredFiles = new AtomicLong();
        this.restoredBytes = new AtomicLong();
        this.failedRestores = new AtomicLong();
        this.deletedVersions = new AtomicLong();
    }
    
    public void recordBackedUpFile(long bytes) {
        backedUpFiles.incrementAndGet();
        backedUpBytes.addAndGet(bytes);
    }
    
    public void recordSkippedFile() {
        skippedFiles.incrementAndGet();
    }
    
    public void recordFailedFile() {
        failedFiles.incrementAndGet();
    }
    
    public void recordRestoredFile(long bytes) {
        restoredFiles.incrementAndGet();
        restoredBytes.addAndGet(bytes);
    }
    
    public void recordFailedRestore() {
        failedRestores.incrementAndGet();
    }
    
    public void recordDeletedVersion() {
        deletedVersions.incrementAndGet();
    }
    
    public long getBackedUpFiles() {
        return backedUpFiles.get();
    }
    
    public long getBackedUpBytes() {
        return backedUpBytes.get();
    }
    
    public long getSkippedFiles() {
        return skippedFiles.get();
    }
    
    public long getFailedFiles() {
        return failedFiles.get();
    }
    
    public long getRestoredFiles() {
        return restoredFiles.get();
    }
    
    public long getRestoredBytes() {
        return restoredBytes.get();
    }
    
    public long getFailedRestores() {
        return failedRestores.get();
    }
    
    public long getDeletedVersions() {
        return deletedVersions.get();
    }
    
    @Override
    public String toString() {
        return String.format(
            "BackupStats{backedUp=%d files (%.2f MB), skipped=%d, failed=%d, " +
            "restored=%d files (%.2f MB), failedRestores=%d, deletedVersions=%d}",
            backedUpFiles.get(), backedUpBytes.get() / 1024.0 / 1024.0,
            skippedFiles.get(), failedFiles.get(),
            restoredFiles.get(), restoredBytes.get() / 1024.0 / 1024.0,
            failedRestores.get(), deletedVersions.get());
    }
} 