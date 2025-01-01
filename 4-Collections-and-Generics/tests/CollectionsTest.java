public class CollectionsTest {
    
    @Test
    public void testGenericStack() {
        GenericStack<String> stack = new GenericStack<>();
        
        // Test push and pop
        stack.push("First");
        stack.push("Second");
        assertEquals("Second", stack.pop());
        assertEquals("First", stack.pop());
        assertTrue(stack.isEmpty());
        
        // Test exception
        assertThrows(EmptyStackException.class, stack::pop);
    }
    
    @Test
    public void testGenericQueue() {
        GenericQueue<Integer> queue = new GenericQueue<>();
        
        // Test enqueue and dequeue
        queue.enqueue(1);
        queue.enqueue(2);
        queue.enqueue(3);
        
        assertEquals(Integer.valueOf(1), queue.dequeue());
        assertEquals(Integer.valueOf(2), queue.dequeue());
        assertEquals(Integer.valueOf(3), queue.dequeue());
        assertTrue(queue.isEmpty());
        
        // Test exception
        assertThrows(NoSuchElementException.class, queue::dequeue);
    }
    
    @Test
    public void testLRUCache() {
        LRUCache<String, Integer> cache = new LRUCache<>(2);
        
        cache.put("A", 1);
        cache.put("B", 2);
        cache.put("C", 3); // Should evict A
        
        assertNull(cache.get("A"));
        assertEquals(Integer.valueOf(2), cache.get("B"));
        assertEquals(Integer.valueOf(3), cache.get("C"));
    }
    
    @Test
    public void testCollectionUtils() {
        List<Integer> numbers = Arrays.asList(1, 2, 3, 4, 5);
        
        // Test filter
        List<Integer> evens = CollectionUtils.filter(numbers, n -> n % 2 == 0);
        assertEquals(Arrays.asList(2, 4), evens);
        
        // Test map
        List<String> strings = CollectionUtils.map(numbers, Object::toString);
        assertEquals(Arrays.asList("1", "2", "3", "4", "5"), strings);
        
        // Test reduce
        Optional<Integer> sum = CollectionUtils.reduce(numbers, Integer::sum);
        assertEquals(Integer.valueOf(15), sum.orElse(0));
    }
} 