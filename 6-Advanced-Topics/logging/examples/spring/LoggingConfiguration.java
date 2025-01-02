/**
 * Spring configuration for logging
 */
@Configuration
@EnableAspectJAutoProxy
public class LoggingConfiguration {
    
    @Bean
    public LoggingAspect loggingAspect() {
        return new LoggingAspect();
    }
    
    @Bean
    public RequestLoggingFilter requestLoggingFilter() {
        CommonsRequestLoggingFilter filter = new CommonsRequestLoggingFilter();
        filter.setIncludeQueryString(true);
        filter.setIncludePayload(true);
        filter.setMaxPayloadLength(10000);
        filter.setIncludeHeaders(true);
        filter.setAfterMessagePrefix("REQUEST DATA: ");
        return filter;
    }
    
    @Bean
    public PerformanceMonitorInterceptor performanceMonitorInterceptor() {
        return new PerformanceMonitorInterceptor(true);
    }
    
    @Bean
    public BeanFactoryAspectJAdvisorsBuilder beanFactoryAspectJAdvisorsBuilder() {
        return new BeanFactoryAspectJAdvisorsBuilder();
    }
} 