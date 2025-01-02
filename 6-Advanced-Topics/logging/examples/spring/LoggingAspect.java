/**
 * AOP aspect for method logging
 */
@Aspect
@Component
public class LoggingAspect {
    private static final Logger logger = LoggerFactory.getLogger(LoggingAspect.class);
    
    @Around("@annotation(Logged)")
    public Object logMethod(ProceedingJoinPoint joinPoint) throws Throwable {
        String methodName = joinPoint.getSignature().getName();
        String className = joinPoint.getTarget().getClass().getName();
        
        MDC.put("className", className);
        MDC.put("methodName", methodName);
        
        try {
            // Log method entry
            if (logger.isDebugEnabled()) {
                logger.debug("Entering method {} with parameters: {}",
                    methodName, Arrays.toString(joinPoint.getArgs()));
            }
            
            long startTime = System.currentTimeMillis();
            Object result = joinPoint.proceed();
            long endTime = System.currentTimeMillis();
            
            // Log method exit
            if (logger.isDebugEnabled()) {
                logger.debug("Exiting method {} with result: {}. Execution time: {}ms",
                    methodName, result, (endTime - startTime));
            }
            
            return result;
            
        } catch (Exception e) {
            logger.error("Error in method {}: {}", methodName, e.getMessage(), e);
            throw e;
        } finally {
            MDC.remove("className");
            MDC.remove("methodName");
        }
    }
    
    @AfterThrowing(pointcut = "execution(* com.example..*.*(..))", throwing = "ex")
    public void logException(JoinPoint joinPoint, Exception ex) {
        logger.error("Exception in {}.{}: {}",
            joinPoint.getTarget().getClass().getName(),
            joinPoint.getSignature().getName(),
            ex.getMessage(),
            ex);
    }
} 