public class ShapeRenderer {
    // ... (previous constants)
    private static final Color GRID_COLOR = new Color(200, 200, 200);
    private static final Color AXIS_COLOR = new Color(100, 100, 100);
    private static final float DASH_LENGTH[] = {5.0f};
    private static final int GRID_SPACING = 50;

    private void drawRectangle(Graphics2D g, Rectangle rect) {
        Point2D center = rect.getCenter();
        double width = rect.getWidth();
        double height = rect.getHeight();
        double rotation = Math.toRadians(rect.getRotation());

        // Create the rectangle path
        Path2D.Double path = new Path2D.Double();
        double[] corners = {
            -width/2, -height/2,  // Top left
             width/2, -height/2,  // Top right
             width/2,  height/2,  // Bottom right
            -width/2,  height/2   // Bottom left
        };

        // Apply rotation
        for (int i = 0; i < corners.length; i += 2) {
            double x = corners[i];
            double y = corners[i + 1];
            double rotatedX = x * Math.cos(rotation) - y * Math.sin(rotation);
            double rotatedY = x * Math.sin(rotation) + y * Math.cos(rotation);
            
            if (i == 0) {
                path.moveTo(center.getX() + rotatedX, center.getY() + rotatedY);
            } else {
                path.lineTo(center.getX() + rotatedX, center.getY() + rotatedY);
            }
        }
        path.closePath();

        // Draw the rectangle
        g.draw(path);
        
        // Draw center point
        drawCenterPoint(g, center);
    }

    private void drawTriangle(Graphics2D g, Triangle triangle) {
        Point2D a = triangle.getPointA();
        Point2D b = triangle.getPointB();
        Point2D c = triangle.getPointC();

        Path2D.Double path = new Path2D.Double();
        path.moveTo(a.getX(), a.getY());
        path.lineTo(b.getX(), b.getY());
        path.lineTo(c.getX(), c.getY());
        path.closePath();

        // Draw the triangle
        g.draw(path);
        
        // Draw center point
        drawCenterPoint(g, triangle.getCenter());

        // Draw angle markers if it's a special triangle
        if (triangle.isEquilateral() || triangle.isIsosceles()) {
            drawAngleMarkers(g, triangle);
        }
    }

    private void drawGrid(Graphics2D g) {
        // Save current graphics state
        Color originalColor = g.getColor();
        Stroke originalStroke = g.getStroke();

        // Set grid style
        g.setColor(GRID_COLOR);
        g.setStroke(new BasicStroke(1.0f, BasicStroke.CAP_BUTT, 
            BasicStroke.JOIN_MITER, 10.0f, DASH_LENGTH, 0.0f));

        // Calculate grid bounds
        Rectangle2D bounds = transform.createTransformedShape(
            new Rectangle2D.Double(
                worldBounds.getMinPoint().getX(),
                worldBounds.getMinPoint().getY(),
                worldBounds.getWidth(),
                worldBounds.getHeight()
            )).getBounds2D();

        // Draw vertical grid lines
        for (double x = bounds.getX(); x <= bounds.getX() + bounds.getWidth(); x += GRID_SPACING) {
            g.drawLine((int)x, (int)bounds.getY(), 
                      (int)x, (int)(bounds.getY() + bounds.getHeight()));
        }

        // Draw horizontal grid lines
        for (double y = bounds.getY(); y <= bounds.getY() + bounds.getHeight(); y += GRID_SPACING) {
            g.drawLine((int)bounds.getX(), (int)y,
                      (int)(bounds.getX() + bounds.getWidth()), (int)y);
        }

        // Draw axes
        g.setColor(AXIS_COLOR);
        g.setStroke(new BasicStroke(2.0f));
        g.drawLine((int)bounds.getX(), CANVAS_HEIGHT/2,
                  (int)(bounds.getX() + bounds.getWidth()), CANVAS_HEIGHT/2);
        g.drawLine(CANVAS_WIDTH/2, (int)bounds.getY(),
                  CANVAS_WIDTH/2, (int)(bounds.getY() + bounds.getHeight()));

        // Restore graphics state
        g.setColor(originalColor);
        g.setStroke(originalStroke);
    }

    private void drawCenterPoint(Graphics2D g, Point2D center) {
        double size = 4;
        g.fill(new Ellipse2D.Double(
            center.getX() - size/2,
            center.getY() - size/2,
            size, size
        ));
    }

    private void drawAngleMarkers(Graphics2D g, Triangle triangle) {
        // Implementation for drawing angle markers
    }

    // Add methods for saving to image file
    public void saveToFile(String filename) throws IOException {
        BufferedImage image = new BufferedImage(
            CANVAS_WIDTH, CANVAS_HEIGHT, BufferedImage.TYPE_INT_ARGB
        );
        Graphics2D g = image.createGraphics();
        
        // Set rendering hints for better quality
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                          RenderingHints.VALUE_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL,
                          RenderingHints.VALUE_STROKE_PURE);

        // Fill background
        g.setColor(Color.WHITE);
        g.fillRect(0, 0, CANVAS_WIDTH, CANVAS_HEIGHT);

        // Render shapes
        render(g);

        // Save to file
        ImageIO.write(image, "PNG", new File(filename));
        g.dispose();
    }
} 