/**
 * Statistics for word occurrences in files
 */
public class WordStats {
    private final int frequency;
    private final Map<Integer, Long> lengthDistribution;
    
    public WordStats(int frequency, Map<Integer, Long> lengthDistribution) {
        this.frequency = frequency;
        this.lengthDistribution = new HashMap<>(lengthDistribution);
    }
    
    public int getFrequency() {
        return frequency;
    }
    
    public Map<Integer, Long> getLengthDistribution() {
        return Collections.unmodifiableMap(lengthDistribution);
    }
    
    public double getAverageLength() {
        return lengthDistribution.entrySet().stream()
            .mapToDouble(e -> e.getKey() * e.getValue())
            .sum() / frequency;
    }
    
    @Override
    public String toString() {
        return String.format(
            "WordStats{frequency=%d, avgLength=%.2f, distribution=%s}",
            frequency, getAverageLength(), lengthDistribution);
    }
} 