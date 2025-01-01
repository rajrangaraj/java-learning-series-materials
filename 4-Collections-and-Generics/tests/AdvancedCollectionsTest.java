import org.junit.Test;
import static org.junit.Assert.*;
import java.util.*;

public class AdvancedCollectionsTest {
    
    @Test
    public void testGenericTree() {
        GenericTree<Integer> tree = new GenericTree<>();
        tree.insert(5);
        tree.insert(3);
        tree.insert(7);
        tree.insert(1);
        tree.insert(9);
        
        List<Integer> inorder = tree.inorderTraversal();
        assertEquals(Arrays.asList(1, 3, 5, 7, 9), inorder);
        
        assertTrue(tree.contains(3));
        assertFalse(tree.contains(4));
    }
    
    @Test
    public void testGenericGraph() {
        GenericGraph<String> graph = new GenericGraph<>();
        graph.addVertex("A");
        graph.addVertex("B");
        graph.addVertex("C");
        
        graph.addEdge("A", "B", 1);
        graph.addEdge("B", "C", 2);
        graph.addEdge("A", "C", 4);
        
        List<String> shortestPath = graph.shortestPath("A", "C");
        assertEquals(Arrays.asList("A", "B", "C"), shortestPath);
    }
    
    @Test
    public void testAdvancedCollectionUtils() {
        List<Integer> numbers = Arrays.asList(1, 2, 3, 4, 5);
        
        // Test sliding window
        List<List<Integer>> windows = AdvancedCollectionUtils.slidingWindow(numbers, 3);
        assertEquals(3, windows.size());
        assertEquals(Arrays.asList(1, 2, 3), windows.get(0));
        
        // Test combinations
        Set<Integer> elements = new HashSet<>(Arrays.asList(1, 2, 3));
        Set<Set<Integer>> combinations = AdvancedCollectionUtils.combinations(elements, 2);
        assertEquals(3, combinations.size());
    }
    
    @Test
    public void testConcurrentCache() throws InterruptedException {
        ConcurrentCache<String, Integer> cache = 
            new ConcurrentCache<>(1000, 500); // 1 second expiration
        
        cache.put("A", 1);
        assertEquals(Optional.of(1), cache.get("A"));
        
        Thread.sleep(1200); // Wait for expiration
        assertEquals(Optional.empty(), cache.get("A"));
        
        cache.shutdown();
    }
    
    @Test
    public void testTopologicalSort() {
        Map<String, Set<String>> graph = new HashMap<>();
        graph.put("A", new HashSet<>(Arrays.asList("B", "C")));
        graph.put("B", new HashSet<>(Collections.singletonList("D")));
        graph.put("C", new HashSet<>(Collections.singletonList("D")));
        graph.put("D", new HashSet<>());
        
        List<String> sorted = AdvancedCollectionUtils.topologicalSort(graph);
        assertEquals(Arrays.asList("A", "C", "B", "D"), sorted);
    }
} 