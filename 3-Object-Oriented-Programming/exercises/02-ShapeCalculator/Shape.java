/**
 * Abstract base class for all shapes
 */
public abstract class Shape implements Comparable<Shape> {
    protected String name;
    protected String color;
    protected Point2D center;

    public Shape(String name, String color, Point2D center) {
        this.name = name;
        this.color = color;
        this.center = center;
    }

    // Abstract methods
    public abstract double calculateArea();
    public abstract double calculatePerimeter();
    public abstract void scale(double factor);
    public abstract Shape clone();

    // Common methods
    @Override
    public int compareTo(Shape other) {
        return Double.compare(this.calculateArea(), other.calculateArea());
    }

    public double distanceTo(Shape other) {
        return this.center.distance(other.center);
    }

    public void move(double dx, double dy) {
        this.center = new Point2D.Double(
            center.getX() + dx,
            center.getY() + dy
        );
    }

    // Getters and setters
    public String getName() { return name; }
    public String getColor() { return color; }
    public Point2D getCenter() { return (Point2D) center.clone(); }

    public void setColor(String color) {
        this.color = color;
    }

    @Override
    public String toString() {
        return String.format("%s[color=%s, area=%.2f, perimeter=%.2f]",
            name, color, calculateArea(), calculatePerimeter());
    }
} 