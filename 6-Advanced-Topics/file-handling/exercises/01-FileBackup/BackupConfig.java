/**
 * Configuration for backup operations
 */
public class BackupConfig {
    private final Set<String> includedExtensions;
    private final long maxFileSize;
    private final int maxVersions;
    private final boolean incrementalBackup;
    
    private BackupConfig(Builder builder) {
        this.includedExtensions = new HashSet<>(builder.includedExtensions);
        this.maxFileSize = builder.maxFileSize;
        this.maxVersions = builder.maxVersions;
        this.incrementalBackup = builder.incrementalBackup;
    }
    
    public Set<String> getIncludedExtensions() {
        return Collections.unmodifiableSet(includedExtensions);
    }
    
    public long getMaxFileSize() {
        return maxFileSize;
    }
    
    public int getMaxVersions() {
        return maxVersions;
    }
    
    public boolean isIncrementalBackup() {
        return incrementalBackup;
    }
    
    public static class Builder {
        private Set<String> includedExtensions = new HashSet<>();
        private long maxFileSize = Long.MAX_VALUE;
        private int maxVersions = 0;
        private boolean incrementalBackup = false;
        
        public Builder includeExtensions(String... extensions) {
            this.includedExtensions.addAll(Arrays.asList(extensions));
            return this;
        }
        
        public Builder maxFileSize(long maxFileSize) {
            this.maxFileSize = maxFileSize;
            return this;
        }
        
        public Builder maxVersions(int maxVersions) {
            this.maxVersions = maxVersions;
            return this;
        }
        
        public Builder incrementalBackup(boolean incremental) {
            this.incrementalBackup = incremental;
            return this;
        }
        
        public BackupConfig build() {
            return new BackupConfig(this);
        }
    }
} 