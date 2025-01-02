/**
 * Custom Hibernate interceptor for logging
 */
public class HibernateLoggingInterceptor extends EmptyInterceptor {
    private static final Logger logger = LoggerFactory.getLogger(HibernateLoggingInterceptor.class);
    
    @Override
    public boolean onSave(Object entity, Serializable id, Object[] state,
            String[] propertyNames, Type[] types) {
        logger.debug("Saving entity: {} with id: {}", entity.getClass().getName(), id);
        return false;
    }
    
    @Override
    public boolean onFlushDirty(Object entity, Serializable id, Object[] currentState,
            Object[] previousState, String[] propertyNames, Type[] types) {
        logger.debug("Updating entity: {} with id: {}", entity.getClass().getName(), id);
        logStateChange(propertyNames, previousState, currentState);
        return false;
    }
    
    @Override
    public void onDelete(Object entity, Serializable id, Object[] state,
            String[] propertyNames, Type[] types) {
        logger.debug("Deleting entity: {} with id: {}", entity.getClass().getName(), id);
    }
    
    @Override
    public String onPrepareStatement(String sql) {
        logger.debug("Executing SQL: {}", sql);
        return sql;
    }
    
    private void logStateChange(String[] propertyNames, Object[] previousState,
            Object[] currentState) {
        if (previousState == null || currentState == null) {
            return;
        }
        
        for (int i = 0; i < propertyNames.length; i++) {
            if (!Objects.equals(previousState[i], currentState[i])) {
                logger.debug("Property '{}' changed from '{}' to '{}'",
                    propertyNames[i], previousState[i], currentState[i]);
            }
        }
    }
} 