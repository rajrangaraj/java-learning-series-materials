/**
 * Custom interceptor for logging JPA operations
 */
@Component
public class JpaLoggingInterceptor extends EmptyInterceptor {
    private static final Logger logger = LoggerFactory.getLogger(JpaLoggingInterceptor.class);
    private final ThreadLocal<Long> queryStartTime = new ThreadLocal<>();
    private final QueryStatistics queryStats;
    
    public JpaLoggingInterceptor(QueryStatistics queryStats) {
        this.queryStats = queryStats;
    }
    
    @Override
    public String onPrepareStatement(String sql) {
        queryStartTime.set(System.nanoTime());
        
        // Log normalized SQL for better grouping
        String normalizedSql = normalizeSql(sql);
        MDC.put("sql", normalizedSql);
        
        if (logger.isDebugEnabled()) {
            logger.debug("Executing SQL: {}", sql);
        }
        
        return sql;
    }
    
    @Override
    public void afterTransactionCompletion(Transaction tx) {
        Long startTime = queryStartTime.get();
        if (startTime != null) {
            long duration = TimeUnit.NANOSECONDS.toMillis(
                System.nanoTime() - startTime);
            
            // Record statistics
            queryStats.recordQueryExecution(
                MDC.get("sql"), duration);
            
            // Log slow queries
            if (duration > 1000) {
                logger.warn("Slow query detected ({}ms): {}", 
                    duration, MDC.get("sql"));
            }
            
            queryStartTime.remove();
            MDC.remove("sql");
        }
    }
    
    @Override
    public boolean onLoad(Object entity, Serializable id,
            Object[] state, String[] propertyNames, Type[] types) {
        if (logger.isTraceEnabled()) {
            logger.trace("Loading entity: {} with id: {}", 
                entity.getClass().getName(), id);
        }
        return false;
    }
    
    @Override
    public boolean onSave(Object entity, Serializable id,
            Object[] state, String[] propertyNames, Type[] types) {
        if (logger.isDebugEnabled()) {
            logger.debug("Saving entity: {} with id: {}", 
                entity.getClass().getName(), id);
        }
        return false;
    }
    
    @Override
    public void onDelete(Object entity, Serializable id,
            Object[] state, String[] propertyNames, Type[] types) {
        if (logger.isDebugEnabled()) {
            logger.debug("Deleting entity: {} with id: {}", 
                entity.getClass().getName(), id);
        }
    }
    
    private String normalizeSql(String sql) {
        // Replace literal values with placeholders
        return sql.replaceAll("'[^']*'", "?")
                 .replaceAll("\\d+", "?")
                 .replaceAll("\\s+", " ")
                 .trim();
    }
} 