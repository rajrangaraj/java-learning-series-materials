# Indexed Collection Exercise

Create a generic collection that supports multiple indexing strategies.

## Requirements

1. Design `IndexedCollection<T>` supporting:
   - Multiple index types
   - Composite keys
   - Index maintenance
   - Query optimization

2. Core Features:
   - add/remove elements
   - create/drop indexes
   - query by index
   - bulk operations

3. Advanced Features:
   - Index statistics
   - Query planning
   - Index rebuilding
   - Consistency checking

## Implementation Tips
- Use `Map` for index storage
- Implement observer pattern
- Consider memory usage
- Add query optimization 