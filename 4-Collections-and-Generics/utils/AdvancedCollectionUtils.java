/**
 * Advanced collection utility methods
 */
public class AdvancedCollectionUtils {
    
    /**
     * Performs a sliding window operation on a list
     */
    public static <T> List<List<T>> slidingWindow(List<T> list, int windowSize) {
        if (windowSize > list.size()) {
            return Collections.emptyList();
        }
        
        List<List<T>> result = new ArrayList<>();
        for (int i = 0; i <= list.size() - windowSize; i++) {
            result.add(list.subList(i, i + windowSize));
        }
        return result;
    }
    
    /**
     * Creates combinations of elements
     */
    public static <T> Set<Set<T>> combinations(Set<T> elements, int k) {
        Set<Set<T>> result = new HashSet<>();
        if (k == 0) {
            result.add(new HashSet<>());
            return result;
        }
        
        List<T> elementList = new ArrayList<>(elements);
        combinationsHelper(elementList, k, 0, new HashSet<>(), result);
        return result;
    }
    
    private static <T> void combinationsHelper(List<T> elements, int k, int start,
                                             Set<T> current, Set<Set<T>> result) {
        if (current.size() == k) {
            result.add(new HashSet<>(current));
            return;
        }
        
        for (int i = start; i < elements.size(); i++) {
            current.add(elements.get(i));
            combinationsHelper(elements, k, i + 1, current, result);
            current.remove(elements.get(i));
        }
    }
    
    /**
     * Performs a topological sort on a directed acyclic graph
     */
    public static <T> List<T> topologicalSort(Map<T, Set<T>> graph) {
        Set<T> visited = new HashSet<>();
        Stack<T> stack = new Stack<>();
        
        for (T node : graph.keySet()) {
            if (!visited.contains(node)) {
                topologicalSortUtil(node, graph, visited, stack);
            }
        }
        
        List<T> result = new ArrayList<>();
        while (!stack.isEmpty()) {
            result.add(stack.pop());
        }
        return result;
    }
    
    private static <T> void topologicalSortUtil(T node, Map<T, Set<T>> graph,
                                              Set<T> visited, Stack<T> stack) {
        visited.add(node);
        
        for (T neighbor : graph.getOrDefault(node, Collections.emptySet())) {
            if (!visited.contains(neighbor)) {
                topologicalSortUtil(neighbor, graph, visited, stack);
            }
        }
        
        stack.push(node);
    }
} 