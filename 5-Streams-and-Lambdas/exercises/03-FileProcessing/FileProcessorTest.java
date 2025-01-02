/**
 * Test cases for FileProcessor implementation
 */
public class FileProcessorTest {
    private Path tempDir;
    private FileProcessor processor;
    
    @BeforeEach
    void setUp() throws IOException {
        tempDir = Files.createTempDirectory("test");
        processor = new FileProcessor(tempDir.toString());
    }
    
    @AfterEach
    void tearDown() throws IOException {
        Files.walk(tempDir)
             .sorted(Comparator.reverseOrder())
             .forEach(path -> {
                 try {
                     Files.delete(path);
                 } catch (IOException e) {
                     // Ignore
                 }
             });
    }
    
    @Test
    void testFindFiles() throws IOException {
        createTestFile("test1.txt", "content", 10);
        createTestFile("test2.txt", "more content", 20);
        createTestFile("test.doc", "wrong extension", 15);
        
        List<Path> found = processor.findFiles(".txt", 5, 15);
        assertEquals(1, found.size());
        assertTrue(found.get(0).toString().endsWith("test1.txt"));
    }
    
    @Test
    void testSearchInFiles() throws IOException {
        createTestFile("test1.txt", "hello world\ntest line\nhello again");
        createTestFile("test2.txt", "no match here");
        
        Map<Path, List<String>> results = processor.searchInFiles("hello", ".txt");
        assertEquals(1, results.size());
        assertEquals(2, results.values().iterator().next().size());
    }
    
    @Test
    void testAnalyzeWordFrequency() throws IOException {
        createTestFile("test.txt", "hello world\nworld of java\njava programming");
        
        List<Path> files = Collections.singletonList(tempDir.resolve("test.txt"));
        Map<String, WordStats> stats = processor.analyzeWordFrequency(files);
        
        assertEquals(2, stats.get("world").getFrequency());
        assertEquals(2, stats.get("java").getFrequency());
    }
    
    @Test
    void testProcessFiles() throws IOException {
        createTestFile("input.txt", "line1\nline2\nline3");
        Path outputDir = tempDir.resolve("output");
        
        List<Path> files = Collections.singletonList(tempDir.resolve("input.txt"));
        processor.processFiles(files, String::toUpperCase, outputDir);
        
        Path outputFile = outputDir.resolve("input.txt");
        List<String> lines = Files.readAllLines(outputFile);
        assertEquals(Arrays.asList("LINE1", "LINE2", "LINE3"), lines);
    }
    
    // Helper method to create test files
    private void createTestFile(String name, String content) throws IOException {
        Path file = tempDir.resolve(name);
        Files.write(file, content.getBytes());
    }
    
    private void createTestFile(String name, String content, long size) 
            throws IOException {
        Path file = tempDir.resolve(name);
        byte[] bytes = new byte[(int) size];
        Arrays.fill(bytes, (byte) 'x');
        Files.write(file, bytes);
    }
} 