/**
 * Test cases for CustomStack implementation
 */
public class CustomStackTest {
    private CustomStack<String> stack;
    
    @BeforeEach
    void setUp() {
        stack = new CustomStack<>();
    }
    
    @Test
    void testPushAndPop() {
        stack.push("first");
        stack.push("second");
        
        assertEquals("second", stack.pop());
        assertEquals("first", stack.pop());
        assertTrue(stack.isEmpty());
    }
    
    @Test
    void testPeek() {
        stack.push("element");
        assertEquals("element", stack.peek());
        assertEquals(1, stack.size());
    }
    
    @Test
    void testEmptyStack() {
        assertThrows(StackEmptyException.class, () -> stack.pop());
        assertThrows(StackEmptyException.class, () -> stack.peek());
    }
    
    @Test
    void testSearch() {
        stack.push("first");
        stack.push("second");
        stack.push("third");
        
        assertEquals(1, stack.search("third"));
        assertEquals(2, stack.search("second"));
        assertEquals(3, stack.search("first"));
        assertEquals(-1, stack.search("notfound"));
    }
    
    @Test
    void testClear() {
        stack.push("first");
        stack.push("second");
        stack.clear();
        
        assertTrue(stack.isEmpty());
        assertEquals(0, stack.size());
    }
    
    @Test
    void testToArray() {
        stack.push("first");
        stack.push("second");
        
        String[] array = stack.toArray();
        assertEquals(2, array.length);
        assertEquals("first", array[0]);
        assertEquals("second", array[1]);
    }
    
    @Test
    void testIterator() {
        stack.push("first");
        stack.push("second");
        stack.push("third");
        
        List<String> items = new ArrayList<>();
        for (String item : stack) {
            items.add(item);
        }
        
        assertEquals(Arrays.asList("third", "second", "first"), items);
    }
    
    @Test
    void testGrowth() {
        int initialCapacity = stack.capacity();
        
        // Push more elements than initial capacity
        for (int i = 0; i < initialCapacity + 1; i++) {
            stack.push("element" + i);
        }
        
        assertTrue(stack.capacity() > initialCapacity);
        assertEquals(initialCapacity + 1, stack.size());
    }
    
    @Test
    void testShrink() {
        // Push many elements
        for (int i = 0; i < 20; i++) {
            stack.push("element" + i);
        }
        
        int fullCapacity = stack.capacity();
        
        // Pop most elements
        for (int i = 0; i < 15; i++) {
            stack.pop();
        }
        
        assertTrue(stack.capacity() < fullCapacity);
    }
    
    @Test
    void testStats() {
        stack.push("first");
        stack.push("second");
        stack.search("first");
        stack.search("notfound");
        stack.pop();
        
        StackStats stats = stack.getStats();
        assertEquals(2, stats.getPushCount());
        assertEquals(1, stats.getPopCount());
        assertEquals(2, stats.getSearchCount());
        assertEquals(0.5, stats.getSearchHitRate());
    }
} 