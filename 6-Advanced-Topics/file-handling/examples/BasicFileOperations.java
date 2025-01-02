/**
 * Demonstrates basic file and directory operations using NIO.2
 */
public class BasicFileOperations {
    
    public static void main(String[] args) throws IOException {
        // Create a temporary directory for our examples
        Path baseDir = Files.createTempDirectory("file-examples");
        try {
            demonstratePathOperations(baseDir);
            demonstrateFileOperations(baseDir);
            demonstrateDirectoryOperations(baseDir);
            demonstrateFileAttributes(baseDir);
        } finally {
            // Cleanup
            deleteDirectory(baseDir);
        }
    }
    
    private static void demonstratePathOperations(Path baseDir) {
        System.out.println("\n=== Path Operations ===");
        
        // Path manipulation
        Path file = baseDir.resolve("test.txt");
        System.out.println("Absolute path: " + file.toAbsolutePath());
        System.out.println("Parent path: " + file.getParent());
        System.out.println("File name: " + file.getFileName());
        
        // Path normalization
        Path complex = baseDir.resolve("dir/../other/./file.txt");
        System.out.println("Original path: " + complex);
        System.out.println("Normalized path: " + complex.normalize());
        
        // Relative paths
        Path other = baseDir.resolve("other/file.txt");
        System.out.println("Relative path: " + baseDir.relativize(other));
    }
    
    private static void demonstrateFileOperations(Path baseDir) throws IOException {
        System.out.println("\n=== File Operations ===");
        
        // Create and write to file
        Path file = baseDir.resolve("test.txt");
        List<String> lines = Arrays.asList("Line 1", "Line 2", "Line 3");
        Files.write(file, lines, StandardCharsets.UTF_8);
        System.out.println("File created: " + file);
        
        // Read from file
        System.out.println("File contents:");
        Files.lines(file).forEach(System.out::println);
        
        // Copy file
        Path copy = baseDir.resolve("test-copy.txt");
        Files.copy(file, copy, StandardCopyOption.REPLACE_EXISTING);
        System.out.println("File copied to: " + copy);
        
        // Move file
        Path moved = baseDir.resolve("test-moved.txt");
        Files.move(copy, moved, StandardCopyOption.REPLACE_EXISTING);
        System.out.println("File moved to: " + moved);
        
        // Delete files
        Files.delete(file);
        Files.deleteIfExists(moved);
        System.out.println("Files deleted");
    }
    
    private static void demonstrateDirectoryOperations(Path baseDir) throws IOException {
        System.out.println("\n=== Directory Operations ===");
        
        // Create directory structure
        Path dir = baseDir.resolve("parent/child/grandchild");
        Files.createDirectories(dir);
        System.out.println("Directories created: " + dir);
        
        // Create some files
        Files.write(dir.resolve("file1.txt"), "Content 1".getBytes());
        Files.write(dir.resolve("file2.txt"), "Content 2".getBytes());
        
        // List directory contents
        System.out.println("\nDirectory listing:");
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(dir)) {
            for (Path entry : stream) {
                System.out.println(entry.getFileName());
            }
        }
        
        // Walk directory tree
        System.out.println("\nDirectory tree:");
        Files.walk(baseDir.resolve("parent"))
             .forEach(p -> System.out.println(baseDir.relativize(p)));
        
        // Find files
        System.out.println("\nFound .txt files:");
        Files.find(baseDir, Integer.MAX_VALUE,
                  (p, attrs) -> p.toString().endsWith(".txt"))
             .forEach(p -> System.out.println(baseDir.relativize(p)));
    }
    
    private static void demonstrateFileAttributes(Path baseDir) throws IOException {
        System.out.println("\n=== File Attributes ===");
        
        Path file = baseDir.resolve("attributes.txt");
        Files.write(file, "Test content".getBytes());
        
        // Basic attributes
        BasicFileAttributes basic = Files.readAttributes(file, BasicFileAttributes.class);
        System.out.println("Creation time: " + basic.creationTime());
        System.out.println("Last modified: " + basic.lastModifiedTime());
        System.out.println("Size: " + basic.size());
        System.out.println("Is directory: " + basic.isDirectory());
        System.out.println("Is regular file: " + basic.isRegularFile());
        
        // Update last modified time
        FileTime newTime = FileTime.from(Instant.now());
        Files.setLastModifiedTime(file, newTime);
        
        // File permissions (POSIX)
        try {
            Set<PosixFilePermission> perms = Files.getPosixFilePermissions(file);
            System.out.println("POSIX permissions: " + PosixFilePermissions.toString(perms));
        } catch (UnsupportedOperationException e) {
            System.out.println("POSIX attributes not supported on this system");
        }
        
        // File owner (if supported)
        try {
            UserPrincipal owner = Files.getOwner(file);
            System.out.println("File owner: " + owner.getName());
        } catch (UnsupportedOperationException e) {
            System.out.println("File owner operations not supported on this system");
        }
    }
    
    private static void deleteDirectory(Path dir) throws IOException {
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