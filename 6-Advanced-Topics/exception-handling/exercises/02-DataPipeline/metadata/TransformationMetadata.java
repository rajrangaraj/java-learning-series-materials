/**
 * Metadata for data transformers
 */
public class TransformationMetadata {
    private final String type;
    private final List<String> availableRules;
    private final Map<String, Object> configuration;
    private final Map<String, TransformationStats> ruleStats;
    private final LocalDateTime startTime;
    
    public TransformationMetadata(String type, List<String> availableRules,
                                Map<String, Object> configuration) {
        this.type = type;
        this.availableRules = new ArrayList<>(availableRules);
        this.configuration = new HashMap<>(configuration);
        this.ruleStats = new ConcurrentHashMap<>();
        this.startTime = LocalDateTime.now();
        
        // Initialize stats for each rule
        availableRules.forEach(rule -> 
            ruleStats.put(rule, new TransformationStats()));
    }
    
    public String getType() {
        return type;
    }
    
    public List<String> getAvailableRules() {
        return Collections.unmodifiableList(availableRules);
    }
    
    public Map<String, Object> getConfiguration() {
        return Collections.unmodifiableMap(configuration);
    }
    
    public void recordTransformation(String rule, boolean success, long duration) {
        TransformationStats stats = ruleStats.get(rule);
        if (stats != null) {
            stats.recordTransformation(success, duration);
        }
    }
    
    public TransformationStats getStatsForRule(String rule) {
        return ruleStats.get(rule);
    }
    
    public Map<String, TransformationStats> getAllStats() {
        return Collections.unmodifiableMap(ruleStats);
    }
    
    public Duration getUptime() {
        return Duration.between(startTime, LocalDateTime.now());
    }
    
    @Override
    public String toString() {
        return String.format(
            "TransformationMetadata{type=%s, rules=%d, uptime=%s}",
            type, availableRules.size(),
            formatDuration(getUptime()));
    }
    
    private String formatDuration(Duration duration) {
        return String.format("%02d:%02d:%02d",
            duration.toHours(),
            duration.toMinutesPart(),
            duration.toSecondsPart());
    }
    
    public static class TransformationStats {
        private final AtomicLong successCount;
        private final AtomicLong failureCount;
        private final AtomicLong totalDuration;
        
        public TransformationStats() {
            this.successCount = new AtomicLong();
            this.failureCount = new AtomicLong();
            this.totalDuration = new AtomicLong();
        }
        
        public void recordTransformation(boolean success, long duration) {
            if (success) {
                successCount.incrementAndGet();
            } else {
                failureCount.incrementAndGet();
            }
            totalDuration.addAndGet(duration);
        }
        
        public long getSuccessCount() {
            return successCount.get();
        }
        
        public long getFailureCount() {
            return failureCount.get();
        }
        
        public long getTotalCount() {
            return successCount.get() + failureCount.get();
        }
        
        public double getSuccessRate() {
            long total = getTotalCount();
            return total > 0 ? 
                (double) successCount.get() / total : 0.0;
        }
        
        public double getAverageDuration() {
            long total = getTotalCount();
            return total > 0 ? 
                (double) totalDuration.get() / total : 0.0;
        }
    }
} 