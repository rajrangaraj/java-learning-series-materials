/**
 * Exercise: Implement a robust file processing system that handles various error scenarios
 */
public class FileProcessor {
    private final Path inputDir;
    private final Path outputDir;
    private final Path errorDir;
    private final ProcessingConfig config;
    private final ProcessingStats stats;
    private final Logger logger;
    
    public FileProcessor(Path inputDir, Path outputDir, Path errorDir, ProcessingConfig config) {
        this.inputDir = inputDir;
        this.outputDir = outputDir;
        this.errorDir = errorDir;
        this.config = config;
        this.stats = new ProcessingStats();
        this.logger = LoggerFactory.getLogger(FileProcessor.class);
    }
    
    /**
     * Process all files in the input directory
     */
    public void processFiles() throws ProcessingException {
        try {
            // Create necessary directories
            Files.createDirectories(outputDir);
            Files.createDirectories(errorDir);
            
            // Process each file in the input directory
            try (Stream<Path> files = Files.walk(inputDir)) {
                files.filter(Files::isRegularFile)
                     .forEach(this::processFile);
            }
            
            // Check if minimum required files were processed
            if (stats.getProcessedFiles() < config.getMinimumFiles()) {
                throw new ProcessingException("Insufficient files processed",
                    "INSUFFICIENT_FILES",
                    Map.of("required", String.valueOf(config.getMinimumFiles()),
                          "processed", String.valueOf(stats.getProcessedFiles())));
            }
            
        } catch (IOException e) {
            throw new ProcessingException("Failed to access directory", "IO_ERROR", e);
        }
    }
    
    private void processFile(Path file) {
        String filename = file.getFileName().toString();
        Path outputFile = outputDir.resolve(filename);
        Path errorFile = errorDir.resolve(filename + ".error");
        
        try {
            // Validate file
            validateFile(file);
            
            // Process the file based on its type
            if (filename.endsWith(".txt")) {
                processTextFile(file, outputFile);
            } else if (filename.endsWith(".csv")) {
                processCsvFile(file, outputFile);
            } else if (filename.endsWith(".json")) {
                processJsonFile(file, outputFile);
            } else {
                throw new UnsupportedFileException("Unsupported file type: " + 
                    getFileExtension(filename));
            }
            
            stats.recordSuccess();
            logger.info("Successfully processed file: {}", filename);
            
        } catch (ValidationException e) {
            handleProcessingError(file, errorFile, e);
            stats.recordValidationError();
        } catch (ProcessingException e) {
            handleProcessingError(file, errorFile, e);
            stats.recordProcessingError();
        } catch (Exception e) {
            handleProcessingError(file, errorFile, e);
            stats.recordUnexpectedError();
        }
    }
    
    private void validateFile(Path file) throws ValidationException {
        try {
            // Check file size
            long size = Files.size(file);
            if (size > config.getMaxFileSize()) {
                throw new ValidationException("File too large",
                    Map.of("maxSize", String.valueOf(config.getMaxFileSize()),
                          "actualSize", String.valueOf(size)));
            }
            
            // Check file age
            FileTime creationTime = Files.getAttribute(file, "creationTime", 
                LinkOption.NOFOLLOW_LINKS);
            if (isFileExpired(creationTime)) {
                throw new ValidationException("File too old",
                    Map.of("maxAge", String.valueOf(config.getMaxFileAge()),
                          "fileAge", String.valueOf(getFileAge(creationTime))));
            }
            
        } catch (IOException e) {
            throw new ValidationException("Failed to validate file", e);
        }
    }
    
    private void processTextFile(Path input, Path output) throws ProcessingException {
        try (BufferedReader reader = Files.newBufferedReader(input);
             BufferedWriter writer = Files.newBufferedWriter(output)) {
            
            String line;
            int lineCount = 0;
            while ((line = reader.readLine()) != null) {
                // Apply text transformations
                line = applyTextTransformations(line);
                
                // Write transformed line
                writer.write(line);
                writer.newLine();
                lineCount++;
            }
            
            if (lineCount == 0) {
                throw new ProcessingException("Empty file", "EMPTY_FILE");
            }
            
        } catch (IOException e) {
            throw new ProcessingException("Failed to process text file", "IO_ERROR", e);
        }
    }
    
    private void processCsvFile(Path input, Path output) throws ProcessingException {
        try (CSVReader reader = new CSVReader(Files.newBufferedReader(input));
             CSVWriter writer = new CSVWriter(Files.newBufferedWriter(output))) {
            
            // Read header
            String[] header = reader.readNext();
            if (header == null) {
                throw new ProcessingException("Missing CSV header", "INVALID_CSV");
            }
            
            // Validate header
            validateCsvHeader(header);
            
            // Write header
            writer.writeNext(header);
            
            // Process rows
            String[] row;
            int rowCount = 0;
            while ((row = reader.readNext()) != null) {
                // Validate and transform row
                row = processCsvRow(row, header);
                writer.writeNext(row);
                rowCount++;
            }
            
            if (rowCount == 0) {
                throw new ProcessingException("Empty CSV file", "EMPTY_CSV");
            }
            
        } catch (IOException e) {
            throw new ProcessingException("Failed to process CSV file", "IO_ERROR", e);
        } catch (CsvValidationException e) {
            throw new ProcessingException("Invalid CSV format", "INVALID_CSV", e);
        }
    }
    
