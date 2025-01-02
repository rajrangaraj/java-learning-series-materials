/**
 * Demonstrates class composition, method overloading, and state management
 */
public class Car {
    // Instance variables
    private String make;
    private String model;
    private int year;
    private double fuelLevel;
    private boolean isRunning;
    private int mileage;
    
    // Composition: Car has an Engine
    private Engine engine;

    // Nested class
    public static class Engine {
        private String type;
        private double displacement;
        private boolean isStarted;

        public Engine(String type, double displacement) {
            this.type = type;
            this.displacement = displacement;
            this.isStarted = false;
        }

        public void start() {
            isStarted = true;
        }

        public void stop() {
            isStarted = false;
        }

        public boolean isStarted() {
            return isStarted;
        }
    }

    // Constructor
    public Car(String make, String model, int year) {
        this.make = make;
        this.model = model;
        this.year = year;
        this.fuelLevel = 100.0;
        this.mileage = 0;
        this.engine = new Engine("V6", 3.5);
    }

    // Methods
    public void start() {
        if (fuelLevel > 0) {
            engine.start();
            isRunning = true;
            System.out.println("Car started");
        } else {
            System.out.println("Can't start: No fuel");
        }
    }

    public void stop() {
        engine.stop();
        isRunning = false;
        System.out.println("Car stopped");
    }

    public void drive(int distance) {
        if (!isRunning) {
            System.out.println("Can't drive: Car is not running");
            return;
        }

        if (fuelLevel <= 0) {
            System.out.println("Can't drive: Out of fuel");
            return;
        }

        mileage += distance;
        fuelLevel -= (distance * 0.1); // Simple fuel consumption model
        System.out.printf("Drove %d miles. Fuel remaining: %.1f%%\n", 
                         distance, fuelLevel);
    }

    public void refuel() {
        fuelLevel = 100.0;
        System.out.println("Tank is now full");
    }

    // Getters
    public String getMake() { return make; }
    public String getModel() { return model; }
    public int getYear() { return year; }
    public double getFuelLevel() { return fuelLevel; }
    public int getMileage() { return mileage; }
    public boolean isRunning() { return isRunning; }

    @Override
    public String toString() {
        return String.format("%d %s %s (Mileage: %d, Fuel: %.1f%%)",
                year, make, model, mileage, fuelLevel);
    }
} 