# Generic Object Pool Exercise

Design a reusable object pool with generic type support.

## Requirements

1. Implement `ObjectPool<T>` with:
   - Pool size management
   - Object lifecycle
   - Validation rules
   - Statistics tracking

2. Core Operations:
   - acquire/release
   - validate/reset
   - expand/shrink
   - cleanup

3. Advanced Features:
   - Timeout handling
   - Health checking
   - Pool partitioning
   - Auto-scaling

## Implementation Tips
- Use concurrent collections
- Implement factory pattern
- Add resource monitoring
- Consider cleanup strategies 