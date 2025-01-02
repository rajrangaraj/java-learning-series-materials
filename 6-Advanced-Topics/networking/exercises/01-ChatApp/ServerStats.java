/**
 * Statistics tracking for chat server
 */
public class ServerStats {
    private final AtomicInteger totalConnections;
    private final AtomicInteger currentConnections;
    private final AtomicInteger totalMessages;
    private final AtomicInteger totalAuthentications;
    private final AtomicInteger totalErrors;
    private final AtomicLong startTime;
    
    public ServerStats() {
        this.totalConnections = new AtomicInteger();
        this.currentConnections = new AtomicInteger();
        this.totalMessages = new AtomicInteger();
        this.totalAuthentications = new AtomicInteger();
        this.totalErrors = new AtomicInteger();
        this.startTime = new AtomicLong(System.currentTimeMillis());
    }
    
    public void recordConnection() {
        totalConnections.incrementAndGet();
        currentConnections.incrementAndGet();
    }
    
    public void recordDisconnection() {
        currentConnections.decrementAndGet();
    }
    
    public void recordMessage() {
        totalMessages.incrementAndGet();
    }
    
    public void recordAuthentication() {
        totalAuthentications.incrementAndGet();
    }
    
    public void recordError() {
        totalErrors.incrementAndGet();
    }
    
    public int getTotalConnections() {
        return totalConnections.get();
    }
    
    public int getCurrentConnections() {
        return currentConnections.get();
    }
    
    public int getTotalMessages() {
        return totalMessages.get();
    }
    
    public int getTotalAuthentications() {
        return totalAuthentications.get();
    }
    
    public int getTotalErrors() {
        return totalErrors.get();
    }
    
    public long getUptime() {
        return System.currentTimeMillis() - startTime.get();
    }
    
    @Override
    public String toString() {
        return String.format(
            "ServerStats{uptime=%d seconds, totalConnections=%d, " +
            "currentConnections=%d, totalMessages=%d, " +
            "authentications=%d, errors=%d}",
            getUptime() / 1000,
            totalConnections.get(),
            currentConnections.get(),
            totalMessages.get(),
            totalAuthentications.get(),
            totalErrors.get());
    }
} 