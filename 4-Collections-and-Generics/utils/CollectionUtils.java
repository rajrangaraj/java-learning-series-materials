/**
 * Utility methods for working with collections
 */
public class CollectionUtils {
    
    /**
     * Filters elements based on predicate
     */
    public static <T> List<T> filter(Collection<T> collection, 
                                   Predicate<T> predicate) {
        return collection.stream()
                        .filter(predicate)
                        .collect(Collectors.toList());
    }
    
    /**
     * Maps elements using provided function
     */
    public static <T, R> List<R> map(Collection<T> collection,
                                    Function<T, R> mapper) {
        return collection.stream()
                        .map(mapper)
                        .collect(Collectors.toList());
    }
    
    /**
     * Reduces collection to single value
     */
    public static <T> Optional<T> reduce(Collection<T> collection,
                                       BinaryOperator<T> reducer) {
        return collection.stream().reduce(reducer);
    }
    
    /**
     * Partitions collection based on predicate
     */
    public static <T> Map<Boolean, List<T>> partition(
            Collection<T> collection,
            Predicate<T> predicate) {
        return collection.stream()
                        .collect(Collectors.partitioningBy(predicate));
    }
    
    /**
     * Groups elements by key function
     */
    public static <T, K> Map<K, List<T>> groupBy(
            Collection<T> collection,
            Function<T, K> keyExtractor) {
        return collection.stream()
                        .collect(Collectors.groupingBy(keyExtractor));
    }
    
    /**
     * Finds first element matching predicate
     */
    public static <T> Optional<T> findFirst(
            Collection<T> collection,
            Predicate<T> predicate) {
        return collection.stream()
                        .filter(predicate)
                        .findFirst();
    }
    
    /**
     * Checks if any element matches predicate
     */
    public static <T> boolean any(Collection<T> collection,
                                Predicate<T> predicate) {
        return collection.stream().anyMatch(predicate);
    }
    
    /**
     * Checks if all elements match predicate
     */
    public static <T> boolean all(Collection<T> collection,
                                Predicate<T> predicate) {
        return collection.stream().allMatch(predicate);
    }
} 