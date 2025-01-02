/**
 * Interface for inventory data operations
 */
public interface InventoryRepository {
    Map<String, Integer> checkAvailability(List<OrderItem> items) 
        throws InventoryException;
        
    InventoryResult reserveItems(List<OrderItem> items) 
        throws OptimisticLockingException;
        
    InventoryResult reserveItemsWithLock(List<OrderItem> items) 
        throws InventoryException;
        
    void releaseReservation(String reservationId) 
        throws InventoryException;
        
    InventoryStatus getItemStatus(String itemId) 
        throws InventoryException;
}

/**
 * Inventory operation result
 */
public class InventoryResult {
    private final String reservationId;
    private final boolean successful;
    private final Map<String, Integer> allocations;
    private final List<String> unavailableItems;
    private final String errorCode;
    private final LocalDateTime timestamp;
    
    private InventoryResult(Builder builder) {
        this.reservationId = builder.reservationId;
        this.successful = builder.successful;
        this.allocations = new HashMap<>(builder.allocations);
        this.unavailableItems = new ArrayList<>(builder.unavailableItems);
        this.errorCode = builder.errorCode;
        this.timestamp = LocalDateTime.now();
    }
    
    public static InventoryResult success(String reservationId, 
            Map<String, Integer> allocations) {
        return new Builder(reservationId)
            .successful(true)
            .allocations(allocations)
            .build();
    }
    
    public static InventoryResult insufficientStock(List<String> unavailableItems) {
        return new Builder(null)
            .successful(false)
            .unavailableItems(unavailableItems)
            .errorCode("INSUFFICIENT_STOCK")
            .build();
    }
    
    public static InventoryResult error(String errorCode) {
        return new Builder(null)
            .successful(false)
            .errorCode(errorCode)
            .build();
    }
    
    // Builder pattern implementation
    public static class Builder {
        // Builder implementation
    }
} 