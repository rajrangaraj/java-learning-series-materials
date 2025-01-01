/**
 * Circle implementation
 */
public class Circle extends Shape {
    private double radius;
    private static final double PI = Math.PI;

    public Circle(String color, Point2D center, double radius) {
        super("Circle", color, center);
        setRadius(radius);
    }

    @Override
    public double calculateArea() {
        return PI * radius * radius;
    }

    @Override
    public double calculatePerimeter() {
        return 2 * PI * radius;
    }

    @Override
    public void scale(double factor) {
        if (factor <= 0) {
            throw new IllegalArgumentException("Scale factor must be positive");
        }
        this.radius *= factor;
    }

    @Override
    public Shape clone() {
        return new Circle(this.color, (Point2D) this.center.clone(), this.radius);
    }

    // Circle-specific methods
    public boolean contains(Point2D point) {
        return center.distance(point) <= radius;
    }

    public boolean intersects(Circle other) {
        double distance = this.center.distance(other.center);
        return distance <= (this.radius + other.radius);
    }

    // Getters and setters
    public double getRadius() { return radius; }
    
    public void setRadius(double radius) {
        if (radius <= 0) {
            throw new IllegalArgumentException("Radius must be positive");
        }
        this.radius = radius;
    }

    public double getDiameter() { return 2 * radius; }

    @Override
    public String toString() {
        return String.format("Circle[center=(%.1f,%.1f), radius=%.1f, color=%s]",
            center.getX(), center.getY(), radius, color);
    }
} 