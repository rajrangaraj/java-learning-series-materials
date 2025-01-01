/**
 * Test cases for UniqueWordCounter implementation
 */
public class UniqueWordCounterTest {
    private UniqueWordCounter counter;
    
    @BeforeEach
    void setUp() {
        counter = new UniqueWordCounter(StopWords.getCommonStopWords());
    }
    
    @Test
    void testBasicWordCounting() {
        counter.processText("hello world hello java world");
        
        assertEquals(2, counter.getWordFrequency("hello"));
        assertEquals(2, counter.getWordFrequency("world"));
        assertEquals(1, counter.getWordFrequency("java"));
    }
    
    @Test
    void testCaseInsensitivity() {
        counter.processText("Hello WORLD hello World");
        assertEquals(2, counter.getWordFrequency("hello"));
        assertEquals(2, counter.getWordFrequency("world"));
    }
    
    @Test
    void testPunctuation() {
        counter.processText("hello, world! hello; world.");
        assertEquals(2, counter.getWordFrequency("hello"));
        assertEquals(2, counter.getWordFrequency("world"));
    }
    
    @Test
    void testStopWords() {
        counter.processText("the quick brown fox and the lazy dog");
        assertEquals(0, counter.getWordFrequency("the"));
        assertEquals(0, counter.getWordFrequency("and"));
        assertEquals(1, counter.getWordFrequency("quick"));
    }
    
    @Test
    void testMostCommonWords() {
        counter.processText("apple banana apple cherry banana apple");
        List<Map.Entry<String, Integer>> common = counter.getMostCommonWords(2);
        
        assertEquals("apple", common.get(0).getKey());
        assertEquals(3, common.get(0).getValue());
        assertEquals("banana", common.get(1).getKey());
        assertEquals(2, common.get(1).getValue());
    }
    
    @Test
    void testWordStats() {
        counter.processText("programming in java is fun");
        WordStats stats = counter.getStats();
        
        assertEquals(3, stats.getTotalWords()); // excluding stop words
        assertEquals("programming", stats.getLongestWord());
    }
    
    @Test
    void testClear() {
        counter.processText("hello world");
        counter.clear();
        assertEquals(0, counter.getWordFrequency("hello"));
        assertEquals(0, counter.getWordFrequency("world"));
    }
} 