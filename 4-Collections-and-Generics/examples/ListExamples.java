/**
 * Examples demonstrating various List implementations and operations
 */
public class ListExamples {
    
    /**
     * ArrayList vs LinkedList performance comparison
     */
    public static void compareListPerformance() {
        List<Integer> arrayList = new ArrayList<>();
        List<Integer> linkedList = new LinkedList<>();
        int elementCount = 100000;
        
        // Add elements performance
        long startTime = System.nanoTime();
        for (int i = 0; i < elementCount; i++) {
            arrayList.add(i);
        }
        long arrayListAddTime = System.nanoTime() - startTime;
        
        startTime = System.nanoTime();
        for (int i = 0; i < elementCount; i++) {
            linkedList.add(i);
        }
        long linkedListAddTime = System.nanoTime() - startTime;
        
        System.out.println("Add Performance:");
        System.out.printf("ArrayList: %d ns%n", arrayListAddTime);
        System.out.printf("LinkedList: %d ns%n", linkedListAddTime);
        
        // Random access performance
        startTime = System.nanoTime();
        for (int i = 0; i < 1000; i++) {
            arrayList.get(ThreadLocalRandom.current().nextInt(elementCount));
        }
        long arrayListAccessTime = System.nanoTime() - startTime;
        
        startTime = System.nanoTime();
        for (int i = 0; i < 1000; i++) {
            linkedList.get(ThreadLocalRandom.current().nextInt(elementCount));
        }
        long linkedListAccessTime = System.nanoTime() - startTime;
        
        System.out.println("\nRandom Access Performance:");
        System.out.printf("ArrayList: %d ns%n", arrayListAccessTime);
        System.out.printf("LinkedList: %d ns%n", linkedListAccessTime);
        
        // Insert at beginning performance
        startTime = System.nanoTime();
        for (int i = 0; i < 1000; i++) {
            arrayList.add(0, i);
        }
        long arrayListInsertTime = System.nanoTime() - startTime;
        
        startTime = System.nanoTime();
        for (int i = 0; i < 1000; i++) {
            linkedList.add(0, i);
        }
        long linkedListInsertTime = System.nanoTime() - startTime;
        
        System.out.println("\nInsert at Beginning Performance:");
        System.out.printf("ArrayList: %d ns%n", arrayListInsertTime);
        System.out.printf("LinkedList: %d ns%n", linkedListInsertTime);
    }
    
    /**
     * Demonstrates common List operations
     */
    public static void demonstrateListOperations() {
        List<String> list = new ArrayList<>();
        
        // Adding elements
        list.add("Apple");
        list.add("Banana");
        list.add("Cherry");
        System.out.println("Initial list: " + list);
        
        // Insert at position
        list.add(1, "Blueberry");
        System.out.println("After insertion: " + list);
        
        // Remove by object
        list.remove("Banana");
        System.out.println("After removal: " + list);
        
        // Remove by index
        list.remove(0);
        System.out.println("After index removal: " + list);
        
        // Contains check
        System.out.println("Contains 'Cherry': " + list.contains("Cherry"));
        
        // Index of
        System.out.println("Index of 'Cherry': " + list.indexOf("Cherry"));
        
        // Sublist
        list.addAll(Arrays.asList("Dragonfruit", "Elderberry", "Fig"));
        List<String> subList = list.subList(1, 3);
        System.out.println("Sublist: " + subList);
        
        // Sort
        Collections.sort(list);
        System.out.println("Sorted list: " + list);
        
        // Custom sort
        Collections.sort(list, (a, b) -> b.compareTo(a));
        System.out.println("Reverse sorted: " + list);
    }
    
    /**
     * Demonstrates thread-safe List operations
     */
    public static void demonstrateThreadSafeList() {
        List<Integer> synchronizedList = Collections.synchronizedList(new ArrayList<>());
        List<Integer> concurrentList = new CopyOnWriteArrayList<>();
        
        // Demonstrate concurrent modifications
        ExecutorService executor = Executors.newFixedThreadPool(3);
        
        // Add elements concurrently
        for (int i = 0; i < 100; i++) {
            final int num = i;
            executor.submit(() -> {
                synchronizedList.add(num);
                concurrentList.add(num);
            });
        }
        
        // Shutdown and wait for completion
        executor.shutdown();
        try {
            executor.awaitTermination(5, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        System.out.println("Synchronized List size: " + synchronizedList.size());
        System.out.println("Concurrent List size: " + concurrentList.size());
    }
    
    /**
     * Demonstrates List views and wrappers
     */
    public static void demonstrateListViews() {
        // Immutable list
        List<String> immutableList = List.of("One", "Two", "Three");
        System.out.println("Immutable list: " + immutableList);
        
        // Unmodifiable view
        List<String> baseList = new ArrayList<>(Arrays.asList("A", "B", "C"));
        List<String> unmodifiableList = Collections.unmodifiableList(baseList);
        System.out.println("Unmodifiable view: " + unmodifiableList);
        
        // Single element list
        List<Integer> singletonList = Collections.singletonList(42);
        System.out.println("Singleton list: " + singletonList);
        
        // Empty list
        List<Double> emptyList = Collections.emptyList();
        System.out.println("Empty list: " + emptyList);
    }
    
    public static void main(String[] args) {
        System.out.println("List Performance Comparison:");
        System.out.println("==========================");
        compareListPerformance();
        
        System.out.println("\nList Operations Demo:");
        System.out.println("===================");
        demonstrateListOperations();
        
        System.out.println("\nThread-safe List Demo:");
        System.out.println("====================");
        demonstrateThreadSafeList();
        
        System.out.println("\nList Views Demo:");
        System.out.println("===============");
        demonstrateListViews();
    }
} 