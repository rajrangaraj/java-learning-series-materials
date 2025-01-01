/**
 * Examples demonstrating various Set implementations and operations
 */
public class SetExamples {
    
    /**
     * HashSet vs TreeSet performance comparison
     */
    public static void compareSetPerformance() {
        Set<Integer> hashSet = new HashSet<>();
        Set<Integer> treeSet = new TreeSet<>();
        int elementCount = 100000;
        
        // Add elements performance
        long startTime = System.nanoTime();
        for (int i = 0; i < elementCount; i++) {
            hashSet.add(i);
        }
        long hashSetAddTime = System.nanoTime() - startTime;
        
        startTime = System.nanoTime();
        for (int i = 0; i < elementCount; i++) {
            treeSet.add(i);
        }
        long treeSetAddTime = System.nanoTime() - startTime;
        
        System.out.println("Add Performance:");
        System.out.printf("HashSet: %d ns%n", hashSetAddTime);
        System.out.printf("TreeSet: %d ns%n", treeSetAddTime);
        
        // Contains performance
        startTime = System.nanoTime();
        for (int i = 0; i < 1000; i++) {
            hashSet.contains(ThreadLocalRandom.current().nextInt(elementCount));
        }
        long hashSetSearchTime = System.nanoTime() - startTime;
        
        startTime = System.nanoTime();
        for (int i = 0; i < 1000; i++) {
            treeSet.contains(ThreadLocalRandom.current().nextInt(elementCount));
        }
        long treeSetSearchTime = System.nanoTime() - startTime;
        
        System.out.println("\nSearch Performance:");
        System.out.printf("HashSet: %d ns%n", hashSetSearchTime);
        System.out.printf("TreeSet: %d ns%n", treeSetSearchTime);
    }
    
    /**
     * Demonstrates common Set operations
     */
    public static void demonstrateSetOperations() {
        Set<String> set1 = new HashSet<>(Arrays.asList("A", "B", "C"));
        Set<String> set2 = new HashSet<>(Arrays.asList("B", "C", "D"));
        
        // Union
        Set<String> union = new HashSet<>(set1);
        union.addAll(set2);
        System.out.println("Union: " + union);
        
        // Intersection
        Set<String> intersection = new HashSet<>(set1);
        intersection.retainAll(set2);
        System.out.println("Intersection: " + intersection);
        
        // Difference
        Set<String> difference = new HashSet<>(set1);
        difference.removeAll(set2);
        System.out.println("Difference: " + difference);
        
        // Symmetric Difference
        Set<String> symDifference = new HashSet<>(set1);
        symDifference.addAll(set2);
        Set<String> tmp = new HashSet<>(set1);
        tmp.retainAll(set2);
        symDifference.removeAll(tmp);
        System.out.println("Symmetric Difference: " + symDifference);
    }
    
    /**
     * Demonstrates NavigableSet operations with TreeSet
     */
    public static void demonstrateNavigableSet() {
        NavigableSet<Integer> navSet = new TreeSet<>();
        for (int i = 0; i <= 100; i += 10) {
            navSet.add(i);
        }
        
        System.out.println("Original set: " + navSet);
        System.out.println("Lower than 55: " + navSet.lower(55));
        System.out.println("Floor of 55: " + navSet.floor(55));
        System.out.println("Higher than 55: " + navSet.higher(55));
        System.out.println("Ceiling of 55: " + navSet.ceiling(55));
        
        System.out.println("First element: " + navSet.first());
        System.out.println("Last element: " + navSet.last());
        System.out.println("Subset [20,60): " + navSet.subSet(20, 60));
        System.out.println("Descending Set: " + navSet.descendingSet());
    }
    
    public static void main(String[] args) {
        System.out.println("Set Performance Comparison:");
        System.out.println("=========================");
        compareSetPerformance();
        
        System.out.println("\nSet Operations Demo:");
        System.out.println("==================");
        demonstrateSetOperations();
        
        System.out.println("\nNavigableSet Demo:");
        System.out.println("=================");
        demonstrateNavigableSet();
    }
} 