/**
 * Examples demonstrating various Map implementations and operations
 */
public class MapExamples {
    
    /**
     * HashMap vs TreeMap performance comparison
     */
    public static void compareMapPerformance() {
        Map<Integer, String> hashMap = new HashMap<>();
        Map<Integer, String> treeMap = new TreeMap<>();
        int elementCount = 100000;
        
        // Add elements performance
        long startTime = System.nanoTime();
        for (int i = 0; i < elementCount; i++) {
            hashMap.put(i, "Value" + i);
        }
        long hashMapAddTime = System.nanoTime() - startTime;
        
        startTime = System.nanoTime();
        for (int i = 0; i < elementCount; i++) {
            treeMap.put(i, "Value" + i);
        }
        long treeMapAddTime = System.nanoTime() - startTime;
        
        System.out.println("Put Performance:");
        System.out.printf("HashMap: %d ns%n", hashMapAddTime);
        System.out.printf("TreeMap: %d ns%n", treeMapAddTime);
        
        // Get performance
        startTime = System.nanoTime();
        for (int i = 0; i < 1000; i++) {
            hashMap.get(ThreadLocalRandom.current().nextInt(elementCount));
        }
        long hashMapGetTime = System.nanoTime() - startTime;
        
        startTime = System.nanoTime();
        for (int i = 0; i < 1000; i++) {
            treeMap.get(ThreadLocalRandom.current().nextInt(elementCount));
        }
        long treeMapGetTime = System.nanoTime() - startTime;
        
        System.out.println("\nGet Performance:");
        System.out.printf("HashMap: %d ns%n", hashMapGetTime);
        System.out.printf("TreeMap: %d ns%n", treeMapGetTime);
    }
    
    /**
     * Demonstrates common Map operations
     */
    public static void demonstrateMapOperations() {
        Map<String, Integer> scores = new HashMap<>();
        
        // Basic operations
        scores.put("Alice", 95);
        scores.put("Bob", 85);
        scores.put("Charlie", 90);
        
        System.out.println("Original map: " + scores);
        
        // putIfAbsent
        scores.putIfAbsent("Alice", 100); // Won't change Alice's score
        scores.putIfAbsent("David", 88);  // Adds David
        
        // computeIfPresent
        scores.computeIfPresent("Bob", (k, v) -> v + 5);
        
        // computeIfAbsent
        scores.computeIfAbsent("Eve", k -> 75);
        
        System.out.println("After computations: " + scores);
        
        // Iteration methods
        System.out.println("\nDifferent iteration methods:");
        
        // forEach
        scores.forEach((name, score) -> 
            System.out.printf("%s scored %d%n", name, score));
            
        // entrySet
        for (Map.Entry<String, Integer> entry : scores.entrySet()) {
            System.out.printf("%s: %d%n", entry.getKey(), entry.getValue());
        }
    }
    
    /**
     * Demonstrates NavigableMap operations with TreeMap
     */
    public static void demonstrateNavigableMap() {
        NavigableMap<Integer, String> grades = new TreeMap<>();
        grades.put(90, "A");
        grades.put(80, "B");
        grades.put(70, "C");
        grades.put(60, "D");
        
        System.out.println("Grade for 85: " + grades.floorEntry(85).getValue());
        System.out.println("Grade for 75: " + grades.floorEntry(75).getValue());
        
        System.out.println("\nGrades descending: " + 
            grades.descendingMap());
            
        System.out.println("Subset of grades (70-90): " + 
            grades.subMap(70, true, 90, true));
    }
    
    public static void main(String[] args) {
        System.out.println("Map Performance Comparison:");
        System.out.println("=========================");
        compareMapPerformance();
        
        System.out.println("\nMap Operations Demo:");
        System.out.println("==================");
        demonstrateMapOperations();
        
        System.out.println("\nNavigableMap Demo:");
        System.out.println("=================");
        demonstrateNavigableMap();
    }
} 