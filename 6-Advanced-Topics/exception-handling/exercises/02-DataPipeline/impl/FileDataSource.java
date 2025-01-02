/**
 * Implementation of DataSource that reads from files
 */
public class FileDataSource implements DataSource {
    private static final Logger logger = LoggerFactory.getLogger(FileDataSource.class);
    private final Path directory;
    private final String filePattern;
    private final Queue<Path> files;
    private BufferedReader currentReader;
    private Path currentFile;
    private final ObjectMapper objectMapper;
    private int batchSize;
    
    public FileDataSource(Path directory, String filePattern, int batchSize) {
        this.directory = directory;
        this.filePattern = filePattern;
        this.batchSize = batchSize;
        this.files = new LinkedList<>();
        this.objectMapper = new ObjectMapper();
        initialize();
    }
    
    private void initialize() {
        try {
            PathMatcher matcher = FileSystems.getDefault()
                .getPathMatcher("glob:" + filePattern);
                
            Files.list(directory)
                .filter(path -> matcher.matches(path.getFileName()))
                .sorted()
                .forEach(files::offer);
                
        } catch (IOException e) {
            throw new SourceUnavailableException(
                "Failed to initialize file source", e);
        }
    }
    
    @Override
    public List<DataRecord> fetchRecords() throws SourceUnavailableException {
        List<DataRecord> records = new ArrayList<>();
        
        try {
            while (records.size() < batchSize && hasMore()) {
                String line = readLine();
                if (line != null) {
                    DataRecord record = parseRecord(line);
                    records.add(record);
                }
            }
        } catch (IOException e) {
            throw new SourceUnavailableException(
                "Error reading from file: " + currentFile, e);
        }
        
        return records;
    }
    
    private String readLine() throws IOException {
        if (currentReader == null || !hasMore()) {
            return null;
        }
        
        String line = currentReader.readLine();
        if (line == null) {
            currentReader.close();
            currentReader = null;
            currentFile = null;
            return readLine();
        }
        
        return line;
    }
    
    private DataRecord parseRecord(String line) throws IOException {
        try {
            Map<String, Object> data = objectMapper.readValue(
                line, new TypeReference<Map<String, Object>>() {});
            return new DataRecord(UUID.randomUUID().toString(), data);
        } catch (JsonProcessingException e) {
            logger.warn("Failed to parse record: {}", line);
            throw new SourceUnavailableException(
                "Failed to parse record", e);
        }
    }
    
    @Override
    public boolean hasMore() {
        if (currentReader != null) {
            return true;
        }
        
        if (files.isEmpty()) {
            return false;
        }
        
        try {
            currentFile = files.poll();
            currentReader = Files.newBufferedReader(currentFile);
            return true;
        } catch (IOException e) {
            logger.error("Failed to open file: {}", currentFile, e);
            return hasMore();
        }
    }
    
    @Override
    public void reset() throws SourceUnavailableException {
        try {
            if (currentReader != null) {
                currentReader.close();
            }
            initialize();
        } catch (IOException e) {
            throw new SourceUnavailableException(
                "Failed to reset source", e);
        }
    }
    
    @Override
    public SourceMetadata getMetadata() {
        return new SourceMetadata(
            "FILE",
            directory.toString(),
            filePattern,
            files.size(),
            currentFile != null ? 
                currentFile.getFileName().toString() : null
        );
    }
    
    @Override
    public void close() throws Exception {
        if (currentReader != null) {
            currentReader.close();
        }
    }
} 