/**
 * Utility class for advanced mathematical operations on shapes
 */
public final class ShapeMath {
    private static final double EPSILON = 1e-10;

    private ShapeMath() {} // Prevent instantiation

    /**
     * Calculate moments and properties of shapes
     */
    public static class Moments {
        // First moments (centroid)
        public static Point2D calculateCentroid(Shape shape) {
            if (shape instanceof Circle) {
                return shape.getCenter();
            } else if (shape instanceof Polygon) {
                return calculatePolygonCentroid((Polygon) shape);
            }
            throw new UnsupportedOperationException("Unsupported shape type");
        }

        // Second moments (moment of inertia)
        public static double calculateMomentOfInertia(Shape shape) {
            if (shape instanceof Circle) {
                Circle circle = (Circle) shape;
                return Math.PI * Math.pow(circle.getRadius(), 4) / 4;
            } else if (shape instanceof Rectangle) {
                Rectangle rect = (Rectangle) shape;
                double width = rect.getWidth();
                double height = rect.getHeight();
                return (width * Math.pow(height, 3) + height * Math.pow(width, 3)) / 12;
            }
            throw new UnsupportedOperationException("Unsupported shape type");
        }

        private static Point2D calculatePolygonCentroid(Polygon polygon) {
            double area = 0;
            double cx = 0;
            double cy = 0;
            List<Point2D> points = polygon.getPoints();
            
            for (int i = 0; i < points.size(); i++) {
                int j = (i + 1) % points.size();
                double factor = points.get(i).getX() * points.get(j).getY() - 
                              points.get(j).getX() * points.get(i).getY();
                area += factor;
                cx += (points.get(i).getX() + points.get(j).getX()) * factor;
                cy += (points.get(i).getY() + points.get(j).getY()) * factor;
            }
            
            area /= 2.0;
            cx /= (6.0 * area);
            cy /= (6.0 * area);
            
            return new Point2D.Double(cx, cy);
        }
    }

    /**
     * Complex geometric calculations
     */
    public static class Geometry {
        // Calculate shape intersections
        public static List<Point2D> findIntersectionPoints(Shape shape1, Shape shape2) {
            if (shape1 instanceof Circle && shape2 instanceof Circle) {
                return findCircleIntersections((Circle) shape1, (Circle) shape2);
            }
            // Add more intersection calculations as needed
            throw new UnsupportedOperationException("Unsupported shape combination");
        }

        // Calculate minimum enclosing circle
        public static Circle findMinimumEnclosingCircle(List<Point2D> points) {
            if (points == null || points.isEmpty()) {
                throw new IllegalArgumentException("Points list cannot be empty");
            }

            // Welzl's algorithm implementation
            List<Point2D> shuffled = new ArrayList<>(points);
            Collections.shuffle(shuffled);
            return minimumCircle(shuffled, new ArrayList<>(), shuffled.size());
        }

        private static Circle minimumCircle(List<Point2D> points, 
                                          List<Point2D> boundary, int n) {
            if (boundary.size() == 3 || n == 0) {
                return makeCircleFromBoundary(boundary);
            }

            int idx = n - 1;
            Point2D p = points.get(idx);
            
            Circle circle = minimumCircle(points, boundary, idx);
            
            if (circle != null && circle.contains(p)) {
                return circle;
            }

            boundary.add(p);
            circle = minimumCircle(points, boundary, idx);
            boundary.remove(boundary.size() - 1);
            
            return circle;
        }

