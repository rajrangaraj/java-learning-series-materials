/**
 * Examples demonstrating generic class implementations
 */
public class GenericClassExample {
    
    /**
     * Generic Pair class
     */
    public static class Pair<T, U> {
        private T first;
        private U second;
        
        public Pair(T first, U second) {
            this.first = first;
            this.second = second;
        }
        
        public T getFirst() { return first; }
        public U getSecond() { return second; }
        public void setFirst(T first) { this.first = first; }
        public void setSecond(U second) { this.second = second; }
        
        @Override
        public String toString() {
            return String.format("(%s, %s)", first, second);
        }
    }
    
    /**
     * Generic bounded type example
     */
    public static class NumberContainer<T extends Number> {
        private T value;
        
        public NumberContainer(T value) {
            this.value = value;
        }
        
        public double getValue() {
            return value.doubleValue();
        }
        
        public boolean isInteger() {
            return value instanceof Integer;
        }
    }
    
    /**
     * Generic method examples
     */
    public static <T> void swapElements(T[] array, int i, int j) {
        T temp = array[i];
        array[i] = array[j];
        array[j] = temp;
    }
    
    public static <T extends Comparable<T>> T findMax(T[] array) {
        if (array == null || array.length == 0) {
            throw new IllegalArgumentException("Array cannot be empty");
        }
        
        T max = array[0];
        for (int i = 1; i < array.length; i++) {
            if (array[i].compareTo(max) > 0) {
                max = array[i];
            }
        }
        return max;
    }
    
    /**
     * Wildcard examples
     */
    public static void printList(List<?> list) {
        for (Object elem : list) {
            System.out.print(elem + " ");
        }
        System.out.println();
    }
    
    public static double sumOfList(List<? extends Number> list) {
        double sum = 0.0;
        for (Number num : list) {
            sum += num.doubleValue();
        }
        return sum;
    }
    
    public static void main(String[] args) {
        // Pair example
        Pair<String, Integer> pair = new Pair<>("Age", 25);
        System.out.println("Pair: " + pair);
        
        // NumberContainer example
        NumberContainer<Integer> intContainer = new NumberContainer<>(42);
        NumberContainer<Double> doubleContainer = new NumberContainer<>(3.14);
        
        System.out.println("Integer value: " + intContainer.getValue());
        System.out.println("Is integer? " + intContainer.isInteger());
        System.out.println("Double value: " + doubleContainer.getValue());
        System.out.println("Is integer? " + doubleContainer.isInteger());
        
        // Generic method examples
        Integer[] numbers = {1, 2, 3, 4, 5};
        swapElements(numbers, 0, 4);
        System.out.println("After swap: " + Arrays.toString(numbers));
        
        System.out.println("Max value: " + findMax(numbers));
        
        // Wildcard examples
        List<Integer> intList = Arrays.asList(1, 2, 3);
        List<Double> doubleList = Arrays.asList(1.1, 2.2, 3.3);
        
        System.out.print("Integer list: ");
        printList(intList);
        System.out.print("Double list: ");
        printList(doubleList);
        
        System.out.println("Sum of integer list: " + sumOfList(intList));
        System.out.println("Sum of double list: " + sumOfList(doubleList));
    }
} 