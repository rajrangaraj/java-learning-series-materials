/**
 * Rectangle implementation
 */
public class Rectangle extends Shape {
    private double width;
    private double height;
    private double rotation; // in degrees

    public Rectangle(String color, Point2D center, double width, double height) {
        super("Rectangle", color, center);
        setWidth(width);
        setHeight(height);
        this.rotation = 0.0;
    }

    @Override
    public double calculateArea() {
        return width * height;
    }

    @Override
    public double calculatePerimeter() {
        return 2 * (width + height);
    }

    @Override
    public void scale(double factor) {
        if (factor <= 0) {
            throw new IllegalArgumentException("Scale factor must be positive");
        }
        this.width *= factor;
        this.height *= factor;
    }

    @Override
    public Shape clone() {
        Rectangle clone = new Rectangle(this.color, 
            (Point2D) this.center.clone(), this.width, this.height);
        clone.setRotation(this.rotation);
        return clone;
    }

    // Rectangle-specific methods
    public boolean contains(Point2D point) {
        // Transform point based on rectangle's rotation
        Point2D transformed = transformPoint(point);
        double dx = Math.abs(transformed.getX() - center.getX());
        double dy = Math.abs(transformed.getY() - center.getY());
        return dx <= width/2 && dy <= height/2;
    }

    public boolean intersects(Rectangle other) {
        // Simplified intersection check (doesn't account for rotation)
        double dx = Math.abs(this.center.getX() - other.center.getX());
        double dy = Math.abs(this.center.getY() - other.center.getY());
        return dx <= (this.width + other.width)/2 && 
               dy <= (this.height + other.height)/2;
    }

    private Point2D transformPoint(Point2D point) {
        if (rotation == 0.0) return point;
        
        double rad = Math.toRadians(rotation);
        double cos = Math.cos(rad);
        double sin = Math.sin(rad);
        
        double dx = point.getX() - center.getX();
        double dy = point.getY() - center.getY();
        
        return new Point2D.Double(
            center.getX() + dx * cos - dy * sin,
            center.getY() + dx * sin + dy * cos
        );
    }

    // Getters and setters
    public double getWidth() { return width; }
    public double getHeight() { return height; }
    public double getRotation() { return rotation; }
    
    public void setWidth(double width) {
        if (width <= 0) {
            throw new IllegalArgumentException("Width must be positive");
        }
        this.width = width;
    }
    
    public void setHeight(double height) {
        if (height <= 0) {
            throw new IllegalArgumentException("Height must be positive");
        }
        this.height = height;
    }
    
    public void setRotation(double degrees) {
        this.rotation = degrees % 360.0;
    }

    public double getDiagonal() {
        return Math.sqrt(width * width + height * height);
    }

    @Override
    public String toString() {
        return String.format("Rectangle[center=(%.1f,%.1f), width=%.1f, height=%.1f, " +
                           "rotation=%.1f°, color=%s]",
            center.getX(), center.getY(), width, height, rotation, color);
    }
} 