/**
 * Demonstrates Java object serialization
 */
public class SerializationExample {
    
    public static void main(String[] args) throws IOException, ClassNotFoundException {
        // Create temporary file for serialization
        Path tempFile = Files.createTempFile("serial", ".dat");
        
        try {
            demonstrateBasicSerialization(tempFile);
            demonstrateCustomSerialization(tempFile);
            demonstrateExternalization(tempFile);
        } finally {
            Files.deleteIfExists(tempFile);
        }
    }
    
    private static void demonstrateBasicSerialization(Path file) 
            throws IOException, ClassNotFoundException {
        System.out.println("\n=== Basic Serialization ===");
        
        // Create and serialize object
        Person person = new Person("John Doe", 30);
        person.addAddress(new Address("123 Main St", "Anytown", "12345"));
        
        try (ObjectOutputStream out = new ObjectOutputStream(
                new BufferedOutputStream(Files.newOutputStream(file)))) {
            out.writeObject(person);
        }
        
        // Deserialize object
        try (ObjectInputStream in = new ObjectInputStream(
                new BufferedInputStream(Files.newInputStream(file)))) {
            Person restored = (Person) in.readObject();
            System.out.println("Restored person: " + restored);
        }
    }
    
    private static void demonstrateCustomSerialization(Path file) 
            throws IOException, ClassNotFoundException {
        System.out.println("\n=== Custom Serialization ===");
        
        // Create and serialize object
        CustomPerson person = new CustomPerson("Jane Doe", 25);
        person.setPassword("secret123");
        
        try (ObjectOutputStream out = new ObjectOutputStream(
                new BufferedOutputStream(Files.newOutputStream(file)))) {
            out.writeObject(person);
        }
        
        // Deserialize object
        try (ObjectInputStream in = new ObjectInputStream(
                new BufferedInputStream(Files.newInputStream(file)))) {
            CustomPerson restored = (CustomPerson) in.readObject();
            System.out.println("Restored custom person: " + restored);
        }
    }
    
    private static void demonstrateExternalization(Path file) 
            throws IOException, ClassNotFoundException {
        System.out.println("\n=== Externalization ===");
        
        // Create and serialize object
        ExternalPerson person = new ExternalPerson("Bob Smith", 40);
        person.setInternalId(12345);
        
        try (ObjectOutputStream out = new ObjectOutputStream(
                new BufferedOutputStream(Files.newOutputStream(file)))) {
            out.writeObject(person);
        }
        
        // Deserialize object
        try (ObjectInputStream in = new ObjectInputStream(
                new BufferedInputStream(Files.newInputStream(file)))) {
            ExternalPerson restored = (ExternalPerson) in.readObject();
            System.out.println("Restored external person: " + restored);
        }
    }
} 