        private static List<Point2D> findCircleIntersections(Circle c1, Circle c2) {
            List<Point2D> intersections = new ArrayList<>();
            double distance = c1.getCenter().distance(c2.getCenter());
            
            // Circles don't intersect or are identical
            if (distance > c1.getRadius() + c2.getRadius() || 
                distance < Math.abs(c1.getRadius() - c2.getRadius()) ||
                (distance < EPSILON && Math.abs(c1.getRadius() - c2.getRadius()) < EPSILON)) {
                return intersections;
            }

            double a = (c1.getRadius()*c1.getRadius() - 
                       c2.getRadius()*c2.getRadius() + distance*distance) / (2*distance);
            double h = Math.sqrt(c1.getRadius()*c1.getRadius() - a*a);

            Point2D p2 = c2.getCenter();
            Point2D p1 = c1.getCenter();
            
            double x3 = p1.getX() + a*(p2.getX() - p1.getX())/distance;
            double y3 = p1.getY() + a*(p2.getY() - p1.getY())/distance;

            // One intersection point
            if (Math.abs(h) < EPSILON) {
                intersections.add(new Point2D.Double(x3, y3));
                return intersections;
            }

            // Two intersection points
            double x4 = x3 + h*(p2.getY() - p1.getY())/distance;
            double y4 = y3 - h*(p2.getX() - p1.getX())/distance;
            double x5 = x3 - h*(p2.getY() - p1.getY())/distance;
            double y5 = y3 + h*(p2.getX() - p1.getX())/distance;

            intersections.add(new Point2D.Double(x4, y4));
            intersections.add(new Point2D.Double(x5, y5));
            return intersections;
        }
    }

    /**
     * Shape optimization calculations
     */
    public static class Optimization {
        // Find optimal placement of shapes
        public static List<Point2D> findOptimalPlacement(List<Shape> shapes, 
                                                       Rectangle boundary) {
            // Simple grid-based placement
            List<Point2D> placements = new ArrayList<>();
            double x = boundary.getCenter().getX() - boundary.getWidth()/2;
            double y = boundary.getCenter().getY() - boundary.getHeight()/2;
            
            int cols = (int) Math.sqrt(shapes.size());
            int rows = (shapes.size() + cols - 1) / cols;
            
            double cellWidth = boundary.getWidth() / cols;
            double cellHeight = boundary.getHeight() / rows;
            
            for (int i = 0; i < shapes.size(); i++) {
                int row = i / cols;
                int col = i % cols;
                placements.add(new Point2D.Double(
                    x + col * cellWidth + cellWidth/2,
                    y + row * cellHeight + cellHeight/2
                ));
            }
            
            return placements;
        }

        // Calculate minimum area enclosing rectangle
        public static Rectangle findMinimumAreaRectangle(List<Point2D> points) {
            if (points.size() < 3) {
                throw new IllegalArgumentException("Need at least 3 points");
            }

            // Convert to convex hull first
            List<Point2D> hull = createConvexHull(points);
            
            Rectangle minRect = null;
            double minArea = Double.MAX_VALUE;
            
            // Rotate calipers along hull edges
            for (int i = 0; i < hull.size(); i++) {
                Point2D p1 = hull.get(i);
                Point2D p2 = hull.get((i + 1) % hull.size());
                
                // Calculate edge angle
                double angle = Math.atan2(p2.getY() - p1.getY(), 
                                        p2.getX() - p1.getX());
                
                // Rotate points
                List<Point2D> rotated = new ArrayList<>();
                for (Point2D p : hull) {
                    double x = p.getX() * Math.cos(-angle) - 
                              p.getY() * Math.sin(-angle);
                    double y = p.getX() * Math.sin(-angle) + 
                              p.getY() * Math.cos(-angle);
                    rotated.add(new Point2D.Double(x, y));
                }
                
                // Find bounding box
                double minX = Double.MAX_VALUE, maxX = Double.MIN_VALUE;
                double minY = Double.MAX_VALUE, maxY = Double.MIN_VALUE;
                
                for (Point2D p : rotated) {
                    minX = Math.min(minX, p.getX());
                    maxX = Math.max(maxX, p.getX());
                    minY = Math.min(minY, p.getY());
                    maxY = Math.max(maxY, p.getY());
                }
                
                double area = (maxX - minX) * (maxY - minY);
                if (area < minArea) {
                    minArea = area;
                    minRect = new Rectangle("black", 
                        new Point2D.Double((maxX + minX)/2, (maxY + minY)/2),
                        maxX - minX, maxY - minY);
                    minRect.setRotation(Math.toDegrees(angle));
                }
            }
            
            return minRect;
        }

        private static List<Point2D> createConvexHull(List<Point2D> points) {
            // Graham scan algorithm implementation
            // ... implementation details ...
            return new ArrayList<>(); // Placeholder
        }
    }
} 