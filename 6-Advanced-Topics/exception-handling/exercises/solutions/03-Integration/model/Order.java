/**
 * Represents an order in the system
 */
public class Order {
    private final String id;
    private final String customerId;
    private final List<OrderItem> items;
    private final PaymentDetails paymentDetails;
    private final Address shippingAddress;
    private final ShippingService shippingService;
    private OrderStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Map<String, String> metadata;
    
    private Order(Builder builder) {
        this.id = builder.id;
        this.customerId = builder.customerId;
        this.items = new ArrayList<>(builder.items);
        this.paymentDetails = builder.paymentDetails;
        this.shippingAddress = builder.shippingAddress;
        this.shippingService = builder.shippingService;
        this.status = OrderStatus.CREATED;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = this.createdAt;
        this.metadata = new HashMap<>(builder.metadata);
    }
    
    // Getters and utility methods
    
    public static class Builder {
        // Builder implementation
    }
}

/**
 * Represents an item in an order
 */
public class OrderItem {
    private final String productId;
    private final int quantity;
    private final BigDecimal price;
    private final Map<String, String> attributes;
    
    // Constructor and methods
}

/**
 * Represents a customer
 */
public class Customer {
    private final String id;
    private final String email;
    private final String name;
    private final List<Address> addresses;
    private final List<PaymentMethod> paymentMethods;
    private CustomerStatus status;
    
    // Constructor and methods
}

/**
 * Represents an address
 */
public class Address {
    private final String street;
    private final String city;
    private final String state;
    private final String postalCode;
    private final String country;
    private final String phoneNumber;
    
    // Constructor and methods
} 