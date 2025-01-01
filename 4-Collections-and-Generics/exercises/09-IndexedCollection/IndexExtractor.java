/**
 * Interface for extracting index keys from elements
 */
@FunctionalInterface
public interface IndexExtractor<T> {
    Object extractKey(T element);
} 