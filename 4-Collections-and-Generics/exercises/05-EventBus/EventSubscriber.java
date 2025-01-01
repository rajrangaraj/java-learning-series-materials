/**
 * Interface for event subscribers
 */
@FunctionalInterface
public interface EventSubscriber<T extends Event> {
    void onEvent(T event);
} 