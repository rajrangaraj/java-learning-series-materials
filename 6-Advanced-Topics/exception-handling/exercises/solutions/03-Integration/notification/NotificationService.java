/**
 * Interface for notification operations
 */
public interface NotificationService {
    void sendOrderConfirmation(NotificationRequest request) 
        throws NotificationException;
        
    void sendShippingUpdate(String orderId, ShipmentStatus status) 
        throws NotificationException;
        
    void sendPaymentFailure(String orderId, String reason) 
        throws NotificationException;
        
    NotificationStatus checkStatus(String notificationId) 
        throws NotificationException;
}

/**
 * Interface for message queue operations
 */
public interface MessageQueue {
    void publish(String topic, Message message) 
        throws MessageQueueException;
        
    void subscribe(String topic, MessageHandler handler) 
        throws MessageQueueException;
        
    void acknowledge(String messageId) 
        throws MessageQueueException;
        
    void deadLetter(String messageId, String reason) 
        throws MessageQueueException;
}

/**
 * Message handler interface
 */
@FunctionalInterface
public interface MessageHandler {
    void handleMessage(Message message) throws MessageHandlingException;
} 