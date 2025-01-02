/**
 * Test cases for NumberProcessor implementation
 */
public class NumberProcessorTest {
    private NumberProcessor processor;
    
    @BeforeEach
    void setUp() {
        processor = new NumberProcessor();
    }
    
    @Test
    void testFindPrimes() {
        List<Integer> primes = processor.findPrimes(1, 20);
        assertEquals(Arrays.asList(2, 3, 5, 7, 11, 13, 17, 19), primes);
    }
    
    @Test
    void testFibonacci() {
        List<Long> fib = processor.fibonacci(8);
        assertEquals(Arrays.asList(0L, 1L, 1L, 2L, 3L, 5L, 8L, 13L), fib);
    }
    
    @Test
    void testFindPerfectNumbers() {
        List<Integer> perfect = processor.findPerfectNumbers(30);
        assertEquals(Arrays.asList(6, 28), perfect);
    }
    
    @Test
    void testGroupByDigitSum() {
        List<Integer> numbers = Arrays.asList(11, 22, 13, 31, 15);
        Map<Integer, List<Integer>> groups = processor.groupByDigitSum(numbers);
        
        assertEquals(Arrays.asList(11), groups.get(2));
        assertEquals(Arrays.asList(22), groups.get(4));
        assertEquals(Arrays.asList(13, 31), groups.get(4));
        assertEquals(Arrays.asList(15), groups.get(6));
    }
    
    @Test
    void testFindPalindromes() {
        List<Integer> numbers = Arrays.asList(121, 123, 555, 1234, 12321);
        List<Integer> palindromes = processor.findPalindromes(numbers);
        assertEquals(Arrays.asList(121, 555, 12321), palindromes);
    }
    
    @Test
    void testGenerateStats() {
        List<Integer> numbers = Arrays.asList(1, 2, 2, 3, 4, 5);
        NumberStats stats = processor.generateStats(numbers);
        
        assertEquals(6, stats.getCount());
        assertEquals(17.0, stats.getSum());
        assertEquals(2.833, stats.getAverage(), 0.001);
        assertEquals(1.0, stats.getMin());
        assertEquals(5.0, stats.getMax());
        assertEquals(Integer.valueOf(2), stats.getMode());
        assertEquals(2.5, stats.getMedian());
    }
    
    @Test
    void testTransformNumbers() {
        List<Integer> numbers = Arrays.asList(1, 2, 3, 4);
        List<UnaryOperator<Double>> operations = Arrays.asList(
            x -> x * 2,
            x -> x + 1,
            x -> Math.pow(x, 2)
        );
        
        List<Double> transformed = processor.transformNumbers(numbers, operations);
        assertEquals(Arrays.asList(9.0, 25.0, 49.0, 81.0), transformed);
    }
} 