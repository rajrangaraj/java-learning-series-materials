/**
 * Exception thrown when retry attempts are exhausted
 */
public class RetryExhaustedException extends DataProcessingException {
    private final int attempts;
    private final Duration totalDuration;
    private final List<Throwable> failures;
    
    public RetryExhaustedException(String message, int attempts,
            Duration totalDuration, List<Throwable> failures) {
        super(message);
        this.attempts = attempts;
        this.totalDuration = totalDuration;
        this.failures = new ArrayList<>(failures);
    }
    
    public RetryExhaustedException(String message, Throwable cause) {
        super(message, cause);
        this.attempts = 1;
        this.totalDuration = Duration.ZERO;
        this.failures = Collections.singletonList(cause);
    }
    
    public int getAttempts() {
        return attempts;
    }
    
    public Duration getTotalDuration() {
        return totalDuration;
    }
    
    public List<Throwable> getFailures() {
        return Collections.unmodifiableList(failures);
    }
    
    @Override
    public String getDetails() {
        return String.format("Attempts: %d, Duration: %s, Failures: %s",
            attempts, totalDuration, summarizeFailures());
    }
    
    private String summarizeFailures() {
        return failures.stream()
            .map(t -> t.getClass().getSimpleName() + ": " + t.getMessage())
            .collect(Collectors.joining(", "));
    }
} 