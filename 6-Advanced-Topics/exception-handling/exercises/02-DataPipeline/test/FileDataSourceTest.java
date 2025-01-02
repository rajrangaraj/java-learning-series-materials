/**
 * Tests for the FileDataSource implementation
 */
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class FileDataSourceTest {
    private Path tempDir;
    private FileDataSource source;
    
    @BeforeAll
    void setUp() throws IOException {
        tempDir = Files.createTempDirectory("source-test");
        createTestFiles();
        source = new FileDataSource(tempDir, "*.json", 100);
    }
    
    @AfterAll
    void tearDown() throws IOException {
        source.close();
        Files.walk(tempDir)
             .sorted(Comparator.reverseOrder())
             .forEach(path -> {
                 try {
                     Files.delete(path);
                 } catch (IOException e) {
                     e.printStackTrace();
                 }
             });
    }
    
    @Test
    void testFetchRecords() throws Exception {
        List<DataRecord> records = source.fetchRecords();
        assertFalse(records.isEmpty());
        
        DataRecord record = records.get(0);
        assertNotNull(record.getId());
        assertFalse(record.getData().isEmpty());
    }
    
    @Test
    void testHasMore() {
        assertTrue(source.hasMore());
        
        // Fetch all records
        while (source.hasMore()) {
            try {
                source.fetchRecords();
            } catch (Exception e) {
                fail("Unexpected exception: " + e.getMessage());
            }
        }
        
        assertFalse(source.hasMore());
    }
    
    @Test
    void testReset() throws Exception {
        // Fetch some records
        source.fetchRecords();
        
        // Reset
        source.reset();
        
        assertTrue(source.hasMore());
        assertFalse(source.fetchRecords().isEmpty());
    }
    
    @Test
    void testInvalidFile() throws IOException {
        // Create invalid JSON file
        Path invalidFile = tempDir.resolve("invalid.json");
        Files.write(invalidFile, "invalid json".getBytes());
        
        // Create new source instance
        FileDataSource invalidSource = new FileDataSource(
            tempDir, "invalid.json", 100);
        
        assertThrows(SourceUnavailableException.class, 
            invalidSource::fetchRecords);
    }
    
    private void createTestFiles() throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        
        for (int i = 0; i < 5; i++) {
            Map<String, Object> data = new HashMap<>();
            data.put("id", UUID.randomUUID().toString());
            data.put("value", "test" + i);
            data.put("timestamp", 
                LocalDateTime.now().toString());
            
            Path file = tempDir.resolve("test" + i + ".json");
            mapper.writeValue(file.toFile(), data);
        }
    }
} 