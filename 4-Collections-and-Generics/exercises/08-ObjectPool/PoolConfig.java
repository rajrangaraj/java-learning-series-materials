/**
 * Configuration for object pool
 */
public class PoolConfig {
    private final int minSize;
    private final int maxSize;
    private final long maxWaitMillis;
    private final long maxIdleTimeMillis;
    private final long maintenanceIntervalMillis;
    
    private PoolConfig(Builder builder) {
        this.minSize = builder.minSize;
        this.maxSize = builder.maxSize;
        this.maxWaitMillis = builder.maxWaitMillis;
        this.maxIdleTimeMillis = builder.maxIdleTimeMillis;
        this.maintenanceIntervalMillis = builder.maintenanceIntervalMillis;
    }
    
    // Getters
    public int getMinSize() { return minSize; }
    public int getMaxSize() { return maxSize; }
    public long getMaxWaitMillis() { return maxWaitMillis; }
    public long getMaxIdleTimeMillis() { return maxIdleTimeMillis; }
    public long getMaintenanceIntervalMillis() { return maintenanceIntervalMillis; }
    
    public static class Builder {
        private int minSize = 5;
        private int maxSize = 20;
        private long maxWaitMillis = 5000;
        private long maxIdleTimeMillis = 300000;
        private long maintenanceIntervalMillis = 60000;
        
        public Builder minSize(int minSize) {
            this.minSize = minSize;
            return this;
        }
        
        public Builder maxSize(int maxSize) {
            this.maxSize = maxSize;
            return this;
        }
        
        public Builder maxWaitMillis(long maxWaitMillis) {
            this.maxWaitMillis = maxWaitMillis;
            return this;
        }
        
        public Builder maxIdleTimeMillis(long maxIdleTimeMillis) {
            this.maxIdleTimeMillis = maxIdleTimeMillis;
            return this;
        }
        
        public Builder maintenanceIntervalMillis(long maintenanceIntervalMillis) {
            this.maintenanceIntervalMillis = maintenanceIntervalMillis;
            return this;
        }
        
        public PoolConfig build() {
            return new PoolConfig(this);
        }
    }
} 