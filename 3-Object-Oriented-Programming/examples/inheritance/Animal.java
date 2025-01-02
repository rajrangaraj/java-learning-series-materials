/**
 * Base class demonstrating inheritance principles
 */
public abstract class Animal {
    // Protected fields accessible by subclasses
    protected String name;
    protected int age;
    protected double weight;

    // Constructor
    public Animal(String name, int age, double weight) {
        this.name = name;
        this.age = age;
        this.weight = weight;
    }

    // Abstract methods to be implemented by subclasses
    public abstract String makeSound();
    public abstract String getSpecies();

    // Common methods for all animals
    public void eat() {
        System.out.println(name + " is eating.");
        weight += 0.1;
    }

    public void sleep() {
        System.out.println(name + " is sleeping.");
    }

    // Getters and setters
    public String getName() { return name; }
    public int getAge() { return age; }
    public double getWeight() { return weight; }

    public void setWeight(double weight) {
        if (weight > 0) {
            this.weight = weight;
        }
    }

    @Override
    public String toString() {
        return String.format("%s[name=%s, age=%d, weight=%.1f]",
                           getSpecies(), name, age, weight);
    }
} 