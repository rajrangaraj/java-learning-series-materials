/**
 * Tests for FileProcessor implementation
 */
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class FileProcessorTest {
    private Path tempDir;
    private Path inputDir;
    private Path outputDir;
    private Path errorDir;
    private FileProcessor processor;
    private ProcessingConfig config;
    
    @BeforeAll
    void setUp() throws IOException {
        tempDir = Files.createTempDirectory("file-processor-test");
        inputDir = tempDir.resolve("input");
        outputDir = tempDir.resolve("output");
        errorDir = tempDir.resolve("error");
        
        Files.createDirectories(inputDir);
        
        config = new ProcessingConfig.Builder()
            .maxFileSize(1024 * 1024) // 1MB
            .maxFileAge(30)
            .minimumFiles(1)
            .upperCase(true)
            .trimLength(100)
            .requiredColumns("id", "name", "value")
            .requiredJsonFields("type", "data")
            .requireJsonObject(true)
            .build();
        
        processor = new FileProcessor(inputDir, outputDir, errorDir, config);
    }
    
    @AfterAll
    void tearDown() throws IOException {
        deleteDirectory(tempDir);
    }
    
    @Test
    void testProcessTextFile() throws IOException {
        // Create test file
        Path textFile = inputDir.resolve("test.txt");
        List<String> lines = Arrays.asList(
            "This is a test file",
            "with multiple lines",
            "to process"
        );
        Files.write(textFile, lines);
        
        // Process files
        assertDoesNotThrow(() -> processor.processFiles());
        
        // Verify output
        Path outputFile = outputDir.resolve("test.txt");
        assertTrue(Files.exists(outputFile));
        List<String> outputLines = Files.readAllLines(outputFile);
        
        assertEquals(3, outputLines.size());
        assertEquals("THIS IS A TEST FILE", outputLines.get(0));
        assertEquals("WITH MULTIPLE LINES", outputLines.get(1));
        assertEquals("TO PROCESS", outputLines.get(2));
        
        // Verify stats
        ProcessingStats stats = processor.getStats();
        assertEquals(1, stats.getProcessedFiles());
        assertEquals(0, stats.getValidationErrors());
        assertEquals(0, stats.getProcessingErrors());
    }
    
    @Test
    void testProcessCsvFile() throws IOException {
        // Create test file
        Path csvFile = inputDir.resolve("test.csv");
        List<String> lines = Arrays.asList(
            "id,name,value",
            "1,test1,100",
            "2,test2,200"
        );
        Files.write(csvFile, lines);
        
        // Process files
        assertDoesNotThrow(() -> processor.processFiles());
        
        // Verify output
        Path outputFile = outputDir.resolve("test.csv");
        assertTrue(Files.exists(outputFile));
        List<String> outputLines = Files.readAllLines(outputFile);
        
        assertEquals(3, outputLines.size());
        assertTrue(outputLines.get(0).contains("id,name,value"));
        assertTrue(outputLines.get(1).contains("1,TEST1,100"));
        assertTrue(outputLines.get(2).contains("2,TEST2,200"));
    }
    
    @Test
    void testProcessJsonFile() throws IOException {
        // Create test file
        Path jsonFile = inputDir.resolve("test.json");
        String json = """
            {
                "type": "test",
                "data": {
                    "id": 1,
                    "name": "test item"
                }
            }
            """;
        Files.write(jsonFile, json.getBytes());
        
        // Process files
        assertDoesNotThrow(() -> processor.processFiles());
        
        // Verify output
        Path outputFile = outputDir.resolve("test.json");
        assertTrue(Files.exists(outputFile));
        
        JsonNode root = new ObjectMapper().readTree(outputFile.toFile());
        assertTrue(root.has("processedAt"));
        assertTrue(root.has("content"));
        assertEquals("test", root.get("content").get("type").asText());
    }
    
    @Test
    void testValidationFailure() throws IOException {
        // Create oversized file
        Path largeFile = inputDir.resolve("large.txt");
        byte[] data = new byte[2 * 1024 * 1024]; // 2MB
        Files.write(largeFile, data);
        
        // Process files
        assertDoesNotThrow(() -> processor.processFiles());
        
        // Verify error file
        Path errorFile = errorDir.resolve("large.txt.error");
        assertTrue(Files.exists(errorFile));
        
        JsonNode error = new ObjectMapper().readTree(errorFile.toFile());
        assertEquals("File too large", error.get("error").asText());
        
        // Verify stats
        ProcessingStats stats = processor.getStats();
        assertEquals(0, stats.getProcessedFiles());
        assertEquals(1, stats.getValidationErrors());
    }
    
    @Test
    void testInvalidCsvFormat() throws IOException {
        // Create invalid CSV file
        Path csvFile = inputDir.resolve("invalid.csv");
        List<String> lines = Arrays.asList(
            "invalid_column",
            "1,test1",
            "2,test2"
        );
        Files.write(csvFile, lines);
        
        // Process files
        assertDoesNotThrow(() -> processor.processFiles());
        
        // Verify error file
        Path errorFile = errorDir.resolve("invalid.csv.error");
        assertTrue(Files.exists(errorFile));
        
        JsonNode error = new ObjectMapper().readTree(errorFile.toFile());
        assertTrue(error.get("error").asText().contains("Missing required columns"));
        
        // Verify stats
        ProcessingStats stats = processor.getStats();
        assertEquals(0, stats.getProcessedFiles());
        assertEquals(0, stats.getValidationErrors());
        assertEquals(1, stats.getProcessingErrors());
    }
    
    @Test
    void testInvalidJsonFormat() throws IOException {
        // Create invalid JSON file
        Path jsonFile = inputDir.resolve("invalid.json");
        String json = """
            {
                "missing_required_fields": true
            }
            """;
        Files.write(jsonFile, json.getBytes());
        
        // Process files
        assertDoesNotThrow(() -> processor.processFiles());
        
        // Verify error file
        Path errorFile = errorDir.resolve("invalid.json.error");
        assertTrue(Files.exists(errorFile));
        
        JsonNode error = new ObjectMapper().readTree(errorFile.toFile());
        assertTrue(error.get("error").asText().contains("Missing required field"));
        
        // Verify stats
        ProcessingStats stats = processor.getStats();
        assertEquals(0, stats.getProcessedFiles());
        assertEquals(0, stats.getValidationErrors());
        assertEquals(1, stats.getProcessingErrors());
    }
    
    @Test
    void testUnsupportedFileType() throws IOException {
        // Create unsupported file
        Path unsupportedFile = inputDir.resolve("test.xyz");
        Files.write(unsupportedFile, "test".getBytes());
        
        // Process files
        assertDoesNotThrow(() -> processor.processFiles());
        
        // Verify error file
        Path errorFile = errorDir.resolve("test.xyz.error");
        assertTrue(Files.exists(errorFile));
        
        JsonNode error = new ObjectMapper().readTree(errorFile.toFile());
        assertEquals("UNSUPPORTED_FILE", error.get("errorCode").asText());
        
        // Verify stats
        ProcessingStats stats = processor.getStats();
        assertEquals(0, stats.getProcessedFiles());
        assertEquals(0, stats.getValidationErrors());
        assertEquals(1, stats.getProcessingErrors());
    }
    
    @Test
    void testMinimumFilesRequirement() throws IOException {
        // Set minimum files requirement
        config = new ProcessingConfig.Builder()
            .minimumFiles(2)
            .build();
        processor = new FileProcessor(inputDir, outputDir, errorDir, config);
        
        // Create single file
        Path textFile = inputDir.resolve("test.txt");
        Files.write(textFile, "test".getBytes());
        
        // Process files
        ProcessingException exception = assertThrows(
            ProcessingException.class,
            () -> processor.processFiles()
        );
        
        assertEquals("INSUFFICIENT_FILES", exception.getErrorCode());
        assertEquals("2", exception.getDetails().get("required"));
        assertEquals("1", exception.getDetails().get("processed"));
    }
    
    @Test
    void testConcurrentProcessing() throws IOException, InterruptedException {
        // Create multiple files
        for (int i = 0; i < 10; i++) {
            Path file = inputDir.resolve("test" + i + ".txt");
            Files.write(file, ("test content " + i).getBytes());
        }
        
        // Process files using multiple threads
        ExecutorService executor = Executors.newFixedThreadPool(4);
        List<Future<?>> futures = new ArrayList<>();
        
        for (int i = 0; i < 4; i++) {
            futures.add(executor.submit(() -> {
                try {
                    processor.processFiles();
                } catch (ProcessingException e) {
                    fail("Unexpected exception: " + e);
                }
            }));
        }
        
        // Wait for all threads to complete
        for (Future<?> future : futures) {
            future.get();
        }
        
        // Verify results
        ProcessingStats stats = processor.getStats();
        assertEquals(10, stats.getProcessedFiles());
        assertEquals(0, stats.getValidationErrors());
        assertEquals(0, stats.getProcessingErrors());
        
        executor.shutdown();
    }
    
    private void deleteDirectory(Path dir) throws IOException {
        if (Files.exists(dir)) {
            Files.walk(dir)
                 .sorted(Comparator.reverseOrder())
                 .forEach(path -> {
                     try {
                         Files.delete(path);
                     } catch (IOException e) {
                         System.err.println("Failed to delete: " + path);
                     }
                 });
        }
    }
} 