/**
 * Generic object pool implementation
 */
public class ObjectPool<T> {
    private final BlockingQueue<PooledObject<T>> available;
    private final Set<PooledObject<T>> inUse;
    private final ObjectFactory<T> factory;
    private final ObjectValidator<T> validator;
    private final PoolConfig config;
    private final PoolStats stats;
    
    public ObjectPool(ObjectFactory<T> factory, PoolConfig config) {
        this.factory = factory;
        this.config = config;
        this.validator = new ObjectValidator<>();
        this.available = new ArrayBlockingQueue<>(config.getMaxSize());
        this.inUse = ConcurrentHashMap.newKeySet();
        this.stats = new PoolStats();
        
        // Initialize pool with minimum size
        for (int i = 0; i < config.getMinSize(); i++) {
            available.offer(createPooledObject());
        }
        
        // Start maintenance thread
        startMaintenanceTask();
    }
    
    public T acquire() throws PoolException {
        try {
            PooledObject<T> pooledObject = available.poll(
                config.getMaxWaitMillis(), TimeUnit.MILLISECONDS);
                
            if (pooledObject == null) {
                if (getTotalSize() < config.getMaxSize()) {
                    pooledObject = createPooledObject();
                } else {
                    throw new PoolException("Pool exhausted");
                }
            }
            
            if (!validator.isValid(pooledObject.getObject())) {
                stats.recordInvalid();
                pooledObject = createPooledObject();
            }
            
            inUse.add(pooledObject);
            pooledObject.markAsInUse();
            stats.recordAcquired();
            return pooledObject.getObject();
            
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new PoolException("Acquisition interrupted");
        }
    }
    
    public void release(T object) {
        PooledObject<T> pooledObject = findPooledObject(object);
        if (pooledObject != null) {
            if (available.size() < config.getMaxSize()) {
                pooledObject.markAsAvailable();
                inUse.remove(pooledObject);
                available.offer(pooledObject);
                stats.recordReleased();
            } else {
                removeObject(pooledObject);
            }
        }
    }
    
    private PooledObject<T> createPooledObject() {
        T object = factory.create();
        PooledObject<T> pooledObject = new PooledObject<>(object);
        stats.recordCreated();
        return pooledObject;
    }
    
    private void removeObject(PooledObject<T> pooledObject) {
        inUse.remove(pooledObject);
        factory.destroy(pooledObject.getObject());
        stats.recordDestroyed();
    }
    
    private PooledObject<T> findPooledObject(T object) {
        return inUse.stream()
            .filter(po -> po.getObject() == object)
            .findFirst()
            .orElse(null);
    }
    
    private void startMaintenanceTask() {
        ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
        executor.scheduleAtFixedRate(
            this::performMaintenance,
            config.getMaintenanceIntervalMillis(),
            config.getMaintenanceIntervalMillis(),
            TimeUnit.MILLISECONDS
        );
    }
    
    private void performMaintenance() {
        // Remove invalid objects
        available.removeIf(pooledObject -> {
            if (!validator.isValid(pooledObject.getObject())) {
                stats.recordInvalid();
                return true;
            }
            return false;
        });
        
        // Maintain minimum size
        while (available.size() < config.getMinSize()) {
            available.offer(createPooledObject());
        }
        
        // Check for expired objects
        available.removeIf(pooledObject -> {
            if (pooledObject.isExpired(config.getMaxIdleTimeMillis())) {
                stats.recordExpired();
                return true;
            }
            return false;
        });
    }
    
    public PoolStats getStats() {
        return stats;
    }
    
    private int getTotalSize() {
        return available.size() + inUse.size();
    }
} 