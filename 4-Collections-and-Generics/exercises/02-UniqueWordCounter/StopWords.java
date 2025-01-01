/**
 * Common English stop words
 */
public class StopWords {
    private static final Set<String> STOP_WORDS = new HashSet<>(Arrays.asList(
        "a", "an", "and", "are", "as", "at", "be", "by", "for", "from",
        "has", "he", "in", "is", "it", "its", "of", "on", "that", "the",
        "to", "was", "were", "will", "with"
    ));
    
    /**
     * Gets the set of common English stop words
     */
    public static Set<String> getCommonStopWords() {
        return Collections.unmodifiableSet(STOP_WORDS);
    }
    
    /**
     * Loads custom stop words from a file
     */
    public static Set<String> loadFromFile(String filePath) throws IOException {
        Set<String> stopWords = new HashSet<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                stopWords.add(line.trim().toLowerCase());
            }
        }
        return stopWords;
    }
} 