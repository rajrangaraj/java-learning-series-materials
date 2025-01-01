/**
 * Class representing a rectangular bounding box for shapes
 */
public class BoundingBox {
    private final Point2D minPoint;  // Bottom-left corner
    private final Point2D maxPoint;  // Top-right corner

    public BoundingBox(Point2D minPoint, Point2D maxPoint) {
        this.minPoint = (Point2D) minPoint.clone();
        this.maxPoint = (Point2D) maxPoint.clone();
    }

    // Factory method for creating from points
    public static BoundingBox fromPoints(List<Point2D> points) {
        if (points == null || points.isEmpty()) {
            throw new IllegalArgumentException("Points list cannot be empty");
        }

        double minX = Double.MAX_VALUE;
        double minY = Double.MAX_VALUE;
        double maxX = Double.MIN_VALUE;
        double maxY = Double.MIN_VALUE;

        for (Point2D point : points) {
            minX = Math.min(minX, point.getX());
            minY = Math.min(minY, point.getY());
            maxX = Math.max(maxX, point.getX());
            maxY = Math.max(maxY, point.getY());
        }

        return new BoundingBox(
            new Point2D.Double(minX, minY),
            new Point2D.Double(maxX, maxY)
        );
    }

    // Geometric properties
    public double getWidth() {
        return maxPoint.getX() - minPoint.getX();
    }

    public double getHeight() {
        return maxPoint.getY() - minPoint.getY();
    }

    public double getArea() {
        return getWidth() * getHeight();
    }

    public double getPerimeter() {
        return 2 * (getWidth() + getHeight());
    }

    public Point2D getCenter() {
        return new Point2D.Double(
            (minPoint.getX() + maxPoint.getX()) / 2,
            (minPoint.getY() + maxPoint.getY()) / 2
        );
    }

    // Intersection and containment
    public boolean contains(Point2D point) {
        return point.getX() >= minPoint.getX() && point.getX() <= maxPoint.getX() &&
               point.getY() >= minPoint.getY() && point.getY() <= maxPoint.getY();
    }

    public boolean intersects(BoundingBox other) {
        return !(other.maxPoint.getX() < minPoint.getX() ||
                other.minPoint.getX() > maxPoint.getX() ||
                other.maxPoint.getY() < minPoint.getY() ||
                other.minPoint.getY() > maxPoint.getY());
    }

    public BoundingBox union(BoundingBox other) {
        return new BoundingBox(
            new Point2D.Double(
                Math.min(minPoint.getX(), other.minPoint.getX()),
                Math.min(minPoint.getY(), other.minPoint.getY())
            ),
            new Point2D.Double(
                Math.max(maxPoint.getX(), other.maxPoint.getX()),
                Math.max(maxPoint.getY(), other.maxPoint.getY())
            )
        );
    }

    public BoundingBox intersection(BoundingBox other) {
        if (!intersects(other)) return null;

        return new BoundingBox(
            new Point2D.Double(
                Math.max(minPoint.getX(), other.minPoint.getX()),
                Math.max(minPoint.getY(), other.minPoint.getY())
            ),
            new Point2D.Double(
                Math.min(maxPoint.getX(), other.maxPoint.getX()),
                Math.min(maxPoint.getY(), other.maxPoint.getY())
            )
        );
    }

    // Getters
    public Point2D getMinPoint() { return (Point2D) minPoint.clone(); }
    public Point2D getMaxPoint() { return (Point2D) maxPoint.clone(); }

    @Override
    public String toString() {
        return String.format("BoundingBox[min=(%.1f,%.1f), max=(%.1f,%.1f)]",
            minPoint.getX(), minPoint.getY(),
            maxPoint.getX(), maxPoint.getY());
    }
} 