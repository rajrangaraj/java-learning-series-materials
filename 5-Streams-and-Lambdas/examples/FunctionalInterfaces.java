/**
 * Examples demonstrating built-in and custom functional interfaces
 */
public class FunctionalInterfaces {
    
    public static void main(String[] args) {
        // Built-in functional interfaces
        demonstrateBuiltInInterfaces();
        
        // Custom functional interfaces
        demonstrateCustomInterfaces();
        
        // Function composition
        demonstrateFunctionComposition();
        
        // Exception handling
        demonstrateExceptionHandling();
    }
    
    private static void demonstrateBuiltInInterfaces() {
        System.out.println("Built-in Functional Interfaces:");
        System.out.println("============================");
        
        // Function<T, R>
        Function<String, Integer> strLength = String::length;
        System.out.println("String length: " + strLength.apply("hello"));
        
        // BiFunction<T, U, R>
        BiFunction<String, String, String> concat = String::concat;
        System.out.println("Concatenated: " + concat.apply("hello ", "world"));
        
        // Predicate<T>
        Predicate<String> isEmpty = String::isEmpty;
        System.out.println("Is empty: " + isEmpty.test(""));
        
        // BiPredicate<T, U>
        BiPredicate<String, String> startsWith = String::startsWith;
        System.out.println("Starts with: " + startsWith.test("hello", "he"));
        
        // Consumer<T>
        Consumer<String> printer = System.out::println;
        printer.accept("Consuming a string");
        
        // BiConsumer<T, U>
        BiConsumer<String, Integer> repeater = (str, times) -> {
            for (int i = 0; i < times; i++) {
                System.out.println(str);
            }
        };
        repeater.accept("Repeat", 3);
        
        // Supplier<T>
        Supplier<LocalDateTime> now = LocalDateTime::now;
        System.out.println("Current time: " + now.get());
        
        // UnaryOperator<T>
        UnaryOperator<String> upper = String::toUpperCase;
        System.out.println("Uppercase: " + upper.apply("hello"));
        
        // BinaryOperator<T>
        BinaryOperator<Integer> max = Integer::max;
        System.out.println("Maximum: " + max.apply(10, 20));
    }
    
    private static void demonstrateCustomInterfaces() {
        System.out.println("\nCustom Functional Interfaces:");
        System.out.println("==========================");
        
        // Custom functional interface with single abstract method
        Validator<String> lengthValidator = s -> s != null && s.length() >= 5;
        System.out.println("Valid length: " + lengthValidator.isValid("hello"));
        
        // Custom interface with type parameters
        Transformer<String, Integer> parseHex = s -> Integer.parseInt(s, 16);
        System.out.println("Hex to int: " + parseHex.transform("1A"));
        
        // Custom interface with multiple parameters
        TriFunction<Integer, Integer, Integer, Double> average = 
            (a, b, c) -> (a + b + c) / 3.0;
        System.out.println("Average: " + average.apply(1, 2, 3));
        
        // Custom interface with default method
        Calculator calculator = (a, b) -> a + b;
        System.out.println("Sum: " + calculator.calculate(5, 3));
        System.out.println("Double sum: " + calculator.calculateDouble(5, 3));
    }
    
    private static void demonstrateFunctionComposition() {
        System.out.println("\nFunction Composition:");
        System.out.println("====================");
        
        // Composing Functions
        Function<String, String> trim = String::trim;
        Function<String, String> upper = String::toUpperCase;
        Function<String, Integer> length = String::length;
        
        Function<String, Integer> trimUpperLength = 
            trim.andThen(upper).andThen(length);
        
        System.out.println("Composed result: " + 
            trimUpperLength.apply("  hello  "));
        
        // Composing Predicates
        Predicate<String> nonNull = s -> s != null;
        Predicate<String> nonEmpty = s -> !s.isEmpty();
        Predicate<String> validInput = nonNull.and(nonEmpty);
        
        System.out.println("Valid input: " + validInput.test("hello"));
        System.out.println("Valid input: " + validInput.test(""));
    }
    
    private static void demonstrateExceptionHandling() {
        System.out.println("\nException Handling:");
        System.out.println("==================");
        
        // Wrapper for handling checked exceptions
        ThrowingFunction<String, Integer> parser = Integer::parseInt;
        Function<String, Optional<Integer>> safeParser = 
            wrapFunction(parser);
        
        System.out.println("Parsed: " + safeParser.apply("123"));
        System.out.println("Failed parse: " + safeParser.apply("abc"));
    }
    
    // Custom functional interfaces
    @FunctionalInterface
    interface Validator<T> {
        boolean isValid(T t);
    }
    
    @FunctionalInterface
    interface Transformer<T, R> {
        R transform(T t);
    }
    
    @FunctionalInterface
    interface TriFunction<T, U, V, R> {
        R apply(T t, U u, V v);
    }
    
    @FunctionalInterface
    interface Calculator {
        int calculate(int a, int b);
        
        // Default method
        default int calculateDouble(int a, int b) {
            return calculate(a, b) * 2;
        }
    }
    
    @FunctionalInterface
    interface ThrowingFunction<T, R> {
        R apply(T t) throws Exception;
    }
    
    // Utility method for exception handling
    private static <T, R> Function<T, Optional<R>> wrapFunction(
            ThrowingFunction<T, R> function) {
        return t -> {
            try {
                return Optional.of(function.apply(t));
            } catch (Exception e) {
                return Optional.empty();
            }
        };
    }
} 