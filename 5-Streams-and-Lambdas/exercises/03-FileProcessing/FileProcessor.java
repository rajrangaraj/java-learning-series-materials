/**
 * Processes files using streams and functional operations
 */
public class FileProcessor {
    private final Path basePath;
    private final FileProcessingStats stats;
    
    public FileProcessor(String basePath) {
        this.basePath = Paths.get(basePath);
        this.stats = new FileProcessingStats();
    }
    
    /**
     * Finds files matching given criteria
     */
    public List<Path> findFiles(String extension, long minSize, long maxSize) 
            throws IOException {
        return Files.walk(basePath)
                   .filter(Files::isRegularFile)
                   .filter(p -> p.toString().endsWith(extension))
                   .filter(p -> {
                       try {
                           long size = Files.size(p);
                           return size >= minSize && size <= maxSize;
                       } catch (IOException e) {
                           return false;
                       }
                   })
                   .collect(Collectors.toList());
    }
    
    /**
     * Searches for text in files
     */
    public Map<Path, List<String>> searchInFiles(String searchText, String extension) 
            throws IOException {
        return Files.walk(basePath)
                   .filter(Files::isRegularFile)
                   .filter(p -> p.toString().endsWith(extension))
                   .collect(Collectors.toMap(
                       Function.identity(),
                       p -> findMatchingLines(p, searchText)
                   ))
                   .entrySet().stream()
                   .filter(e -> !e.getValue().isEmpty())
                   .collect(Collectors.toMap(
                       Map.Entry::getKey,
                       Map.Entry::getValue
                   ));
    }
    
    /**
     * Processes text files and generates word frequency statistics
     */
    public Map<String, WordStats> analyzeWordFrequency(List<Path> files) 
            throws IOException {
        return files.stream()
                   .filter(Files::isRegularFile)
                   .flatMap(this::readWords)
                   .collect(Collectors.groupingBy(
                       String::toLowerCase,
                       Collectors.collectingAndThen(
                           Collectors.toList(),
                           this::calculateWordStats
                       )
                   ));
    }
    
    /**
     * Processes files in parallel and applies transformations
     */
    public void processFiles(List<Path> files, 
                           Function<String, String> lineTransformation,
                           Path outputDir) throws IOException {
        Files.createDirectories(outputDir);
        
        files.parallelStream().forEach(file -> {
            try {
                Path relativePath = basePath.relativize(file);
                Path outputPath = outputDir.resolve(relativePath);
                Files.createDirectories(outputPath.getParent());
                
                try (BufferedReader reader = Files.newBufferedReader(file);
                     BufferedWriter writer = Files.newBufferedWriter(outputPath)) {
                    reader.lines()
                          .map(lineTransformation)
                          .forEach(line -> {
                              try {
                                  writer.write(line);
                                  writer.newLine();
                              } catch (IOException e) {
                                  throw new UncheckedIOException(e);
                              }
                          });
                    stats.recordProcessedFile();
                }
            } catch (IOException e) {
                stats.recordFailedFile();
                throw new UncheckedIOException(e);
            }
        });
    }
    
    /**
     * Monitors directory for changes and processes new files
     */
    public void watchAndProcess(String extension, 
                              Consumer<Path> fileProcessor) throws IOException {
        try (WatchService watchService = FileSystems.getDefault().newWatchService()) {
            basePath.register(watchService, 
                StandardWatchEventKinds.ENTRY_CREATE,
                StandardWatchEventKinds.ENTRY_MODIFY);
            
            while (true) {
                WatchKey key = watchService.take();
                
                key.pollEvents().stream()
                   .filter(event -> {
                       Path path = (Path) event.context();
                       return path.toString().endsWith(extension);
                   })
                   .forEach(event -> {
                       Path path = basePath.resolve((Path) event.context());
                       try {
                           fileProcessor.accept(path);
                           stats.recordWatchedFile();
                       } catch (Exception e) {
                           stats.recordFailedFile();
                       }
                   });
                
                if (!key.reset()) {
                    break;
                }
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
    
    /**
     * Gets processing statistics
     */
    public FileProcessingStats getStats() {
        return stats;
    }
    
    // Helper methods
    private List<String> findMatchingLines(Path file, String searchText) {
        try (BufferedReader reader = Files.newBufferedReader(file)) {
            return reader.lines()
                        .filter(line -> line.contains(searchText))
                        .collect(Collectors.toList());
        } catch (IOException e) {
            return Collections.emptyList();
        }
    }
    
    private Stream<String> readWords(Path file) {
        try {
            return Files.lines(file)
                       .flatMap(line -> Arrays.stream(line.split("\\W+")))
                       .filter(word -> !word.isEmpty());
        } catch (IOException e) {
            return Stream.empty();
        }
    }
    
    private WordStats calculateWordStats(List<String> occurrences) {
        return new WordStats(
            occurrences.size(),
            occurrences.stream()
                      .collect(Collectors.groupingBy(
                          String::length,
                          Collectors.counting()
                      ))
        );
    }
} 