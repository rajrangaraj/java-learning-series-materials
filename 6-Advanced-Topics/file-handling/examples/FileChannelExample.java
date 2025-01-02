/**
 * Demonstrates NIO Channel and Buffer operations
 */
public class FileChannelExample {
    
    public static void main(String[] args) throws IOException {
        Path file = Files.createTempFile("channel", ".dat");
        
        try {
            demonstrateBasicChannelOperations(file);
            demonstrateBufferOperations(file);
            demonstrateMemoryMappedFiles(file);
            demonstrateChannelTransfers(file);
        } finally {
            Files.deleteIfExists(file);
        }
    }
    
    private static void demonstrateBasicChannelOperations(Path file) throws IOException {
        System.out.println("\n=== Basic Channel Operations ===");
        
        // Write using channel
        try (FileChannel channel = FileChannel.open(file, 
                StandardOpenOption.WRITE, StandardOpenOption.CREATE)) {
            
            ByteBuffer buffer = ByteBuffer.allocate(1024);
            buffer.put("Hello, Channel!".getBytes());
            buffer.flip(); // Prepare for writing
            
            int bytesWritten = channel.write(buffer);
            System.out.println("Bytes written: " + bytesWritten);
        }
        
        // Read using channel
        try (FileChannel channel = FileChannel.open(file, StandardOpenOption.READ)) {
            ByteBuffer buffer = ByteBuffer.allocate(1024);
            int bytesRead = channel.read(buffer);
            
            buffer.flip(); // Prepare for reading
            byte[] bytes = new byte[bytesRead];
            buffer.get(bytes);
            
            System.out.println("Read: " + new String(bytes));
        }
    }
    
    private static void demonstrateBufferOperations(Path file) throws IOException {
        System.out.println("\n=== Buffer Operations ===");
        
        // Different types of buffers
        ByteBuffer heap = ByteBuffer.allocate(1024); // Heap buffer
        ByteBuffer direct = ByteBuffer.allocateDirect(1024); // Direct buffer
        
        // Write some data
        heap.putInt(42);
        heap.putDouble(3.14);
        heap.put("Buffer Test".getBytes());
        
        // Demonstrate buffer properties
        System.out.println("Position: " + heap.position());
        System.out.println("Limit: " + heap.limit());
        System.out.println("Capacity: " + heap.capacity());
        
        // Prepare for reading
        heap.flip();
        
        // Read data back
        System.out.println("Integer: " + heap.getInt());
        System.out.println("Double: " + heap.getDouble());
        byte[] stringBytes = new byte[11];
        heap.get(stringBytes);
        System.out.println("String: " + new String(stringBytes));
        
        // Demonstrate marking and resetting
        heap.clear(); // Reset to write mode
        heap.put("Mark/Reset Test".getBytes());
        heap.flip();
        
        // Read 5 bytes
        byte[] partial = new byte[5];
        heap.get(partial);
        System.out.println("First 5 bytes: " + new String(partial));
        
        heap.mark(); // Mark current position
        
        // Read more
        heap.get(partial);
        System.out.println("Next 5 bytes: " + new String(partial));
        
        heap.reset(); // Go back to mark
        heap.get(partial);
        System.out.println("After reset: " + new String(partial));
    }
    
    private static void demonstrateMemoryMappedFiles(Path file) throws IOException {
        System.out.println("\n=== Memory Mapped Files ===");
        
        // Create a 1MB mapped file
        try (FileChannel channel = FileChannel.open(file, 
                StandardOpenOption.READ, StandardOpenOption.WRITE)) {
            
            // Map the file into memory
            MappedByteBuffer mapped = channel.map(
                FileChannel.MapMode.READ_WRITE, 0, 1024 * 1024);
            
            // Write directly to memory
            mapped.putInt(42);
            mapped.putDouble(3.14);
            mapped.put("Memory Mapped".getBytes());
            
            // Force changes to disk
            mapped.force();
            
            // Read from memory
            mapped.position(0);
            System.out.println("Integer: " + mapped.getInt());
            System.out.println("Double: " + mapped.getDouble());
            byte[] stringBytes = new byte[13];
            mapped.get(stringBytes);
            System.out.println("String: " + new String(stringBytes));
        }
    }
    
    private static void demonstrateChannelTransfers(Path file) throws IOException {
        System.out.println("\n=== Channel Transfers ===");
        
        // Create source and destination files
        Path source = Files.createTempFile("source", ".dat");
        Path dest = Files.createTempFile("dest", ".dat");
        
        try {
            // Write some data to source file
            try (FileChannel sourceChannel = FileChannel.open(source, 
                    StandardOpenOption.WRITE)) {
                ByteBuffer buffer = ByteBuffer.wrap(
                    "Channel transfer test data".getBytes());
                sourceChannel.write(buffer);
            }
            
            // Transfer from source to destination
            try (FileChannel sourceChannel = FileChannel.open(source, 
                    StandardOpenOption.READ);
                 FileChannel destChannel = FileChannel.open(dest, 
                    StandardOpenOption.WRITE, StandardOpenOption.CREATE)) {
                
                long bytesTransferred = sourceChannel.transferTo(
                    0, sourceChannel.size(), destChannel);
                System.out.println("Bytes transferred: " + bytesTransferred);
            }
            
            // Verify transfer
            String content = new String(Files.readAllBytes(dest));
            System.out.println("Transferred content: " + content);
            
        } finally {
            Files.deleteIfExists(source);
            Files.deleteIfExists(dest);
        }
    }
} 