/**
 * Statistics for a collection of numbers
 */
public class NumberStats {
    private final long count;
    private final double sum;
    private final double average;
    private final double min;
    private final double max;
    private final Integer mode;
    private final double median;
    
    public NumberStats(long count, double sum, double average, 
                      double min, double max, Integer mode, double median) {
        this.count = count;
        this.sum = sum;
        this.average = average;
        this.min = min;
        this.max = max;
        this.mode = mode;
        this.median = median;
    }
    
    // Getters
    public long getCount() { return count; }
    public double getSum() { return sum; }
    public double getAverage() { return average; }
    public double getMin() { return min; }
    public double getMax() { return max; }
    public Integer getMode() { return mode; }
    public double getMedian() { return median; }
    
    @Override
    public String toString() {
        return String.format(
            "NumberStats{count=%d, sum=%.2f, avg=%.2f, min=%.2f, " +
            "max=%.2f, mode=%s, median=%.2f}",
            count, sum, average, min, max, mode, median);
    }
} 