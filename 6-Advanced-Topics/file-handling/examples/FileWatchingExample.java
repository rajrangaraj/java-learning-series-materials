/**
 * Demonstrates file system watching capabilities
 */
public class FileWatchingExample {
    private final WatchService watchService;
    private final Map<WatchKey, Path> watchKeys;
    private volatile boolean running;
    
    public FileWatchingExample() throws IOException {
        this.watchService = FileSystems.getDefault().newWatchService();
        this.watchKeys = new HashMap<>();
        this.running = true;
    }
    
    public void watchDirectory(Path dir) throws IOException {
        // Register directory with watch service
        WatchKey key = dir.register(watchService,
            StandardWatchEventKinds.ENTRY_CREATE,
            StandardWatchEventKinds.ENTRY_DELETE,
            StandardWatchEventKinds.ENTRY_MODIFY);
        watchKeys.put(key, dir);
        
        // Also register all subdirectories
        Files.walkFileTree(dir, new SimpleFileVisitor<Path>() {
            @Override
            public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) 
                    throws IOException {
                WatchKey key = dir.register(watchService,
                    StandardWatchEventKinds.ENTRY_CREATE,
                    StandardWatchEventKinds.ENTRY_DELETE,
                    StandardWatchEventKinds.ENTRY_MODIFY);
                watchKeys.put(key, dir);
                return FileVisitResult.CONTINUE;
            }
        });
    }
    
    public void processEvents() {
        while (running) {
            WatchKey key;
            try {
                key = watchService.take();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
            
            Path dir = watchKeys.get(key);
            if (dir == null) {
                System.err.println("WatchKey not recognized!");
                continue;
            }
            
            for (WatchEvent<?> event : key.pollEvents()) {
                WatchEvent.Kind<?> kind = event.kind();
                
                if (kind == StandardWatchEventKinds.OVERFLOW) {
                    continue;
                }
                
                @SuppressWarnings("unchecked")
                WatchEvent<Path> ev = (WatchEvent<Path>) event;
                Path name = ev.context();
                Path child = dir.resolve(name);
                
                System.out.printf("%s: %s%n", kind.name(), child);
                
                // If directory is created, watch it too
                if (kind == StandardWatchEventKinds.ENTRY_CREATE) {
                    try {
                        if (Files.isDirectory(child)) {
                            watchDirectory(child);
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            
            if (!key.reset()) {
                watchKeys.remove(key);
                if (watchKeys.isEmpty()) {
                    break;
                }
            }
        }
    }
    
    public void stop() {
        running = false;
    }
    
    public static void main(String[] args) throws IOException {
        // Create a temporary directory for demonstration
        Path dir = Files.createTempDirectory("watch-example");
        
        FileWatchingExample watcher = new FileWatchingExample();
        watcher.watchDirectory(dir);
        
        // Start watching in a separate thread
        Thread watchThread = new Thread(() -> watcher.processEvents());
        watchThread.start();
        
        try {
            // Demonstrate file events
            System.out.println("Watching directory: " + dir);
            
            // Create a file
            Path file1 = dir.resolve("test1.txt");
            Files.write(file1, "Hello".getBytes());
            Thread.sleep(1000);
            
            // Modify the file
            Files.write(file1, "Hello World".getBytes());
            Thread.sleep(1000);
            
            // Create a directory
            Path subDir = dir.resolve("subdir");
            Files.createDirectory(subDir);
            Thread.sleep(1000);
            
            // Create a file in subdirectory
            Path file2 = subDir.resolve("test2.txt");
            Files.write(file2, "In subdirectory".getBytes());
            Thread.sleep(1000);
            
            // Delete files
            Files.delete(file2);
            Files.delete(subDir);
            Files.delete(file1);
            
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // Cleanup
            watcher.stop();
            watchThread.interrupt();
            Files.walk(dir)
                 .sorted(Comparator.reverseOrder())
                 .forEach(path -> {
                     try {
                         Files.delete(path);
                     } catch (IOException e) {
                         e.printStackTrace();
                     }
                 });
        }
    }
} 