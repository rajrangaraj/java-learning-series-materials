/**
 * Utility class for managing and calculating properties of shapes
 */
public class ShapeCalculator {
    private List<Shape> shapes;
    private Map<String, ShapeGroup> groups;

    public ShapeCalculator() {
        shapes = new ArrayList<>();
        groups = new HashMap<>();
    }

    // Shape management
    public void addShape(Shape shape) {
        shapes.add(shape);
    }

    public void removeShape(Shape shape) {
        shapes.remove(shape);
        // Remove from all groups
        groups.values().forEach(group -> group.removeShape(shape));
    }

    // Group management
    public void createGroup(String groupName) {
        groups.put(groupName, new ShapeGroup(groupName));
    }

    public void addToGroup(String groupName, Shape shape) {
        groups.computeIfAbsent(groupName, k -> new ShapeGroup(k))
              .addShape(shape);
    }

    // Calculations
    public double getTotalArea() {
        return shapes.stream()
                    .mapToDouble(Shape::calculateArea)
                    .sum();
    }

    public double getTotalPerimeter() {
        return shapes.stream()
                    .mapToDouble(Shape::calculatePerimeter)
                    .sum();
    }

    // Shape analysis
    public Shape getLargestShape() {
        return shapes.stream()
                    .max(Shape::compareTo)
                    .orElse(null);
    }

    public Shape getSmallestShape() {
        return shapes.stream()
                    .min(Shape::compareTo)
                    .orElse(null);
    }

    public Map<String, Double> getAreasByType() {
        return shapes.stream()
                    .collect(Collectors.groupingBy(
                        shape -> shape.getClass().getSimpleName(),
                        Collectors.summingDouble(Shape::calculateArea)
                    ));
    }

    // Shape operations
    public void scaleAll(double factor) {
        shapes.forEach(shape -> shape.scale(factor));
    }

    public void moveAll(double dx, double dy) {
        shapes.forEach(shape -> shape.move(dx, dy));
    }

    // Sorting
    public List<Shape> sortByArea() {
        List<Shape> sorted = new ArrayList<>(shapes);
        Collections.sort(sorted);
        return sorted;
    }

    public List<Shape> sortByPerimeter() {
        List<Shape> sorted = new ArrayList<>(shapes);
        Collections.sort(sorted, 
            Comparator.comparingDouble(Shape::calculatePerimeter));
        return sorted;
    }

    // Statistics
    public ShapeStatistics getStatistics() {
        return new ShapeStatistics(shapes);
    }

    // Report generation
    public String generateReport() {
        StringBuilder report = new StringBuilder();
        ShapeStatistics stats = getStatistics();
        
        report.append("Shape Calculator Report\n");
        report.append("=====================\n\n");
        
        report.append("Overall Statistics:\n");
        report.append(String.format("Total Shapes: %d\n", shapes.size()));
        report.append(String.format("Total Area: %.2f\n", stats.getTotalArea()));
        report.append(String.format("Total Perimeter: %.2f\n", stats.getTotalPerimeter()));
        
        report.append("\nShape Distribution:\n");
        stats.getShapeCounts().forEach((type, count) -> 
            report.append(String.format("- %s: %d\n", type, count)));
        
        report.append("\nArea Distribution:\n");
        stats.getAreasByType().forEach((type, area) -> 
            report.append(String.format("- %s: %.2f\n", type, area)));
        
        return report.toString();
    }
} 