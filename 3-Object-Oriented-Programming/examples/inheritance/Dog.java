public class Dog extends Animal {
    private String breed;
    private boolean isVaccinated;

    public Dog(String name, int age, double weight, String breed) {
        super(name, age, weight);
        this.breed = breed;
        this.isVaccinated = false;
    }

    @Override
    public String makeSound() {
        return "Woof!";
    }

    @Override
    public String getSpecies() {
        return "Dog";
    }

    // Dog-specific methods
    public void fetch() {
        System.out.println(name + " is fetching the ball.");
    }

    public void vaccinate() {
        isVaccinated = true;
        System.out.println(name + " has been vaccinated.");
    }

    public boolean isVaccinated() {
        return isVaccinated;
    }

    public String getBreed() {
        return breed;
    }

    @Override
    public String toString() {
        return super.toString() + String.format(" Breed: %s, Vaccinated: %s",
                                              breed, isVaccinated);
    }
} 