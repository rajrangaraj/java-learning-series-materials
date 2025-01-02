/**
 * Tests for the DataProcessor class
 */
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class DataProcessorTest {
    private DataSource mockSource;
    private DataTransformer mockTransformer;
    private DataValidator mockValidator;
    private DataSink mockSink;
    private ErrorHandler mockErrorHandler;
    private DataProcessor processor;
    private Path tempDir;
    
    @BeforeAll
    void setUp() throws IOException {
        tempDir = Files.createTempDirectory("data-processor-test");
        mockSource = mock(DataSource.class);
        mockTransformer = mock(DataTransformer.class);
        mockValidator = mock(DataValidator.class);
        mockSink = mock(DataSink.class);
        mockErrorHandler = mock(ErrorHandler.class);
        
        processor = new DataProcessor(
            mockSource, mockTransformer, mockValidator,
            mockSink, mockErrorHandler);
    }
    
    @AfterAll
    void tearDown() throws IOException {
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
    void testSuccessfulProcessing() throws Exception {
        // Prepare test data
        DataRecord record = createTestRecord();
        List<DataRecord> records = Collections.singletonList(record);
        
        // Configure mocks
        when(mockSource.fetchRecords()).thenReturn(records);
        when(mockTransformer.transform(any())).thenReturn(record);
        when(mockValidator.validate(any()))
            .thenReturn(new ValidationResult(ValidationLevel.NORMAL));
        
        // Start processing
        processor.startProcessing();
        Thread.sleep(1000); // Allow time for processing
        processor.shutdown();
        
        // Verify
        verify(mockSource, atLeastOnce()).fetchRecords();
        verify(mockTransformer).transform(record);
        verify(mockValidator).validate(record);
        verify(mockSink).write(record);
        
        ProcessingStats stats = processor.getStats();
        assertEquals(1, stats.getSuccessCount());
        assertEquals(0, stats.getErrorCount());
    }
    
    @Test
    void testHandleSourceError() throws Exception {
        // Configure mock to throw exception
        when(mockSource.fetchRecords())
            .thenThrow(new SourceUnavailableException("Test error"));
        
        // Start processing
        processor.startProcessing();
        Thread.sleep(1000);
        processor.shutdown();
        
        // Verify error handling
        verify(mockErrorHandler)
            .handleSourceError(any(SourceUnavailableException.class));
        
        ProcessingStats stats = processor.getStats();
        assertTrue(stats.getErrorCount() > 0);
    }
    
    @Test
    void testHandleTransformationError() throws Exception {
        // Prepare test data
        DataRecord record = createTestRecord();
        List<DataRecord> records = Collections.singletonList(record);
        
        // Configure mocks
        when(mockSource.fetchRecords()).thenReturn(records);
        when(mockTransformer.transform(any()))
            .thenThrow(new TransformationException("Test error"));
        
        // Start processing
        processor.startProcessing();
        Thread.sleep(1000);
        processor.shutdown();
        
        // Verify error handling
        verify(mockErrorHandler)
            .handleTransformationError(eq(record), 
                any(TransformationException.class));
        
        ProcessingStats stats = processor.getStats();
        assertTrue(stats.getTransformErrors() > 0);
    }
    
    @Test
    void testHandleValidationError() throws Exception {
        // Prepare test data
        DataRecord record = createTestRecord();
        List<DataRecord> records = Collections.singletonList(record);
        
        // Configure mocks
        when(mockSource.fetchRecords()).thenReturn(records);
        when(mockTransformer.transform(any())).thenReturn(record);
        
        ValidationResult invalidResult = new ValidationResult(
            ValidationLevel.STRICT);
        invalidResult.addError("field", "Invalid value");
        when(mockValidator.validate(any())).thenReturn(invalidResult);
        
        // Start processing
        processor.startProcessing();
        Thread.sleep(1000);
        processor.shutdown();
        
        // Verify error handling
        verify(mockErrorHandler)
            .handleValidationError(eq(record), eq(invalidResult));
        
        ProcessingStats stats = processor.getStats();
        assertTrue(stats.getValidateErrors() > 0);
    }
    
    @Test
    void testHandleSinkError() throws Exception {
        // Prepare test data
        DataRecord record = createTestRecord();
        List<DataRecord> records = Collections.singletonList(record);
        
        // Configure mocks
        when(mockSource.fetchRecords()).thenReturn(records);
        when(mockTransformer.transform(any())).thenReturn(record);
        when(mockValidator.validate(any()))
            .thenReturn(new ValidationResult(ValidationLevel.NORMAL));
        doThrow(new SinkException("Test error"))
            .when(mockSink).write(any());
        
        // Start processing
        processor.startProcessing();
        Thread.sleep(1000);
        processor.shutdown();
        
        // Verify error handling
        verify(mockErrorHandler)
            .handleSinkError(eq(record), any(SinkException.class));
        
        ProcessingStats stats = processor.getStats();
        assertTrue(stats.getSinkErrors() > 0);
    }
    
    @Test
    void testCircuitBreakerTripping() throws Exception {
        // Configure mock to consistently fail
        when(mockSource.fetchRecords())
            .thenThrow(new SourceUnavailableException("Test error"));
        
        // Start processing
        processor.startProcessing();
        Thread.sleep(5000); // Allow time for circuit breaker to trip
        processor.shutdown();
        
        // Verify circuit breaker behavior
        ProcessingStats stats = processor.getStats();
        assertTrue(stats.getErrorCount() >= 5); // Circuit breaker threshold
        
        verify(mockErrorHandler, atLeast(5))
            .handleSourceError(any(SourceUnavailableException.class));
    }
    
    @Test
    void testBatchProcessing() throws Exception {
        // Prepare test data
        List<DataRecord> records = createTestRecords(10);
        
        // Configure mocks
        when(mockSource.fetchRecords()).thenReturn(records);
        when(mockTransformer.transform(any()))
            .thenAnswer(i -> i.getArgument(0));
        when(mockValidator.validate(any()))
            .thenReturn(new ValidationResult(ValidationLevel.NORMAL));
        
        // Start processing
        processor.startProcessing();
        Thread.sleep(2000);
        processor.shutdown();
        
        // Verify batch processing
        verify(mockSource, atLeastOnce()).fetchRecords();
        verify(mockTransformer, times(10)).transform(any());
        verify(mockValidator, times(10)).validate(any());
        verify(mockSink, times(10)).write(any());
        
        ProcessingStats stats = processor.getStats();
        assertEquals(10, stats.getSuccessCount());
        assertEquals(0, stats.getErrorCount());
    }
    
    private DataRecord createTestRecord() {
        Map<String, Object> data = new HashMap<>();
        data.put("id", UUID.randomUUID().toString());
        data.put("value", "test");
        data.put("timestamp", LocalDateTime.now());
        return new DataRecord(UUID.randomUUID().toString(), data);
    }
    
    private List<DataRecord> createTestRecords(int count) {
        return IntStream.range(0, count)
            .mapToObj(i -> createTestRecord())
            .collect(Collectors.toList());
    }
} 