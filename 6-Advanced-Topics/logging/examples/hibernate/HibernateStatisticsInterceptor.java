/**
 * Interceptor for collecting Hibernate statistics
 */
public class HibernateStatisticsInterceptor extends EmptyInterceptor {
    private static final Logger logger = LoggerFactory.getLogger(HibernateStatisticsInterceptor.class);
    private final StatisticsCollector stats;
    
    public HibernateStatisticsInterceptor(StatisticsCollector stats) {
        this.stats = stats;
    }
    
    @Override
    public String onPrepareStatement(String sql) {
        stats.incrementQueryCount();
        
        // Log slow queries
        long startTime = System.nanoTime();
        try {
            return sql;
        } finally {
            long duration = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startTime);
            if (duration > 1000) { // More than 1 second
                logger.warn("Slow query detected ({}ms): {}", duration, sql);
                stats.recordSlowQuery(sql, duration);
            }
        }
    }
    
    @Override
    public void postFlush(Iterator entities) {
        stats.recordFlush();
    }
    
    @Override
    public boolean onLoad(Object entity, Serializable id, Object[] state,
            String[] propertyNames, Type[] types) {
        stats.incrementLoadCount();
        return false;
    }
    
    @Override
    public boolean onSave(Object entity, Serializable id, Object[] state,
            String[] propertyNames, Type[] types) {
        stats.incrementSaveCount();
        return false;
    }
    
    @Override
    public void onCollectionRecreate(Object collection, Serializable key) {
        stats.incrementCollectionOperationCount();
    }
    
    @Override
    public void onCollectionRemove(Object collection, Serializable key) {
        stats.incrementCollectionOperationCount();
    }
    
    @Override
    public void onCollectionUpdate(Object collection, Serializable key) {
        stats.incrementCollectionOperationCount();
    }
} 