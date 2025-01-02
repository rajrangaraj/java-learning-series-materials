/**
 * Configuration for file processing
 */
public class ProcessingConfig {
    private final long maxFileSize;
    private final long maxFileAge;
    private final int minimumFiles;
    private final boolean upperCase;
    private final int trimLength;
    private final Set<String> requiredColumns;
    private final Set<String> requiredJsonFields;
    private final boolean requireJsonObject;
    private final boolean requireJsonArray;
    
    private ProcessingConfig(Builder builder) {
        this.maxFileSize = builder.maxFileSize;
        this.maxFileAge = builder.maxFileAge;
        this.minimumFiles = builder.minimumFiles;
        this.upperCase = builder.upperCase;
        this.trimLength = builder.trimLength;
        this.requiredColumns = new HashSet<>(builder.requiredColumns);
        this.requiredJsonFields = new HashSet<>(builder.requiredJsonFields);
        this.requireJsonObject = builder.requireJsonObject;
        this.requireJsonArray = builder.requireJsonArray;
    }
    
    // Getters
    public long getMaxFileSize() {
        return maxFileSize;
    }
    
    public long getMaxFileAge() {
        return maxFileAge;
    }
    
    public int getMinimumFiles() {
        return minimumFiles;
    }
    
    public boolean isUpperCase() {
        return upperCase;
    }
    
    public int getTrimLength() {
        return trimLength;
    }
    
    public Set<String> getRequiredColumns() {
        return Collections.unmodifiableSet(requiredColumns);
    }
    
    public Set<String> getRequiredJsonFields() {
        return Collections.unmodifiableSet(requiredJsonFields);
    }
    
    public boolean isRequireJsonObject() {
        return requireJsonObject;
    }
    
    public boolean isRequireJsonArray() {
        return requireJsonArray;
    }
    
    public static class Builder {
        private long maxFileSize = Long.MAX_VALUE;
        private long maxFileAge = 365;
        private int minimumFiles = 1;
        private boolean upperCase = false;
        private int trimLength = 0;
        private Set<String> requiredColumns = new HashSet<>();
        private Set<String> requiredJsonFields = new HashSet<>();
        private boolean requireJsonObject = false;
        private boolean requireJsonArray = false;
        
        public Builder maxFileSize(long maxFileSize) {
            this.maxFileSize = maxFileSize;
            return this;
        }
        
        public Builder maxFileAge(long maxFileAge) {
            this.maxFileAge = maxFileAge;
            return this;
        }
        
        public Builder minimumFiles(int minimumFiles) {
            this.minimumFiles = minimumFiles;
            return this;
        }
        
        public Builder upperCase(boolean upperCase) {
            this.upperCase = upperCase;
            return this;
        }
        
        public Builder trimLength(int trimLength) {
            this.trimLength = trimLength;
            return this;
        }
        
        public Builder requiredColumns(String... columns) {
            this.requiredColumns.addAll(Arrays.asList(columns));
            return this;
        }
        
        public Builder requiredJsonFields(String... fields) {
            this.requiredJsonFields.addAll(Arrays.asList(fields));
            return this;
        }
        
        public Builder requireJsonObject(boolean require) {
            this.requireJsonObject = require;
            return this;
        }
        
        public Builder requireJsonArray(boolean require) {
            this.requireJsonArray = require;
            return this;
        }
        
        public ProcessingConfig build() {
            return new ProcessingConfig(this);
        }
    }
} 