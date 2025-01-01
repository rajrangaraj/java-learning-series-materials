# Unique Word Counter Exercise

Create a program that processes text files and counts unique words using appropriate collections.

## Requirements

1. Implement word counting that:
   - Ignores case
   - Handles punctuation
   - Supports stop words
   - Tracks word frequency

2. Support operations:
   - Load text from file
   - Get word frequency
   - Find most common words
   - Export statistics

3. Performance considerations:
   - Efficient memory usage
   - Quick lookup times
   - Sorted results option

## Implementation Tips

- Use `HashSet` for unique words
- Consider `TreeMap` for sorted frequency
- Implement proper text cleaning
- Use streams for processing 