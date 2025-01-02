/**
 * Custom filter for logging HTTP requests and responses
 */
@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class RequestResponseLoggingFilter extends OncePerRequestFilter {
    private static final Logger logger = LoggerFactory.getLogger(RequestResponseLoggingFilter.class);
    private final ObjectMapper objectMapper;
    private final Set<String> excludedPaths;
    private final Set<String> sensitiveHeaders;
    
    public RequestResponseLoggingFilter(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
        this.excludedPaths = Set.of("/health", "/metrics", "/favicon.ico");
        this.sensitiveHeaders = Set.of("authorization", "cookie", "x-api-key");
    }
    
    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                  HttpServletResponse response,
                                  FilterChain filterChain)
            throws ServletException, IOException {
        
        if (shouldLog(request)) {
            String requestId = UUID.randomUUID().toString();
            MDC.put("requestId", requestId);
            response.setHeader("X-Request-ID", requestId);
            
            try {
                // Log request
                logRequest(request);
                
                // Wrap response for logging
                ContentCachingResponseWrapper responseWrapper = 
                    new ContentCachingResponseWrapper(response);
                
                // Process request
                filterChain.doFilter(request, responseWrapper);
                
                // Log response
                logResponse(responseWrapper);
                
                // Copy content to original response
                responseWrapper.copyBodyToResponse();
                
            } finally {
                MDC.remove("requestId");
            }
        } else {
            filterChain.doFilter(request, response);
        }
    }
    
    private boolean shouldLog(HttpServletRequest request) {
        String path = request.getRequestURI();
        return !excludedPaths.contains(path);
    }
    
    private void logRequest(HttpServletRequest request) {
        try {
            Map<String, Object> requestData = new HashMap<>();
            requestData.put("timestamp", LocalDateTime.now());
            requestData.put("method", request.getMethod());
            requestData.put("uri", request.getRequestURI());
            requestData.put("queryString", request.getQueryString());
            requestData.put("clientIP", request.getRemoteAddr());
            requestData.put("userAgent", request.getHeader("User-Agent"));
            requestData.put("headers", getHeaders(request));
            
            // Log request body for specific content types
            String contentType = request.getContentType();
            if (contentType != null && 
                (contentType.contains("application/json") || 
                 contentType.contains("application/xml"))) {
                requestData.put("body", getRequestBody(request));
            }
            
            logger.info("Incoming request: {}", 
                objectMapper.writeValueAsString(requestData));
            
        } catch (Exception e) {
            logger.error("Failed to log request", e);
        }
    }
    
    private void logResponse(ContentCachingResponseWrapper response) {
        try {
            Map<String, Object> responseData = new HashMap<>();
            responseData.put("timestamp", LocalDateTime.now());
            responseData.put("status", response.getStatus());
            responseData.put("headers", getHeaders(response));
            
            // Log response body for specific content types
            String contentType = response.getContentType();
            if (contentType != null && 
                (contentType.contains("application/json") || 
                 contentType.contains("application/xml"))) {
                responseData.put("body", 
                    new String(response.getContentAsByteArray(), 
                        StandardCharsets.UTF_8));
            }
            
            logger.info("Outgoing response: {}", 
                objectMapper.writeValueAsString(responseData));
            
        } catch (Exception e) {
            logger.error("Failed to log response", e);
        }
    }
    
    private Map<String, String> getHeaders(HttpServletRequest request) {
        Map<String, String> headers = new HashMap<>();
        Collections.list(request.getHeaderNames()).forEach(headerName -> {
            if (!sensitiveHeaders.contains(headerName.toLowerCase())) {
                headers.put(headerName, request.getHeader(headerName));
            }
        });
        return headers;
    }
    
    private Map<String, String> getHeaders(HttpServletResponse response) {
        Map<String, String> headers = new HashMap<>();
        response.getHeaderNames().forEach(headerName -> {
            if (!sensitiveHeaders.contains(headerName.toLowerCase())) {
                headers.put(headerName, response.getHeader(headerName));
            }
        });
        return headers;
    }
    
    private String getRequestBody(HttpServletRequest request) throws IOException {
        String body = null;
        if (request instanceof ContentCachingRequestWrapper) {
            body = new String(
                ((ContentCachingRequestWrapper) request).getContentAsByteArray(),
                StandardCharsets.UTF_8);
        }
        return body;
    }
} 