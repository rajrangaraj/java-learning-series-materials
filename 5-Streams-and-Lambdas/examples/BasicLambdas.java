/**
 * Examples demonstrating lambda expression syntax and usage
 */
public class BasicLambdas {
    
    public static void main(String[] args) {
        // Basic lambda syntax examples
        demonstrateBasicSyntax();
        
        // Variable capture examples
        demonstrateVariableCapture();
        
        // Method reference examples
        demonstrateMethodReferences();
        
        // Common functional interface examples
        demonstrateFunctionalInterfaces();
    }
    
    private static void demonstrateBasicSyntax() {
        System.out.println("Basic Lambda Syntax Examples:");
        System.out.println("===========================");
        
        // No parameters
        Runnable noParams = () -> System.out.println("Lambda with no parameters");
        
        // Single parameter
        Consumer<String> singleParam = s -> System.out.println("Got: " + s);
        
        // Multiple parameters
        BiFunction<Integer, Integer, Integer> add = (a, b) -> a + b;
        
        // Block with multiple statements
        Consumer<String> block = s -> {
            String result = s.toUpperCase();
            System.out.println("Transformed: " + result);
        };
        
        // Execute the lambdas
        noParams.run();
        singleParam.accept("hello");
        System.out.println("Sum: " + add.apply(5, 3));
        block.accept("test");
    }
    
    private static void demonstrateVariableCapture() {
        System.out.println("\nVariable Capture Examples:");
        System.out.println("========================");
        
        String prefix = "Message: ";  // Effectively final
        
        // Capturing local variable
        Consumer<String> printer = msg -> System.out.println(prefix + msg);
        
        printer.accept("Hello");
        
        int[] counter = {0};  // Mutable object for counting
        
        // Capturing and modifying mutable object
        Runnable increment = () -> counter[0]++;
        
        increment.run();
        increment.run();
        System.out.println("Counter: " + counter[0]);
    }
    
    private static void demonstrateMethodReferences() {
        System.out.println("\nMethod Reference Examples:");
        System.out.println("========================");
        
        // Static method reference
        Function<String, Integer> parse = Integer::parseInt;
        
        // Instance method reference on specific object
        String prefix = "Hello, ";
        Function<String, String> addPrefix = prefix::concat;
        
        // Instance method reference on arbitrary object
        Function<String, String> toUpper = String::toUpperCase;
        
        // Constructor reference
        Supplier<ArrayList<String>> listFactory = ArrayList::new;
        
        // Execute the method references
        System.out.println("Parsed: " + parse.apply("123"));
        System.out.println("Prefixed: " + addPrefix.apply("world"));
        System.out.println("Uppercase: " + toUpper.apply("test"));
        System.out.println("New list: " + listFactory.get());
    }
    
    private static void demonstrateFunctionalInterfaces() {
        System.out.println("\nFunctional Interface Examples:");
        System.out.println("============================");
        
        // Predicate
        Predicate<Integer> isEven = n -> n % 2 == 0;
        
        // Function
        Function<Integer, String> numberToString = n -> "Number: " + n;
        
        // Consumer
        Consumer<String> printer = s -> System.out.println("Consuming: " + s);
        
        // Supplier
        Supplier<LocalDateTime> now = () -> LocalDateTime.now();
        
        // BiFunction
        BiFunction<String, String, String> concat = (a, b) -> a + b;
        
        // Execute the functional interfaces
        System.out.println("Is 4 even? " + isEven.test(4));
        System.out.println(numberToString.apply(42));
        printer.accept("test message");
        System.out.println("Current time: " + now.get());
        System.out.println("Concatenated: " + concat.apply("Hello, ", "world!"));
    }
} 