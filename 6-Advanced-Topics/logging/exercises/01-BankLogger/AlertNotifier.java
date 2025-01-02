/**
 * Handles sending alerts through various channels
 */
public class AlertNotifier {
    private static final Logger logger = LoggerFactory.getLogger(AlertNotifier.class);
    private final Map<AlertType, List<NotificationChannel>> channelMap;
    private final ExecutorService executor;
    
    public AlertNotifier() {
        this.channelMap = new EnumMap<>(AlertType.class);
        this.executor = Executors.newFixedThreadPool(4);
        initializeChannelMap();
    }
    
    private void initializeChannelMap() {
        // Configure notification channels for each alert type
        channelMap.put(AlertType.SECURITY, Arrays.asList(
            NotificationChannel.EMAIL,
            NotificationChannel.SMS,
            NotificationChannel.SLACK
        ));
        
        channelMap.put(AlertType.SYSTEM, Arrays.asList(
            NotificationChannel.EMAIL,
            NotificationChannel.SLACK
        ));
        
        channelMap.put(AlertType.SECURITY_NOTIFICATION, Arrays.asList(
            NotificationChannel.EMAIL,
            NotificationChannel.SLACK
        ));
        
        channelMap.put(AlertType.SYSTEM_NOTIFICATION, Arrays.asList(
            NotificationChannel.EMAIL
        ));
    }
    
    public void sendAlert(Alert alert) {
        List<NotificationChannel> channels = channelMap.getOrDefault(
            alert.getType(), Collections.emptyList());
        
        for (NotificationChannel channel : channels) {
            executor.submit(() -> {
                try {
                    sendAlertViaChannel(alert, channel);
                } catch (Exception e) {
                    logger.error("Failed to send alert via {}", channel, e);
                }
            });
        }
    }
    
    private void sendAlertViaChannel(Alert alert, NotificationChannel channel) {
        // Simulate sending alert through different channels
        logger.info("Sending {} alert via {}: {}", 
            alert.getType(), channel, alert.getMessage());
        
        // Add artificial delay to simulate network call
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
    
    private enum NotificationChannel {
        EMAIL,
        SMS,
        SLACK,
        WEBHOOK
    }
} 