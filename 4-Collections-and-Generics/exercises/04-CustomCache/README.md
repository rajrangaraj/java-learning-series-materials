# Custom Cache Implementation Exercise

Implement a generic cache system with eviction policies and statistics tracking.

## Requirements

1. Create a `CustomCache<K,V>` that supports:
   - Multiple eviction policies (LRU, LFU, FIFO)
   - Configurable size limits
   - Entry expiration
   - Statistics tracking

2. Core Operations:
   - put/get/remove
   - clear/reset
   - getStats
   - evict

3. Advanced Features:
   - Bulk operations
   - Entry listeners
   - Background cleanup
   - Cache persistence

## Implementation Tips
- Use `ConcurrentHashMap` for thread safety
- Implement eviction policy interface
- Use `ScheduledExecutorService` for cleanup
- Consider weak references for memory management 