    private void processJsonFile(Path input, Path output) throws ProcessingException {
        try {
            // Read JSON
            JsonNode root = new ObjectMapper().readTree(input.toFile());
            
            // Validate JSON structure
            validateJsonStructure(root);
            
            // Transform JSON
            JsonNode transformed = transformJson(root);
            
            // Write transformed JSON
            new ObjectMapper().writerWithDefaultPrettyPrinter()
                            .writeValue(output.toFile(), transformed);
            
        } catch (IOException e) {
            throw new ProcessingException("Failed to process JSON file", "IO_ERROR", e);
        }
    }
    
    private void handleProcessingError(Path input, Path errorFile, Exception e) {
        try {
            // Create error report
            Map<String, String> errorInfo = new HashMap<>();
            errorInfo.put("timestamp", LocalDateTime.now().toString());
            errorInfo.put("file", input.getFileName().toString());
            errorInfo.put("error", e.getMessage());
            if (e instanceof ProcessingException) {
                errorInfo.put("errorCode", ((ProcessingException) e).getErrorCode());
            }
            if (e.getCause() != null) {
                errorInfo.put("cause", e.getCause().getMessage());
            }
            
            // Write error report
            new ObjectMapper().writerWithDefaultPrettyPrinter()
                            .writeValue(errorFile.toFile(), errorInfo);
            
            logger.error("Failed to process file: {}", input.getFileName(), e);
            
        } catch (IOException ex) {
            logger.error("Failed to write error report for file: {}", 
                input.getFileName(), ex);
        }
    }
    
    // Helper methods
    private String getFileExtension(String filename) {
        int dot = filename.lastIndexOf('.');
        return dot > 0 ? filename.substring(dot + 1) : "";
    }
    
    private boolean isFileExpired(FileTime creationTime) {
        return getFileAge(creationTime) > config.getMaxFileAge();
    }
    
    private long getFileAge(FileTime creationTime) {
        return Duration.between(creationTime.toInstant(), 
            Instant.now()).toDays();
    }
    
    private String applyTextTransformations(String line) {
        // Apply configured transformations
        if (config.isUpperCase()) {
            line = line.toUpperCase();
        }
        if (config.getTrimLength() > 0 && line.length() > config.getTrimLength()) {
            line = line.substring(0, config.getTrimLength());
        }
        return line;
    }
    
    private void validateCsvHeader(String[] header) throws ProcessingException {
        // Check required columns
        List<String> missing = config.getRequiredColumns().stream()
            .filter(col -> !Arrays.asList(header).contains(col))
            .collect(Collectors.toList());
            
        if (!missing.isEmpty()) {
            throw new ProcessingException("Missing required columns: " + missing,
                "INVALID_CSV_HEADER");
        }
    }
    
    private String[] processCsvRow(String[] row, String[] header) 
            throws ProcessingException {
        // Validate row length
        if (row.length != header.length) {
            throw new ProcessingException(
                "Row length does not match header length",
                "INVALID_CSV_ROW",
                Map.of("expected", String.valueOf(header.length),
                      "actual", String.valueOf(row.length)));
        }
        
        // Apply transformations
        return Arrays.stream(row)
            .map(this::applyTextTransformations)
            .toArray(String[]::new);
    }
    
    private void validateJsonStructure(JsonNode root) throws ProcessingException {
        // Check if root is an object or array as required
        if (config.isRequireJsonObject() && !root.isObject()) {
            throw new ProcessingException("Root must be a JSON object", "INVALID_JSON");
        }
        if (config.isRequireJsonArray() && !root.isArray()) {
            throw new ProcessingException("Root must be a JSON array", "INVALID_JSON");
        }
        
        // Check required fields
        for (String field : config.getRequiredJsonFields()) {
            if (root.isObject() && !root.has(field)) {
                throw new ProcessingException("Missing required field: " + field,
                    "INVALID_JSON");
            }
        }
    }
    
    private JsonNode transformJson(JsonNode root) {
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode transformed = mapper.createObjectNode();
        
        // Add processing metadata
        transformed.put("processedAt", LocalDateTime.now().toString());
        transformed.put("originalSize", root.size());
        
        // Add transformed content
        transformed.set("content", root);
        
        return transformed;
    }
    
    public ProcessingStats getStats() {
        return stats;
    }
} 