/**
 * Processes collections of numbers using streams and functional operations
 */
public class NumberProcessor {
    
    /**
     * Finds all prime numbers in the given range
     */
    public List<Integer> findPrimes(int start, int end) {
        return IntStream.rangeClosed(start, end)
                       .filter(this::isPrime)
                       .boxed()
                       .collect(Collectors.toList());
    }
    
    /**
     * Calculates Fibonacci numbers up to n
     */
    public List<Long> fibonacci(int n) {
        return Stream.iterate(new long[]{0, 1}, f -> new long[]{f[1], f[0] + f[1]})
                    .limit(n)
                    .map(f -> f[0])
                    .collect(Collectors.toList());
    }
    
    /**
     * Finds perfect numbers up to n (numbers equal to sum of their proper divisors)
     */
    public List<Integer> findPerfectNumbers(int n) {
        return IntStream.rangeClosed(1, n)
                       .filter(this::isPerfect)
                       .boxed()
                       .collect(Collectors.toList());
    }
    
    /**
     * Groups numbers by their digit sum
     */
    public Map<Integer, List<Integer>> groupByDigitSum(List<Integer> numbers) {
        return numbers.stream()
                     .collect(Collectors.groupingBy(this::digitSum));
    }
    
    /**
     * Finds numbers that are palindromes
     */
    public List<Integer> findPalindromes(List<Integer> numbers) {
        return numbers.stream()
                     .filter(this::isPalindrome)
                     .collect(Collectors.toList());
    }
    
    /**
     * Generates statistics for a collection of numbers
     */
    public NumberStats generateStats(List<Integer> numbers) {
        DoubleSummaryStatistics stats = numbers.stream()
                                              .mapToDouble(Integer::doubleValue)
                                              .summaryStatistics();
        
        return new NumberStats(
            stats.getCount(),
            stats.getSum(),
            stats.getAverage(),
            stats.getMin(),
            stats.getMax(),
            calculateMode(numbers),
            calculateMedian(numbers)
        );
    }
    
    /**
     * Transforms numbers using provided operations
     */
    public List<Double> transformNumbers(List<Integer> numbers, 
                                       List<UnaryOperator<Double>> operations) {
        return numbers.stream()
                     .map(Integer::doubleValue)
                     .map(n -> operations.stream()
                                       .reduce(UnaryOperator.identity(), 
                                             UnaryOperator::andThen)
                                       .apply(n))
                     .collect(Collectors.toList());
    }
    
    // Helper methods
    private boolean isPrime(int n) {
        if (n < 2) return false;
        return IntStream.rangeClosed(2, (int) Math.sqrt(n))
                       .noneMatch(i -> n % i == 0);
    }
    
    private boolean isPerfect(int n) {
        return n > 0 && IntStream.range(1, n)
                                .filter(i -> n % i == 0)
                                .sum() == n;
    }
    
    private int digitSum(int n) {
        return String.valueOf(Math.abs(n))
                    .chars()
                    .map(Character::getNumericValue)
                    .sum();
    }
    
    private boolean isPalindrome(int n) {
        String str = String.valueOf(Math.abs(n));
        return str.equals(new StringBuilder(str).reverse().toString());
    }
    
    private double calculateMedian(List<Integer> numbers) {
        List<Integer> sorted = numbers.stream()
                                    .sorted()
                                    .collect(Collectors.toList());
        int size = sorted.size();
        if (size == 0) return 0;
        
        if (size % 2 == 0) {
            return (sorted.get(size/2 - 1) + sorted.get(size/2)) / 2.0;
        } else {
            return sorted.get(size/2);
        }
    }
    
    private Integer calculateMode(List<Integer> numbers) {
        return numbers.stream()
                     .collect(Collectors.groupingBy(Function.identity(), 
                                                  Collectors.counting()))
                     .entrySet()
                     .stream()
                     .max(Map.Entry.comparingByValue())
                     .map(Map.Entry::getKey)
                     .orElse(null);
    }
} 