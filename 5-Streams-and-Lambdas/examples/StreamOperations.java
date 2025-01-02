/**
 * Examples demonstrating various Stream operations and their usage
 */
public class StreamOperations {
    
    public static void main(String[] args) {
        // Basic stream operations
        demonstrateBasicOperations();
        
        // Intermediate operations
        demonstrateIntermediateOperations();
        
        // Terminal operations
        demonstrateTerminalOperations();
        
        // Collectors examples
        demonstrateCollectors();
        
        // Parallel stream examples
        demonstrateParallelStreams();
    }
    
    private static void demonstrateBasicOperations() {
        System.out.println("Basic Stream Operations:");
        System.out.println("======================");
        
        // Creating streams
        Stream<String> streamFromValues = Stream.of("a", "b", "c");
        Stream<Integer> streamFromArray = Arrays.stream(new Integer[]{1, 2, 3});
        Stream<String> streamFromCollection = Arrays.asList("x", "y", "z").stream();
        
        // Infinite streams
        Stream<Integer> infiniteStream = Stream.iterate(0, n -> n + 2);
        Stream<Double> randomStream = Stream.generate(Math::random);
        
        // Basic operations
        streamFromValues.forEach(System.out::println);
        System.out.println("Sum: " + streamFromArray.mapToInt(Integer::intValue).sum());
        System.out.println("Joined: " + streamFromCollection.collect(Collectors.joining(", ")));
        System.out.println("First 5 even numbers: " + 
            infiniteStream.limit(5).collect(Collectors.toList()));
        System.out.println("5 random numbers: " + 
            randomStream.limit(5).map(d -> String.format("%.2f", d))
                       .collect(Collectors.joining(", ")));
    }
    
    private static void demonstrateIntermediateOperations() {
        System.out.println("\nIntermediate Operations:");
        System.out.println("======================");
        
        List<String> words = Arrays.asList("apple", "banana", "cherry", "date", "elderberry");
        
        // Filter
        System.out.println("Words longer than 5 letters: " +
            words.stream()
                .filter(w -> w.length() > 5)
                .collect(Collectors.toList()));
        
        // Map
        System.out.println("Uppercase words: " +
            words.stream()
                .map(String::toUpperCase)
                .collect(Collectors.toList()));
        
        // FlatMap
        System.out.println("Individual characters: " +
            words.stream()
                .flatMap(w -> w.chars().mapToObj(c -> String.valueOf((char)c)))
                .distinct()
                .collect(Collectors.joining(", ")));
        
        // Sorted
        System.out.println("Sorted by length: " +
            words.stream()
                .sorted(Comparator.comparing(String::length))
                .collect(Collectors.toList()));
        
        // Peek
        System.out.println("Processing with peek: ");
        words.stream()
            .peek(w -> System.out.print("Processing: " + w + " -> "))
            .map(String::length)
            .forEach(len -> System.out.println("Length: " + len));
    }
    
    private static void demonstrateTerminalOperations() {
        System.out.println("\nTerminal Operations:");
        System.out.println("===================");
        
        List<Integer> numbers = Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);
        
        // Reduction operations
        System.out.println("Sum: " + 
            numbers.stream().reduce(0, Integer::sum));
        
        System.out.println("Product: " + 
            numbers.stream().reduce(1, (a, b) -> a * b));
        
        // Finding operations
        Optional<Integer> first = numbers.stream()
            .filter(n -> n > 5)
            .findFirst();
        System.out.println("First number > 5: " + first.orElse(-1));
        
        // Matching operations
        boolean allEven = numbers.stream().allMatch(n -> n % 2 == 0);
        boolean anyEven = numbers.stream().anyMatch(n -> n % 2 == 0);
        boolean noneNegative = numbers.stream().noneMatch(n -> n < 0);
        
        System.out.println("All even? " + allEven);
        System.out.println("Any even? " + anyEven);
        System.out.println("None negative? " + noneNegative);
        
        // Count, min, max
        System.out.println("Count of even numbers: " + 
            numbers.stream().filter(n -> n % 2 == 0).count());
        System.out.println("Minimum: " + 
            numbers.stream().min(Integer::compareTo).orElse(-1));
        System.out.println("Maximum: " + 
            numbers.stream().max(Integer::compareTo).orElse(-1));
    }
    
    private static void demonstrateCollectors() {
        System.out.println("\nCollectors Examples:");
        System.out.println("===================");
        
        List<Person> people = Arrays.asList(
            new Person("Alice", 25),
            new Person("Bob", 30),
            new Person("Charlie", 25),
            new Person("David", 35)
        );
        
        // Collecting to different collections
        Set<String> names = people.stream()
            .map(Person::getName)
            .collect(Collectors.toSet());
        System.out.println("Names set: " + names);
        
        // Grouping
        Map<Integer, List<Person>> byAge = people.stream()
            .collect(Collectors.groupingBy(Person::getAge));
        System.out.println("Grouped by age: " + byAge);
        
        // Partitioning
        Map<Boolean, List<Person>> partitioned = people.stream()
            .collect(Collectors.partitioningBy(p -> p.getAge() > 30));
        System.out.println("Partitioned by age > 30: " + partitioned);
        
        // Statistics
        DoubleSummaryStatistics ageStats = people.stream()
            .collect(Collectors.summarizingDouble(Person::getAge));
        System.out.println("Age statistics: " + ageStats);
        
        // Joining
        String namesList = people.stream()
            .map(Person::getName)
            .collect(Collectors.joining(", ", "Names: [", "]"));
        System.out.println(namesList);
    }
    
    private static void demonstrateParallelStreams() {
        System.out.println("\nParallel Streams Examples:");
        System.out.println("========================");
        
        List<Integer> numbers = IntStream.rangeClosed(1, 1000000)
            .boxed()
            .collect(Collectors.toList());
        
        // Sequential vs Parallel performance
        long start = System.currentTimeMillis();
        double sequentialSum = numbers.stream()
            .mapToDouble(i -> Math.sqrt(i))
            .sum();
        long sequentialTime = System.currentTimeMillis() - start;
        
        start = System.currentTimeMillis();
        double parallelSum = numbers.parallelStream()
            .mapToDouble(i -> Math.sqrt(i))
            .sum();
        long parallelTime = System.currentTimeMillis() - start;
        
        System.out.println("Sequential time: " + sequentialTime + "ms");
        System.out.println("Parallel time: " + parallelTime + "ms");
        System.out.println("Results match: " + 
            (Math.abs(sequentialSum - parallelSum) < 0.001));
        
        // Parallel collection
        List<String> result = numbers.parallelStream()
            .limit(10)
            .map(n -> "Item " + n)
            .collect(Collectors.toList());
        System.out.println("Parallel processed items: " + result);
    }
    
    // Helper class for examples
    private static class Person {
        private final String name;
        private final int age;
        
        public Person(String name, int age) {
            this.name = name;
            this.age = age;
        }
        
        public String getName() { return name; }
        public int getAge() { return age; }
        
        @Override
        public String toString() {
            return name + "(" + age + ")";
        }
    }
} 