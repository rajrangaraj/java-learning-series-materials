/**
 * Generic stack implementation with additional features
 */
public class CustomStack<T> implements Iterable<T> {
    private static final int DEFAULT_INITIAL_CAPACITY = 10;
    private static final float GROWTH_FACTOR = 1.5f;
    private static final float SHRINK_FACTOR = 0.25f;
    
    private T[] elements;
    private int size;
    private final StackStats stats;
    
    @SuppressWarnings("unchecked")
    public CustomStack() {
        this(DEFAULT_INITIAL_CAPACITY);
    }
    
    @SuppressWarnings("unchecked")
    public CustomStack(int initialCapacity) {
        if (initialCapacity <= 0) {
            throw new IllegalArgumentException("Initial capacity must be positive");
        }
        this.elements = (T[]) new Object[initialCapacity];
        this.size = 0;
        this.stats = new StackStats();
    }
    
    /**
     * Pushes an element onto the stack
     */
    public void push(T element) {
        ensureCapacity();
        elements[size++] = element;
        stats.recordPush();
    }
    
    /**
     * Pops an element from the stack
     */
    public T pop() {
        if (isEmpty()) {
            throw new StackEmptyException("Cannot pop from empty stack");
        }
        
        T element = elements[--size];
        elements[size] = null; // Help GC
        
        shrinkIfNeeded();
        stats.recordPop();
        return element;
    }
    
    /**
     * Peeks at the top element without removing it
     */
    public T peek() {
        if (isEmpty()) {
            throw new StackEmptyException("Cannot peek empty stack");
        }
        return elements[size - 1];
    }
    
    /**
     * Searches for an element in the stack
     * Returns 1-based position from the top
     */
    public int search(T element) {
        for (int i = size - 1; i >= 0; i--) {
            if (Objects.equals(elements[i], element)) {
                int position = size - i;
                stats.recordSearch(true);
                return position;
            }
        }
        stats.recordSearch(false);
        return -1;
    }
    
    /**
     * Clears all elements from the stack
     */
    @SuppressWarnings("unchecked")
    public void clear() {
        elements = (T[]) new Object[DEFAULT_INITIAL_CAPACITY];
        size = 0;
        stats.recordClear();
    }
    
    /**
     * Converts stack to array
     */
    @SuppressWarnings("unchecked")
    public T[] toArray() {
        return Arrays.copyOf(elements, size);
    }
    
    /**
     * Ensures capacity for new elements
     */
    private void ensureCapacity() {
        if (size == elements.length) {
            int newCapacity = (int) (elements.length * GROWTH_FACTOR);
            elements = Arrays.copyOf(elements, newCapacity);
            stats.recordResize(elements.length);
        }
    }
    
    /**
     * Shrinks array if utilization is low
     */
    @SuppressWarnings("unchecked")
    private void shrinkIfNeeded() {
        if (size > 0 && size < elements.length * SHRINK_FACTOR) {
            int newCapacity = Math.max(DEFAULT_INITIAL_CAPACITY, 
                                     (int) (elements.length * SHRINK_FACTOR));
            elements = Arrays.copyOf(elements, newCapacity);
            stats.recordResize(elements.length);
        }
    }
    
    /**
     * Returns current size of the stack
     */
    public int size() {
        return size;
    }
    
    /**
     * Checks if stack is empty
     */
    public boolean isEmpty() {
        return size == 0;
    }
    
    /**
     * Gets current capacity of the stack
     */
    public int capacity() {
        return elements.length;
    }
    
    /**
     * Gets stack statistics
     */
    public StackStats getStats() {
        return stats;
    }
    
    @Override
    public Iterator<T> iterator() {
        return new StackIterator();
    }
    
    /**
     * Iterator implementation for the stack
     */
    private class StackIterator implements Iterator<T> {
        private int index = size - 1;
        
        @Override
        public boolean hasNext() {
            return index >= 0;
        }
        
        @Override
        public T next() {
            if (!hasNext()) {
                throw new NoSuchElementException();
            }
            return elements[index--];
        }
    }
} 