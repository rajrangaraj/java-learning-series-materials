public class Cat extends Animal {
    private boolean isIndoor;
    private int lives;

    public Cat(String name, int age, double weight, boolean isIndoor) {
        super(name, age, weight);
        this.isIndoor = isIndoor;
        this.lives = 9;
    }

    @Override
    public String makeSound() {
        return "Meow!";
    }

    @Override
    public String getSpecies() {
        return "Cat";
    }

    // Cat-specific methods
    public void scratch() {
        System.out.println(name + " is scratching.");
    }

    public void loseLive() {
        if (lives > 0) {
            lives--;
            System.out.println(name + " lost a life! " + lives + " remaining.");
        } else {
            System.out.println(name + " has no more lives!");
        }
    }

    public boolean isIndoor() {
        return isIndoor;
    }

    public int getLives() {
        return lives;
    }

    @Override
    public String toString() {
        return super.toString() + String.format(" Indoor: %s, Lives: %d",
                                              isIndoor, lives);
    }
} 