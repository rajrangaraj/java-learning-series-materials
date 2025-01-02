/**
 * Custom security event logger for Spring Security
 */
@Component
public class SecurityAuditLogger implements ApplicationListener<AbstractAuthenticationEvent> {
    private static final Logger logger = LoggerFactory.getLogger(SecurityAuditLogger.class);
    private final ObjectMapper objectMapper;
    private final SecurityEventRepository eventRepository;
    private final NotificationService notificationService;
    
    public SecurityAuditLogger(ObjectMapper objectMapper,
                             SecurityEventRepository eventRepository,
                             NotificationService notificationService) {
        this.objectMapper = objectMapper;
        this.eventRepository = eventRepository;
        this.notificationService = notificationService;
    }
    
    @Override
    public void onApplicationEvent(AbstractAuthenticationEvent event) {
        try {
            SecurityEvent securityEvent = createSecurityEvent(event);
            
            // Log the event
            logSecurityEvent(securityEvent);
            
            // Store in database
            eventRepository.save(securityEvent);
            
            // Check if notification is needed
            if (requiresNotification(securityEvent)) {
                notificationService.sendSecurityAlert(securityEvent);
            }
            
        } catch (Exception e) {
            logger.error("Failed to process security event", e);
        }
    }
    
    @EventListener
    public void handleAuthenticationSuccess(AuthenticationSuccessEvent event) {
        UserDetails user = (UserDetails) event.getAuthentication().getPrincipal();
        
        MDC.put("username", user.getUsername());
        MDC.put("roles", String.join(",", 
            user.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList())
        ));
        
        try {
            logger.info("User logged in successfully");
            
            Map<String, Object> details = new HashMap<>();
            details.put("timestamp", LocalDateTime.now());
            details.put("ip", getClientIP());
            details.put("userAgent", getClientUserAgent());
            
            logSuccessfulLogin(user.getUsername(), details);
            
        } finally {
            MDC.remove("username");
            MDC.remove("roles");
        }
    }
    
    @EventListener
    public void handleAuthenticationFailure(AuthenticationFailureEvent event) {
        String username = event.getAuthentication().getName();
        Exception exception = event.getException();
        
        MDC.put("username", username);
        MDC.put("errorType", exception.getClass().getSimpleName());
        
        try {
            logger.warn("Authentication failed for user");
            
            Map<String, Object> details = new HashMap<>();
            details.put("timestamp", LocalDateTime.now());
            details.put("ip", getClientIP());
            details.put("userAgent", getClientUserAgent());
            details.put("errorMessage", exception.getMessage());
            
            logFailedLogin(username, details);
            
            // Check for brute force attempts
            checkBruteForceAttempts(username);
            
        } finally {
            MDC.remove("username");
            MDC.remove("errorType");
        }
    }
    
    private SecurityEvent createSecurityEvent(AbstractAuthenticationEvent event) {
        SecurityEvent securityEvent = new SecurityEvent();
        securityEvent.setTimestamp(LocalDateTime.now());
        securityEvent.setEventType(determineEventType(event));
        securityEvent.setUsername(event.getAuthentication().getName());
        securityEvent.setDetails(extractEventDetails(event));
        return securityEvent;
    }
    
    private void logSecurityEvent(SecurityEvent event) {
        try {
            String eventJson = objectMapper.writeValueAsString(event);
            
            switch (event.getSeverity()) {
                case HIGH:
                    logger.error(MarkerFactory.getMarker("SECURITY_HIGH"),
                        "Security event: {}", eventJson);
                    break;
                case MEDIUM:
                    logger.warn(MarkerFactory.getMarker("SECURITY_MEDIUM"),
                        "Security event: {}", eventJson);
                    break;
                default:
                    logger.info(MarkerFactory.getMarker("SECURITY"),
                        "Security event: {}", eventJson);
            }
        } catch (JsonProcessingException e) {
            logger.error("Failed to serialize security event", e);
        }
    }
    
    private boolean requiresNotification(SecurityEvent event) {
        return event.getSeverity() == Severity.HIGH ||
               event.getEventType() == SecurityEventType.UNAUTHORIZED_ACCESS ||
               event.getFailureCount() >= 3;
    }
    
    private void checkBruteForceAttempts(String username) {
        int failureCount = eventRepository.countRecentFailures(username,
            LocalDateTime.now().minusMinutes(5));
        
        if (failureCount >= 5) {
            logger.error(MarkerFactory.getMarker("SECURITY_HIGH"),
                "Possible brute force attack detected for user: {}", username);
            
            notificationService.sendBruteForceAlert(username, failureCount);
        }
    }
    
    private String getClientIP() {
        ServletRequestAttributes attrs = (ServletRequestAttributes) 
            RequestContextHolder.getRequestAttributes();
        if (attrs != null) {
            HttpServletRequest request = attrs.getRequest();
            return request.getRemoteAddr();
        }
        return "unknown";
    }
    
    private String getClientUserAgent() {
        ServletRequestAttributes attrs = (ServletRequestAttributes) 
            RequestContextHolder.getRequestAttributes();
        if (attrs != null) {
            HttpServletRequest request = attrs.getRequest();
            return request.getHeader("User-Agent");
        }
        return "unknown";
    }
} 