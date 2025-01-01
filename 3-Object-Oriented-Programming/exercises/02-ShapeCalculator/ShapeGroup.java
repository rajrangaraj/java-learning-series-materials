/**
 * Class for managing groups of shapes
 */
public class ShapeGroup {
    private String name;
    private List<Shape> shapes;
    private Point2D center;

    public ShapeGroup(String name) {
        this.name = name;
        this.shapes = new ArrayList<>();
        this.center = new Point2D.Double(0, 0);
    }

    public void addShape(Shape shape) {
        shapes.add(shape);
        updateCenter();
    }

    public void removeShape(Shape shape) {
        shapes.remove(shape);
        updateCenter();
    }

    private void updateCenter() {
        if (shapes.isEmpty()) {
            center = new Point2D.Double(0, 0);
            return;
        }

        double sumX = 0, sumY = 0;
        for (Shape shape : shapes) {
            Point2D shapeCenter = shape.getCenter();
            sumX += shapeCenter.getX();
            sumY += shapeCenter.getY();
        }
        center = new Point2D.Double(sumX / shapes.size(), sumY / shapes.size());
    }

    public void moveGroup(double dx, double dy) {
        shapes.forEach(shape -> shape.move(dx, dy));
        updateCenter();
    }

    public void scaleGroup(double factor) {
        shapes.forEach(shape -> {
            // Scale the shape itself
            shape.scale(factor);
            
            // Scale its position relative to group center
            Point2D pos = shape.getCenter();
            double dx = pos.getX() - center.getX();
            double dy = pos.getY() - center.getY();
            shape.move(dx * (factor - 1), dy * (factor - 1));
        });
    }

    public void rotateGroup(double degrees) {
        double radians = Math.toRadians(degrees);
        double cos = Math.cos(radians);
        double sin = Math.sin(radians);

        shapes.forEach(shape -> {
            Point2D pos = shape.getCenter();
            double dx = pos.getX() - center.getX();
            double dy = pos.getY() - center.getY();
            
            double newX = center.getX() + dx * cos - dy * sin;
            double newY = center.getY() + dx * sin + dy * cos;
            
            shape.move(newX - pos.getX(), newY - pos.getY());
        });
    }

    // Getters and calculations
    public String getName() { return name; }
    public List<Shape> getShapes() { return new ArrayList<>(shapes); }
    public Point2D getCenter() { return (Point2D) center.clone(); }
    
    public double getTotalArea() {
        return shapes.stream()
                    .mapToDouble(Shape::calculateArea)
                    .sum();
    }

    public Shape getLargestShape() {
        return shapes.stream()
                    .max(Shape::compareTo)
                    .orElse(null);
    }

    public BoundingBox getBoundingBox() {
        if (shapes.isEmpty()) return null;

        double minX = Double.MAX_VALUE;
        double minY = Double.MAX_VALUE;
        double maxX = Double.MIN_VALUE;
        double maxY = Double.MIN_VALUE;

        for (Shape shape : shapes) {
            Point2D pos = shape.getCenter();
            minX = Math.min(minX, pos.getX());
            minY = Math.min(minY, pos.getY());
            maxX = Math.max(maxX, pos.getX());
            maxY = Math.max(maxY, pos.getY());
        }

        return new BoundingBox(
            new Point2D.Double(minX, minY),
            new Point2D.Double(maxX, maxY)
        );
    }
} 