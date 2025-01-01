/**
 * Generic event bus implementation
 */
public class EventBus {
    private final ConcurrentHashMap<Class<?>, Set<EventSubscriber>> subscribers;
    private final ExecutorService asyncExecutor;
    private final Queue<Event> deadLetterQueue;
    private final EventStats stats;
    
    public EventBus() {
        this.subscribers = new ConcurrentHashMap<>();
        this.asyncExecutor = Executors.newCachedThreadPool();
        this.deadLetterQueue = new ConcurrentLinkedQueue<>();
        this.stats = new EventStats();
    }
    
    public <T extends Event> void subscribe(Class<T> eventType, EventSubscriber<T> subscriber) {
        subscribers.computeIfAbsent(eventType, k -> 
            ConcurrentHashMap.newKeySet()).add(subscriber);
    }
    
    public <T extends Event> void unsubscribe(Class<T> eventType, EventSubscriber<T> subscriber) {
        Set<EventSubscriber> eventSubscribers = subscribers.get(eventType);
        if (eventSubscribers != null) {
            eventSubscribers.remove(subscriber);
        }
    }
    
    public void publish(Event event) {
        publish(event, false);
    }
    
    @SuppressWarnings("unchecked")
    public void publish(Event event, boolean async) {
        Set<EventSubscriber> eventSubscribers = subscribers.get(event.getClass());
        
        if (eventSubscribers == null || eventSubscribers.isEmpty()) {
            deadLetterQueue.offer(event);
            stats.recordDeadLetter();
            return;
        }
        
        stats.recordPublish();
        
        for (EventSubscriber subscriber : eventSubscribers) {
            if (async) {
                asyncExecutor.submit(() -> deliverEvent(subscriber, event));
            } else {
                deliverEvent(subscriber, event);
            }
        }
    }
    
    @SuppressWarnings("unchecked")
    private void deliverEvent(EventSubscriber subscriber, Event event) {
        try {
            subscriber.onEvent(event);
            stats.recordDelivery();
        } catch (Exception e) {
            stats.recordError();
            deadLetterQueue.offer(event);
        }
    }
    
    public Queue<Event> getDeadLetterQueue() {
        return new LinkedList<>(deadLetterQueue);
    }
    
    public EventStats getStats() {
        return stats;
    }
    
    public void shutdown() {
        asyncExecutor.shutdown();
    }
} 