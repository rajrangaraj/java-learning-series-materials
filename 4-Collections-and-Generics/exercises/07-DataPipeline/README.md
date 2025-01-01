# Generic Data Pipeline Exercise

Implement a flexible data processing pipeline using generics.

## Requirements

1. Create `Pipeline<I,O>` that supports:
   - Data transformation stages
   - Filtering operations
   - Aggregation steps
   - Error handling

2. Core Operations:
   - addStage/removeStage
   - process data
   - handle errors
   - collect results

3. Advanced Features:
   - Parallel processing
   - Backpressure handling
   - Monitoring/metrics
   - Pipeline branching

## Implementation Tips
- Use generic type bounds for transformations
- Implement builder pattern
- Consider reactive streams
- Add performance monitoring 