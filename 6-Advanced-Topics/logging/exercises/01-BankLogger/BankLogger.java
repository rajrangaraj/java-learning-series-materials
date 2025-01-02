/**
 * Custom logging framework for banking operations with security and audit features
 */
public class BankLogger {
    private static final Logger logger = LoggerFactory.getLogger(BankLogger.class);
    private final String bankId;
    private final SecurityLevel securityLevel;
    private final AuditLogger auditLogger;
    private final TransactionLogger transactionLogger;
    private final AlertLogger alertLogger;
    
    public BankLogger(String bankId, SecurityLevel securityLevel) {
        this.bankId = bankId;
        this.securityLevel = securityLevel;
        this.auditLogger = new AuditLogger(bankId);
        this.transactionLogger = new TransactionLogger(bankId);
        this.alertLogger = new AlertLogger(bankId);
        
        MDC.put("bankId", bankId);
        MDC.put("securityLevel", securityLevel.name());
    }
    
    public void logTransaction(Transaction transaction) {
        try {
            // Validate transaction before logging
            validateTransaction(transaction);
            
            // Enrich transaction with metadata
            enrichTransactionData(transaction);
            
            // Log based on transaction type and amount
            if (transaction.getAmount().compareTo(BigDecimal.valueOf(10000)) > 0) {
                logLargeTransaction(transaction);
            } else {
                logRegularTransaction(transaction);
            }
            
            // Record for audit
            auditLogger.recordTransaction(transaction);
            
        } catch (Exception e) {
            handleLoggingError("Transaction logging failed", e, transaction);
        }
    }
    
    public void logSecurityEvent(SecurityEvent event) {
        try {
            MDC.put("eventId", event.getId());
            MDC.put("eventType", event.getType().name());
            
            // Log based on security level and event severity
            if (event.getSeverity() == Severity.HIGH) {
                logHighSeverityEvent(event);
            } else {
                logRegularSecurityEvent(event);
            }
            
            // Check for security alerts
            if (shouldTriggerAlert(event)) {
                alertLogger.triggerSecurityAlert(event);
            }
            
            // Record for audit
            auditLogger.recordSecurityEvent(event);
            
        } catch (Exception e) {
            handleLoggingError("Security event logging failed", e, event);
        } finally {
            MDC.remove("eventId");
            MDC.remove("eventType");
        }
    }
    
    public void logSystemEvent(SystemEvent event) {
        try {
            MDC.put("component", event.getComponent());
            MDC.put("operation", event.getOperation());
            
            // Log based on event type
            switch (event.getType()) {
                case STARTUP:
                case SHUTDOWN:
                    logCriticalSystemEvent(event);
                    break;
                case MAINTENANCE:
                    logMaintenanceEvent(event);
                    break;
                default:
                    logRegularSystemEvent(event);
            }
            
            // Record for audit if necessary
            if (event.requiresAudit()) {
                auditLogger.recordSystemEvent(event);
            }
            
        } catch (Exception e) {
            handleLoggingError("System event logging failed", e, event);
        } finally {
            MDC.remove("component");
            MDC.remove("operation");
        }
    }
    
    private void validateTransaction(Transaction transaction) {
        if (transaction == null) {
            throw new IllegalArgumentException("Transaction cannot be null");
        }
        if (transaction.getAmount() == null || transaction.getAmount().compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Invalid transaction amount");
        }
    }
    
    private void enrichTransactionData(Transaction transaction) {
        transaction.setLoggedAt(LocalDateTime.now());
        transaction.setBankId(bankId);
        transaction.setProcessingEnvironment(getEnvironment());
    }
    
    private void logLargeTransaction(Transaction transaction) {
        // Log with extra scrutiny for large amounts
        logger.warn(MarkerFactory.getMarker("LARGE_TRANSACTION"),
            "Large transaction detected: {} for account {}",
            transaction.getAmount(), maskAccountNumber(transaction.getAccountNumber()));
        
        // Additional logging for compliance
        if (securityLevel == SecurityLevel.HIGH) {
            transactionLogger.logDetailedTransaction(transaction);
        }
    }
    
    private void logRegularTransaction(Transaction transaction) {
        logger.info("Transaction processed: {} for account {}",
            transaction.getAmount(), maskAccountNumber(transaction.getAccountNumber()));
        
        transactionLogger.logBasicTransaction(transaction);
    }
    
    private void logHighSeverityEvent(SecurityEvent event) {
        logger.error(MarkerFactory.getMarker("SECURITY_HIGH"),
            "High severity security event: {} in {}",
            event.getType(), event.getLocation());
        
        // Notify security team
        alertLogger.notifySecurityTeam(event);
    }
    
    private void logRegularSecurityEvent(SecurityEvent event) {
        logger.info(MarkerFactory.getMarker("SECURITY"),
            "Security event: {} with severity {}",
            event.getType(), event.getSeverity());
    }
    
    private void logCriticalSystemEvent(SystemEvent event) {
        logger.warn(MarkerFactory.getMarker("SYSTEM_CRITICAL"),
            "Critical system event: {} in component {}",
            event.getOperation(), event.getComponent());
        
        // Notify system administrators
        alertLogger.notifySystemAdmins(event);
    }
    
    private void logMaintenanceEvent(SystemEvent event) {
        logger.info(MarkerFactory.getMarker("SYSTEM_MAINTENANCE"),
            "Maintenance event: {} scheduled for component {}",
            event.getOperation(), event.getComponent());
    }
    
    private void logRegularSystemEvent(SystemEvent event) {
        logger.debug("System event: {} in component {}",
            event.getOperation(), event.getComponent());
    }
    
    private boolean shouldTriggerAlert(SecurityEvent event) {
        return event.getSeverity() == Severity.HIGH ||
               event.getType() == SecurityEventType.UNAUTHORIZED_ACCESS ||
               (securityLevel == SecurityLevel.HIGH && 
                event.getType() == SecurityEventType.SUSPICIOUS_ACTIVITY);
    }
    
    private void handleLoggingError(String message, Exception e, Object context) {
        logger.error("Logging error: {} for context: {}", message, context, e);
        
        // Record error for audit
        auditLogger.recordError(message, e, context);
        
        // Trigger alert for logging system failure
        alertLogger.triggerSystemAlert(message, e);
    }
    
    private String maskAccountNumber(String accountNumber) {
        if (accountNumber == null || accountNumber.length() < 4) {
            return "****";
        }
        return "****" + accountNumber.substring(accountNumber.length() - 4);
    }
    
    private String getEnvironment() {
        return System.getProperty("bank.environment", "PRODUCTION");
    }
    
    public void close() {
        try {
            auditLogger.close();
            transactionLogger.close();
            alertLogger.close();
            MDC.clear();
        } catch (Exception e) {
            logger.error("Error closing loggers", e);
        }
    }
} 