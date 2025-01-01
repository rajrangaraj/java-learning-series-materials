/**
 * Class for calculating and storing shape statistics
 */
public class ShapeStatistics {
    private final List<Shape> shapes;
    private final Map<String, Integer> shapeCounts;
    private final Map<String, Double> areasByType;
    private final double totalArea;
    private final double totalPerimeter;
    private final double averageArea;
    private final BoundingBox boundingBox;

    public ShapeStatistics(List<Shape> shapes) {
        this.shapes = new ArrayList<>(shapes);
        this.shapeCounts = calculateShapeCounts();
        this.areasByType = calculateAreasByType();
        this.totalArea = calculateTotalArea();
        this.totalPerimeter = calculateTotalPerimeter();
        this.averageArea = totalArea / shapes.size();
        this.boundingBox = calculateBoundingBox();
    }

    private Map<String, Integer> calculateShapeCounts() {
        return shapes.stream()
                    .collect(Collectors.groupingBy(
                        shape -> shape.getClass().getSimpleName(),
                        Collectors.counting()
                    ))
                    .entrySet().stream()
                    .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        e -> e.getValue().intValue()
                    ));
    }

    private Map<String, Double> calculateAreasByType() {
        return shapes.stream()
                    .collect(Collectors.groupingBy(
                        shape -> shape.getClass().getSimpleName(),
                        Collectors.summingDouble(Shape::calculateArea)
                    ));
    }

    private double calculateTotalArea() {
        return shapes.stream()
                    .mapToDouble(Shape::calculateArea)
                    .sum();
    }

    private double calculateTotalPerimeter() {
        return shapes.stream()
                    .mapToDouble(Shape::calculatePerimeter)
                    .sum();
    }

    private BoundingBox calculateBoundingBox() {
        if (shapes.isEmpty()) return null;

        double minX = Double.MAX_VALUE;
        double minY = Double.MAX_VALUE;
        double maxX = Double.MIN_VALUE;
        double maxY = Double.MIN_VALUE;

        for (Shape shape : shapes) {
            Point2D center = shape.getCenter();
            minX = Math.min(minX, center.getX());
            minY = Math.min(minY, center.getY());
            maxX = Math.max(maxX, center.getX());
            maxY = Math.max(maxY, center.getY());
        }

        return new BoundingBox(
            new Point2D.Double(minX, minY),
            new Point2D.Double(maxX, maxY)
        );
    }

    // Getters
    public Map<String, Integer> getShapeCounts() {
        return new HashMap<>(shapeCounts);
    }

    public Map<String, Double> getAreasByType() {
        return new HashMap<>(areasByType);
    }

    public double getTotalArea() { return totalArea; }
    public double getTotalPerimeter() { return totalPerimeter; }
    public double getAverageArea() { return averageArea; }
    public BoundingBox getBoundingBox() { return boundingBox; }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Shape Statistics:\n");
        sb.append(String.format("Total Shapes: %d\n", shapes.size()));
        sb.append(String.format("Total Area: %.2f\n", totalArea));
        sb.append(String.format("Average Area: %.2f\n", averageArea));
        sb.append(String.format("Total Perimeter: %.2f\n", totalPerimeter));
        
        sb.append("\nShape Distribution:\n");
        shapeCounts.forEach((type, count) -> 
            sb.append(String.format("- %s: %d\n", type, count)));
        
        return sb.toString();
    }
} 