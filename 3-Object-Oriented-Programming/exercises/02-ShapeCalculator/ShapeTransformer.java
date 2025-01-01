/**
 * Utility class for complex shape transformations
 */
public class ShapeTransformer {
    private static final double EPSILON = 1e-10;

    // Shape combination operations
    public static Shape union(Shape shape1, Shape shape2) {
        if (shape1 instanceof Circle && shape2 instanceof Circle) {
            return unionCircles((Circle)shape1, (Circle)shape2);
        }
        // Add more shape combinations as needed
        throw new UnsupportedOperationException(
            "Union not supported for these shape types"
        );
    }

    public static Shape intersection(Shape shape1, Shape shape2) {
        if (shape1 instanceof Circle && shape2 instanceof Circle) {
            return intersectCircles((Circle)shape1, (Circle)shape2);
        }
        // Add more shape combinations as needed
        throw new UnsupportedOperationException(
            "Intersection not supported for these shape types"
        );
    }

    // Complex transformations
    public static Shape morph(Shape source, Shape target, double progress) {
        if (source.getClass() != target.getClass()) {
            throw new IllegalArgumentException("Shapes must be of the same type");
        }

        if (progress < 0 || progress > 1) {
            throw new IllegalArgumentException("Progress must be between 0 and 1");
        }

        if (source instanceof Circle && target instanceof Circle) {
            return morphCircles((Circle)source, (Circle)target, progress);
        }
        // Add more shape morphing implementations
        throw new UnsupportedOperationException(
            "Morphing not supported for this shape type"
        );
    }

    // Helper methods for specific shape operations
    private static Shape unionCircles(Circle c1, Circle c2) {
        double distance = c1.getCenter().distance(c2.getCenter());
        
        // If one circle contains the other
        if (distance + Math.min(c1.getRadius(), c2.getRadius()) <=
            Math.max(c1.getRadius(), c2.getRadius())) {
            return c1.getRadius() > c2.getRadius() ? c1.clone() : c2.clone();
        }

        // If circles are separate
        if (distance >= c1.getRadius() + c2.getRadius()) {
            // Return the bounding circle
            Point2D center = GeometryUtils.midpoint(c1.getCenter(), c2.getCenter());
            double radius = (distance + c1.getRadius() + c2.getRadius()) / 2;
            return new Circle(c1.getColor(), center, radius);
        }

        // Circles intersect - create minimum bounding circle
        Point2D center = GeometryUtils.midpoint(c1.getCenter(), c2.getCenter());
        double radius = (distance + c1.getRadius() + c2.getRadius()) / 2;
        return new Circle(c1.getColor(), center, radius);
    }

    private static Shape intersectCircles(Circle c1, Circle c2) {
        double distance = c1.getCenter().distance(c2.getCenter());
        
        // If circles don't intersect
        if (distance > c1.getRadius() + c2.getRadius()) {
            return null;
        }

        // If one circle contains the other
        if (distance + Math.min(c1.getRadius(), c2.getRadius()) <=
            Math.max(c1.getRadius(), c2.getRadius())) {
            return c1.getRadius() < c2.getRadius() ? c1.clone() : c2.clone();
        }

        // Circles intersect - return the smaller circle
        return c1.getRadius() < c2.getRadius() ? c1.clone() : c2.clone();
    }

    private static Shape morphCircles(Circle source, Circle target, double progress) {
        Point2D newCenter = new Point2D.Double(
            source.getCenter().getX() * (1 - progress) + target.getCenter().getX() * progress,
            source.getCenter().getY() * (1 - progress) + target.getCenter().getY() * progress
        );
        
        double newRadius = source.getRadius() * (1 - progress) + target.getRadius() * progress;
        
        // Interpolate colors if they're different
        String newColor = source.getColor();
        if (!source.getColor().equals(target.getColor())) {
            Color sourceColor = Color.decode(source.getColor());
            Color targetColor = Color.decode(target.getColor());
            
            int red = (int)(sourceColor.getRed() * (1 - progress) + 
                           targetColor.getRed() * progress);
            int green = (int)(sourceColor.getGreen() * (1 - progress) + 
                            targetColor.getGreen() * progress);
            int blue = (int)(sourceColor.getBlue() * (1 - progress) + 
                           targetColor.getBlue() * progress);
            
            newColor = String.format("#%02x%02x%02x", red, green, blue);
        }
        
        return new Circle(newColor, newCenter, newRadius);
    }
} 