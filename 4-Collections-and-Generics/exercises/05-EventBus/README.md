# Generic Event Bus Exercise

Create a type-safe event publishing and subscription system using generics.

## Requirements

1. Implement `EventBus` that supports:
   - Type-safe event publishing
   - Multiple subscribers
   - Event hierarchies
   - Async delivery option

2. Core Features:
   - subscribe/unsubscribe
   - publish events
   - subscriber filtering
   - event prioritization

3. Advanced Features:
   - Dead letter queue
   - Event replay
   - Subscription patterns
   - Performance monitoring

## Implementation Tips
- Use generic type bounds for events
- Consider weak references for subscribers
- Implement thread-safe collections
- Use executor service for async delivery 