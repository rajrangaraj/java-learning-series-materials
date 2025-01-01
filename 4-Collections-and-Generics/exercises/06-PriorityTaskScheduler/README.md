# Priority Task Scheduler Exercise

Build a generic task scheduling system with priority management.

## Requirements

1. Create `TaskScheduler<T>` that handles:
   - Task priorities
   - Execution timing
   - Dependencies
   - Resource constraints

2. Core Features:
   - schedule/cancel tasks
   - update priorities
   - manage dependencies
   - monitor execution

3. Advanced Features:
   - Task grouping
   - Resource pooling
   - Deadline management
   - Failure handling

## Implementation Tips
- Use `PriorityQueue` for task ordering
- Implement task dependency graph
- Consider thread pool execution
- Add monitoring capabilities 