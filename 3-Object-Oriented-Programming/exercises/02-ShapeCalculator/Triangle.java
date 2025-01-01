/**
 * Triangle implementation
 */
public class Triangle extends Shape {
    private Point2D pointA;
    private Point2D pointB;
    private Point2D pointC;

    public Triangle(String color, Point2D a, Point2D b, Point2D c) {
        super("Triangle", color, calculateCentroid(a, b, c));
        if (!isValid(a, b, c)) {
            throw new IllegalArgumentException("Invalid triangle points");
        }
        this.pointA = (Point2D) a.clone();
        this.pointB = (Point2D) b.clone();
        this.pointC = (Point2D) c.clone();
    }

    private static Point2D calculateCentroid(Point2D a, Point2D b, Point2D c) {
        return new Point2D.Double(
            (a.getX() + b.getX() + c.getX()) / 3,
            (a.getY() + b.getY() + c.getY()) / 3
        );
    }

    private static boolean isValid(Point2D a, Point2D b, Point2D c) {
        double ab = a.distance(b);
        double bc = b.distance(c);
        double ca = c.distance(a);
        return (ab + bc > ca) && (bc + ca > ab) && (ca + ab > bc);
    }

    @Override
    public double calculateArea() {
        // Using Heron's formula
        double a = pointA.distance(pointB);
        double b = pointB.distance(pointC);
        double c = pointC.distance(pointA);
        double s = (a + b + c) / 2; // semi-perimeter
        return Math.sqrt(s * (s-a) * (s-b) * (s-c));
    }

    @Override
    public double calculatePerimeter() {
        return pointA.distance(pointB) + 
               pointB.distance(pointC) + 
               pointC.distance(pointA);
    }

    @Override
    public void scale(double factor) {
        if (factor <= 0) {
            throw new IllegalArgumentException("Scale factor must be positive");
        }
        
        // Scale points relative to centroid
        pointA = scalePoint(pointA, factor);
        pointB = scalePoint(pointB, factor);
        pointC = scalePoint(pointC, factor);
    }

    private Point2D scalePoint(Point2D point, double factor) {
        double dx = point.getX() - center.getX();
        double dy = point.getY() - center.getY();
        return new Point2D.Double(
            center.getX() + dx * factor,
            center.getY() + dy * factor
        );
    }

    @Override
    public Shape clone() {
        return new Triangle(this.color, 
            (Point2D) this.pointA.clone(),
            (Point2D) this.pointB.clone(),
            (Point2D) this.pointC.clone());
    }

    // Triangle-specific methods
    public boolean isEquilateral() {
        double ab = pointA.distance(pointB);
        double bc = pointB.distance(pointC);
        double ca = pointC.distance(pointA);
        double tolerance = 0.0001; // For floating-point comparison
        return Math.abs(ab - bc) < tolerance && 
               Math.abs(bc - ca) < tolerance;
    }

    public boolean isIsosceles() {
        double ab = pointA.distance(pointB);
        double bc = pointB.distance(pointC);
        double ca = pointC.distance(pointA);
        double tolerance = 0.0001;
        return Math.abs(ab - bc) < tolerance || 
               Math.abs(bc - ca) < tolerance || 
               Math.abs(ca - ab) < tolerance;
    }

    public double getAngle(Point2D vertex, Point2D p1, Point2D p2) {
        double a = vertex.distance(p1);
        double b = vertex.distance(p2);
        double c = p1.distance(p2);
        return Math.toDegrees(Math.acos((a*a + b*b - c*c) / (2*a*b)));
    }

    // Getters
    public Point2D getPointA() { return (Point2D) pointA.clone(); }
    public Point2D getPointB() { return (Point2D) pointB.clone(); }
    public Point2D getPointC() { return (Point2D) pointC.clone(); }

    @Override
    public String toString() {
        return String.format("Triangle[A=(%.1f,%.1f), B=(%.1f,%.1f), C=(%.1f,%.1f), color=%s]",
            pointA.getX(), pointA.getY(),
            pointB.getX(), pointB.getY(),
            pointC.getX(), pointC.getY(),
            color);
    }
} 