# Stream Operations Cheatsheet

## Creating Streams 
java
// From values
Stream<String> stream = Stream.of("a", "b", "c");
// From array
String[] array = {"a", "b", "c"};
Stream<String> stream = Arrays.stream(array);
// From collection
List<String> list = Arrays.asList("a", "b", "c");
Stream<String> stream = list.stream();
// Infinite streams
Stream<Integer> infinite = Stream.iterate(0, n -> n + 1);
Stream<Double> random = Stream.generate(Math::random);
// Primitive streams
IntStream intStream = IntStream.range(1, 5); // 1,2,3,4
LongStream longStream = LongStream.rangeClosed(1, 5); // 1,2,3,4,5


## Intermediate Operations

java:5-Streams-and-Lambdas/stream_operations_cheatsheet.md
// Filtering
filter(Predicate<T>) // Filter elements by predicate
distinct() // Remove duplicates
limit(long n) // Limit to n elements
skip(long n) // Skip first n elements
// Mapping
map(Function<T,R>) // Transform elements
mapToInt/Long/Double() // Transform to primitive stream
flatMap(Function<T,Stream<R>>) // Transform and flatten
peek(Consumer<T>) // Process elements while streaming
// Sorting
sorted() // Natural order
sorted(Comparator<T>) // Custom order


## Terminal Operations

// Reduction
reduce(BinaryOperator<T>)    // Reduce to single value
reduce(T, BinaryOperator<T>) // Reduce with initial value
count()                      // Count elements
sum()                        // Sum (numeric streams)
min()/max()                  // Find min/max element

// Collection
collect(Collector<T,A,R>)    // Collect into container
toList()/toSet()            // Collect to List/Set
toArray()                    // Collect to array

// Search
findFirst()/findAny()       // Find an element
anyMatch(Predicate<T>)      // Check if any match
allMatch(Predicate<T>)      // Check if all match
noneMatch(Predicate<T>)     // Check if none match


## Common Collectors
```java
// Basic collectors
Collectors.toList()          // Collect to List
Collectors.toSet()           // Collect to Set
Collectors.toMap(k, v)       // Collect to Map

// String joining
Collectors.joining()         // Join elements
Collectors.joining(", ")     // Join with delimiter
Collectors.joining(", ", "[", "]") // Join with prefix/suffix

// Grouping
Collectors.groupingBy(Function<T,K>)     // Group by key
Collectors.partitioningBy(Predicate<T>)  // Partition by condition

// Numeric
Collectors.counting()        // Count elements
Collectors.summingInt/Long/Double()      // Sum values
Collectors.averagingInt/Long/Double()    // Average values
Collectors.summarizingInt/Long/Double()  // Statistics
```

## Parallel Streams
```java
// Creating parallel streams
collection.parallelStream()
stream.parallel()

// Performance considerations
// - Use with large datasets
// - Avoid stateful operations
// - Consider thread safety
// - Measure performance
```

## Best Practices
1. Use appropriate terminal operation
2. Consider order of operations
3. Avoid side effects in lambdas
4. Use method references when possible
5. Consider parallel streams carefully
6. Close streams with try-with-resources
7. Don't reuse streams

