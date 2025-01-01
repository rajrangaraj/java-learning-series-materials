/**
 * Utility class for geometric calculations
 */
public final class GeometryUtils {
    private GeometryUtils() {} // Prevent instantiation

    public static final double EPSILON = 1e-10;

    // Point operations
    public static double distance(Point2D p1, Point2D p2) {
        return p1.distance(p2);
    }

    public static Point2D midpoint(Point2D p1, Point2D p2) {
        return new Point2D.Double(
            (p1.getX() + p2.getX()) / 2,
            (p1.getY() + p2.getY()) / 2
        );
    }

    // Angle calculations
    public static double angleBetweenPoints(Point2D center, Point2D p1, Point2D p2) {
        double angle1 = Math.atan2(p1.getY() - center.getY(), p1.getX() - center.getX());
        double angle2 = Math.atan2(p2.getY() - center.getY(), p2.getX() - center.getX());
        double angle = Math.toDegrees(angle2 - angle1);
        return angle < 0 ? angle + 360 : angle;
    }

    public static double normalizeAngle(double angle) {
        angle = angle % 360;
        return angle < 0 ? angle + 360 : angle;
    }

    // Line operations
    public static boolean linesIntersect(Point2D a1, Point2D a2, Point2D b1, Point2D b2) {
        double det = (a2.getX() - a1.getX()) * (b2.getY() - b1.getY()) -
                    (b2.getX() - b1.getX()) * (a2.getY() - a1.getY());
        
        if (Math.abs(det) < EPSILON) return false;

        double t = ((b1.getX() - a1.getX()) * (b2.getY() - b1.getY()) -
                   (b2.getX() - b1.getX()) * (b1.getY() - a1.getY())) / det;
        
        double u = ((a2.getX() - a1.getX()) * (b1.getY() - a1.getY()) -
                   (b1.getX() - a1.getX()) * (a2.getY() - a1.getY())) / det;

        return t >= 0 && t <= 1 && u >= 0 && u <= 1;
    }

    // Area calculations
    public static double triangleArea(Point2D a, Point2D b, Point2D c) {
        return Math.abs((b.getX() - a.getX()) * (c.getY() - a.getY()) -
                       (c.getX() - a.getX()) * (b.getY() - a.getY())) / 2;
    }

    public static double polygonArea(List<Point2D> points) {
        if (points.size() < 3) return 0;
        
        double area = 0;
        int j = points.size() - 1;
        
        for (int i = 0; i < points.size(); i++) {
            area += (points.get(j).getX() + points.get(i).getX()) *
                   (points.get(j).getY() - points.get(i).getY());
            j = i;
        }
        
        return Math.abs(area / 2);
    }

    // Transformation utilities
    public static Point2D rotatePoint(Point2D point, Point2D center, double angleDegrees) {
        double angleRad = Math.toRadians(angleDegrees);
        double cos = Math.cos(angleRad);
        double sin = Math.sin(angleRad);
        
        double dx = point.getX() - center.getX();
        double dy = point.getY() - center.getY();
        
        return new Point2D.Double(
            center.getX() + dx * cos - dy * sin,
            center.getY() + dx * sin + dy * cos
        );
    }

    public static Point2D scalePoint(Point2D point, Point2D center, double factor) {
        double dx = point.getX() - center.getX();
        double dy = point.getY() - center.getY();
        
        return new Point2D.Double(
            center.getX() + dx * factor,
            center.getY() + dy * factor
        );
    }

    // Numerical utilities
    public static boolean doubleEquals(double a, double b) {
        return Math.abs(a - b) < EPSILON;
    }

    public static double round(double value, int decimals) {
        double scale = Math.pow(10, decimals);
        return Math.round(value * scale) / scale;
    }
} 