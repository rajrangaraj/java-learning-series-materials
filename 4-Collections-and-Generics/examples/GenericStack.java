/**
 * Generic stack implementation with dynamic resizing
 */
public class GenericStack<T> {
    private T[] elements;
    private int size;
    private static final int DEFAULT_CAPACITY = 10;
    
    @SuppressWarnings("unchecked")
    public GenericStack() {
        elements = (T[]) new Object[DEFAULT_CAPACITY];
        size = 0;
    }
    
    public void push(T element) {
        ensureCapacity();
        elements[size++] = element;
    }
    
    public T pop() {
        if (isEmpty()) {
            throw new EmptyStackException();
        }
        T element = elements[--size];
        elements[size] = null; // Help GC
        return element;
    }
    
    public T peek() {
        if (isEmpty()) {
            throw new EmptyStackException();
        }
        return elements[size - 1];
    }
    
    public boolean isEmpty() {
        return size == 0;
    }
    
    public int size() {
        return size;
    }
    
    @SuppressWarnings("unchecked")
    private void ensureCapacity() {
        if (size == elements.length) {
            T[] newElements = (T[]) new Object[elements.length * 2];
            System.arraycopy(elements, 0, newElements, 0, size);
            elements = newElements;
        }
    }
} 