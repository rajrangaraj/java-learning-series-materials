/**
 * Statistics tracking for word counting
 */
public class WordStats {
    private int totalWords;
    private int uniqueWords;
    private String longestWord;
    private final Map<Integer, Integer> wordLengthDistribution;
    
    public WordStats() {
        this.totalWords = 0;
        this.uniqueWords = 0;
        this.longestWord = "";
        this.wordLengthDistribution = new TreeMap<>();
    }
    
    /**
     * Records a word in the statistics
     */
    public void recordWord(String word) {
        totalWords++;
        
        // Update word length distribution
        int length = word.length();
        wordLengthDistribution.merge(length, 1, Integer::sum);
        
        // Update longest word if necessary
        if (word.length() > longestWord.length()) {
            longestWord = word;
        }
    }
    
    /**
     * Updates the count of unique words
     */
    public void setUniqueWords(int count) {
        this.uniqueWords = count;
    }
    
    /**
     * Gets the total number of words processed
     */
    public int getTotalWords() {
        return totalWords;
    }
    
    /**
     * Gets the number of unique words
     */
    public int getUniqueWords() {
        return uniqueWords;
    }
    
    /**
     * Gets the longest word encountered
     */
    public String getLongestWord() {
        return longestWord;
    }
    
    /**
     * Gets the distribution of word lengths
     */
    public Map<Integer, Integer> getWordLengthDistribution() {
        return Collections.unmodifiableMap(wordLengthDistribution);
    }
    
    /**
     * Gets the average word length
     */
    public double getAverageWordLength() {
        long totalLength = wordLengthDistribution.entrySet().stream()
            .mapToLong(e -> (long) e.getKey() * e.getValue())
            .sum();
        return totalWords == 0 ? 0 : (double) totalLength / totalWords;
    }
    
    /**
     * Resets all statistics
     */
    public void reset() {
        totalWords = 0;
        uniqueWords = 0;
        longestWord = "";
        wordLengthDistribution.clear();
    }
    
    @Override
    public String toString() {
        return String.format(
            "Total words: %d%n" +
            "Unique words: %d%n" +
            "Longest word: '%s' (%d letters)%n" +
            "Average word length: %.2f letters",
            totalWords, uniqueWords, longestWord, 
            longestWord.length(), getAverageWordLength());
    }
} 