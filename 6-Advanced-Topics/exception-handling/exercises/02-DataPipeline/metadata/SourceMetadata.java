/**
 * Metadata for data sources
 */
public class SourceMetadata {
    private final String type;
    private final String location;
    private final String pattern;
    private final int remainingFiles;
    private final String currentFile;
    private final Map<String, Object> properties;
    private final LocalDateTime startTime;
    
    public SourceMetadata(String type, String location, String pattern,
                         int remainingFiles, String currentFile) {
        this.type = type;
        this.location = location;
        this.pattern = pattern;
        this.remainingFiles = remainingFiles;
        this.currentFile = currentFile;
        this.properties = new HashMap<>();
        this.startTime = LocalDateTime.now();
    }
    
    public String getType() {
        return type;
    }
    
    public String getLocation() {
        return location;
    }
    
    public String getPattern() {
        return pattern;
    }
    
    public int getRemainingFiles() {
        return remainingFiles;
    }
    
    public String getCurrentFile() {
        return currentFile;
    }
    
    public Map<String, Object> getProperties() {
        return Collections.unmodifiableMap(properties);
    }
    
    public void addProperty(String key, Object value) {
        properties.put(key, value);
    }
    
    public LocalDateTime getStartTime() {
        return startTime;
    }
    
    public Duration getUptime() {
        return Duration.between(startTime, LocalDateTime.now());
    }
    
    @Override
    public String toString() {
        return String.format(
            "SourceMetadata{type=%s, location=%s, pattern=%s, " +
            "remainingFiles=%d, currentFile=%s, uptime=%s}",
            type, location, pattern, remainingFiles, currentFile,
            formatDuration(getUptime()));
    }
    
    private String formatDuration(Duration duration) {
        return String.format("%02d:%02d:%02d",
            duration.toHours(),
            duration.toMinutesPart(),
            duration.toSecondsPart());
    }
} 