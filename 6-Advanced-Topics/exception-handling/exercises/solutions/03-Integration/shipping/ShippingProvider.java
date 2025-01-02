/**
 * Interface for shipping provider operations
 */
public interface ShippingProvider {
    ShipmentQuote getQuote(ShippingRequest request) 
        throws ShippingException;
        
    ShipmentLabel createLabel(ShippingRequest request) 
        throws ShippingException;
        
    ShipmentTracking trackShipment(String trackingNumber) 
        throws ShippingException;
        
    void cancelShipment(String shipmentId) 
        throws ShippingException;
}

/**
 * Shipping request details
 */
public class ShippingRequest {
    private final String orderId;
    private final Address origin;
    private final Address destination;
    private final List<PackageDetails> packages;
    private final ShippingService service;
    private final Map<String, String> options;
    
    private ShippingRequest(Builder builder) {
        this.orderId = builder.orderId;
        this.origin = builder.origin;
        this.destination = builder.destination;
        this.packages = new ArrayList<>(builder.packages);
        this.service = builder.service;
        this.options = new HashMap<>(builder.options);
    }
    
    // Builder pattern implementation
    public static class Builder {
        // Builder implementation
    }
}

/**
 * Shipping result
 */
public class ShipmentResult {
    private final String shipmentId;
    private final String trackingNumber;
    private final ShippingLabel label;
    private final BigDecimal cost;
    private final LocalDateTime estimatedDelivery;
    private final Map<String, String> metadata;
    
    private ShipmentResult(Builder builder) {
        this.shipmentId = builder.shipmentId;
        this.trackingNumber = builder.trackingNumber;
        this.label = builder.label;
        this.cost = builder.cost;
        this.estimatedDelivery = builder.estimatedDelivery;
        this.metadata = new HashMap<>(builder.metadata);
    }
    
    // Builder pattern implementation
    public static class Builder {
        // Builder implementation
    }
} 