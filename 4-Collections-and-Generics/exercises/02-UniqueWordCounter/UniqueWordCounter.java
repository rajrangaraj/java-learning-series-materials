/**
 * Analyzes text and counts unique words with various statistics
 */
public class UniqueWordCounter {
    private final Map<String, Integer> wordFrequency;
    private final Set<String> stopWords;
    private final WordStats stats;
    
    public UniqueWordCounter() {
        this(Collections.emptySet());
    }
    
    public UniqueWordCounter(Set<String> stopWords) {
        this.wordFrequency = new HashMap<>();
        this.stopWords = new HashSet<>(stopWords);
        this.stats = new WordStats();
    }
    
    /**
     * Processes text from a file
     */
    public void processFile(String filePath) throws IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                processText(line);
            }
        }
    }
    
    /**
     * Processes a text string
     */
    public void processText(String text) {
        if (text == null || text.trim().isEmpty()) {
            return;
        }
        
        Arrays.stream(text.split("\\s+"))
              .map(this::cleanWord)
              .filter(this::isValidWord)
              .forEach(this::addWord);
    }
    
    /**
     * Cleans a word by removing punctuation and converting to lowercase
     */
    private String cleanWord(String word) {
        return word.replaceAll("[^a-zA-Z0-9']", "").toLowerCase();
    }
    
    /**
     * Checks if a word is valid (non-empty and not a stop word)
     */
    private boolean isValidWord(String word) {
        return !word.isEmpty() && 
               !stopWords.contains(word) && 
               !word.matches("\\d+");
    }
    
    /**
     * Adds a word to the frequency counter
     */
    private void addWord(String word) {
        wordFrequency.merge(word, 1, Integer::sum);
        stats.recordWord(word);
    }
    
    /**
     * Gets the frequency of a specific word
     */
    public int getWordFrequency(String word) {
        return wordFrequency.getOrDefault(cleanWord(word), 0);
    }
    
    /**
     * Gets the most common words with their frequencies
     */
    public List<Map.Entry<String, Integer>> getMostCommonWords(int limit) {
        return wordFrequency.entrySet().stream()
                .sorted(Map.Entry.<String, Integer>comparingByValue().reversed()
                        .thenComparing(Map.Entry.comparingByKey()))
                .limit(limit)
                .collect(Collectors.toList());
    }
    
    /**
     * Gets words that appear exactly n times
     */
    public Set<String> getWordsWithFrequency(int frequency) {
        return wordFrequency.entrySet().stream()
                .filter(e -> e.getValue() == frequency)
                .map(Map.Entry::getKey)
                .collect(Collectors.toSet());
    }
    
    /**
     * Exports statistics to a file
     */
    public void exportStats(String filePath) throws IOException {
        try (PrintWriter writer = new PrintWriter(new FileWriter(filePath))) {
            writer.println("Word Frequency Analysis");
            writer.println("=====================");
            writer.println();
            
            writer.println("Summary Statistics:");
            writer.println(stats.toString());
            writer.println();
            
            writer.println("Most Common Words:");
            getMostCommonWords(10).forEach(entry -> 
                writer.printf("%s: %d occurrences%n", 
                    entry.getKey(), entry.getValue()));
            writer.println();
            
            writer.println("Word Length Distribution:");
            stats.getWordLengthDistribution().forEach((length, count) ->
                writer.printf("%d letters: %d words%n", length, count));
        }
    }
    
    /**
     * Gets the current statistics
     */
    public WordStats getStats() {
        return stats;
    }
    
    /**
     * Clears all counted words and statistics
     */
    public void clear() {
        wordFrequency.clear();
        stats.reset();
    }
} 