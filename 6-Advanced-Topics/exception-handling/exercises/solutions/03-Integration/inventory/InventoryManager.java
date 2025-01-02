/**
 * Manages inventory operations with optimistic locking and deadlock recovery
 */
public class InventoryManager {
    private static final Logger logger = LoggerFactory.getLogger(InventoryManager.class);
    
    private final InventoryRepository repository;
    private final DeadlockRetryPolicy deadlockRetryPolicy;
    private final MetricsRegistry metrics;
    private final Lock inventoryLock;
    
    public InventoryManager(InventoryRepository repository, 
                          MetricsRegistry metrics) {
        this.repository = repository;
        this.metrics = metrics;
        this.inventoryLock = new ReentrantLock();
        
        this.deadlockRetryPolicy = DeadlockRetryPolicy.builder()
            .maxRetries(5)
            .backoffPeriod(Duration.ofMillis(100))
            .build();
    }
    
    public InventoryResult reserveItems(List<OrderItem> items) 
            throws InventoryException {
        Timer.Sample timer = Timer.start(metrics.getRegistry());
        
        try {
            return deadlockRetryPolicy.execute(() -> {
                try {
                    // Try optimistic locking first
                    InventoryResult result = tryOptimisticReservation(items);
                    
                    // If failed due to conflict, try pessimistic locking
                    if (!result.isSuccessful()) {
                        result = tryPessimisticReservation(items);
                    }
                    
                    if (result.isSuccessful()) {
                        recordSuccess(timer);
                    } else {
                        recordFailure(result.getErrorCode());
                    }
                    
                    return result;
                    
                } catch (OptimisticLockingException e) {
                    metrics.incrementCounter("inventory.conflict");
                    throw e;
                    
                } catch (DeadlockException e) {
                    metrics.incrementCounter("inventory.deadlock");
                    throw e;
                }
            });
            
        } catch (OptimisticLockingException e) {
            throw new InventoryException(
                "Inventory conflict after retries", e);
            
        } catch (DeadlockException e) {
            throw new InventoryException(
                "Deadlock detected after retries", e);
            
        } catch (Exception e) {
            metrics.incrementCounter("inventory.unexpected.error");
            throw new InventoryException(
                "Unexpected inventory error", e);
        }
    }
    
    private InventoryResult tryOptimisticReservation(List<OrderItem> items) 
            throws InventoryException {
        try {
            // Check availability
            Map<String, Integer> availability = 
                repository.checkAvailability(items);
                
            // Validate all items are available
            List<String> unavailableItems = availability.entrySet()
                .stream()
                .filter(e -> e.getValue() < 0)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
                
            if (!unavailableItems.isEmpty()) {
                return InventoryResult.insufficientStock(unavailableItems);
            }
            
            // Try to reserve with optimistic locking
            return repository.reserveItems(items);
            
        } catch (Exception e) {
            logger.error("Optimistic reservation failed", e);
            return InventoryResult.error("RESERVATION_FAILED");
        }
    }
    
    private InventoryResult tryPessimisticReservation(List<OrderItem> items) 
            throws InventoryException {
        if (!inventoryLock.tryLock()) {
            throw new InventoryException("Failed to acquire inventory lock");
        }
        
        try {
            return repository.reserveItemsWithLock(items);
        } finally {
            inventoryLock.unlock();
        }
    }
    
    private void recordSuccess(Timer.Sample timer) {
        timer.stop(metrics.timer("inventory.reservation.time"));
        metrics.incrementCounter("inventory.success");
    }
    
    private void recordFailure(String errorCode) {
        metrics.incrementCounter("inventory.failure." + errorCode);
    }
} 