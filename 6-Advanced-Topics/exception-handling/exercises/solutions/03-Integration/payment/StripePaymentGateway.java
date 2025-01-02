/**
 * Stripe implementation of PaymentGateway
 */
public class StripePaymentGateway implements PaymentGateway {
    private static final Logger logger = LoggerFactory.getLogger(StripePaymentGateway.class);
    
    private final Stripe stripeClient;
    private final MetricsCollector metrics;
    private final Map<String, PaymentTransaction> transactionCache;
    
    public StripePaymentGateway(Stripe stripeClient, MetricsCollector metrics) {
        this.stripeClient = stripeClient;
        this.metrics = metrics;
        this.transactionCache = new ConcurrentHashMap<>();
    }
    
    @Override
    public PaymentTransaction createTransaction(PaymentDetails details) 
            throws PaymentGatewayException {
        try {
            metrics.startTimer("stripe.create_transaction");
            
            PaymentMethod paymentMethod = createPaymentMethod(details);
            PaymentIntent intent = createPaymentIntent(details, paymentMethod);
            
            PaymentTransaction transaction = new PaymentTransaction(
                intent.getId(), details);
            transactionCache.put(transaction.getTransactionId(), transaction);
            
            return transaction;
            
        } catch (StripeException e) {
            handleStripeError(e);
            throw translateStripeException(e);
        } finally {
            metrics.stopTimer("stripe.create_transaction");
        }
    }
    
    @Override
    public PaymentResult processTransaction(PaymentTransaction transaction) 
            throws PaymentGatewayException {
        try {
            metrics.startTimer("stripe.process_transaction");
            
            PaymentIntent intent = stripeClient.paymentIntents()
                .confirm(transaction.getTransactionId());
                
            if (requiresAction(intent)) {
                return handleActionRequired(intent);
            }
            
            return createPaymentResult(intent);
            
        } catch (StripeException e) {
            handleStripeError(e);
            throw translateStripeException(e);
        } finally {
            metrics.stopTimer("stripe.process_transaction");
        }
    }
    
    private PaymentMethod createPaymentMethod(PaymentDetails details) 
            throws StripeException {
        Map<String, Object> params = new HashMap<>();
        params.put("type", details.getPaymentType());
        params.put("card", details.getCardDetails());
        
        return stripeClient.paymentMethods().create(params);
    }
    
    private PaymentIntent createPaymentIntent(PaymentDetails details, 
            PaymentMethod paymentMethod) throws StripeException {
        Map<String, Object> params = new HashMap<>();
        params.put("amount", details.getAmount().multiply(BigDecimal.valueOf(100)).longValue());
        params.put("currency", details.getCurrency());
        params.put("payment_method", paymentMethod.getId());
        params.put("confirm", true);
        params.put("return_url", details.getReturnUrl());
        
        return stripeClient.paymentIntents().create(params);
    }
    
    private boolean requiresAction(PaymentIntent intent) {
        return "requires_action".equals(intent.getStatus()) ||
               "requires_source_action".equals(intent.getStatus());
    }
    
    private PaymentResult handleActionRequired(PaymentIntent intent) {
        return PaymentResult.builder(intent.getId())
            .successful(false)
            .status(PaymentStatus.REQUIRES_ACTION)
            .metadata("action_url", intent.getNextActionUrl())
            .build();
    }
    
    private PaymentResult createPaymentResult(PaymentIntent intent) {
        boolean successful = "succeeded".equals(intent.getStatus());
        
        PaymentResult.Builder builder = PaymentResult.builder(intent.getId())
            .successful(successful)
            .status(translateStatus(intent.getStatus()));
            
        if (!successful) {
            builder.errorCode(intent.getLastPaymentError().getCode())
                  .errorMessage(intent.getLastPaymentError().getMessage());
        }
        
        return builder.build();
    }
    
    private void handleStripeError(StripeException e) {
        logger.error("Stripe error: {}", e.getMessage(), e);
        metrics.incrementCounter("stripe.error." + e.getClass().getSimpleName());
    }
    
    private PaymentGatewayException translateStripeException(StripeException e) {
        if (e instanceof CardException) {
            return new PaymentGatewayException(
                "Card was declined", "CARD_DECLINED");
        } else if (e instanceof RateLimitException) {
            return new PaymentGatewayTimeoutException(
                "Rate limit exceeded");
        } else if (e instanceof InvalidRequestException) {
            return new PaymentGatewayException(
                "Invalid payment request", "INVALID_REQUEST");
        } else if (e instanceof AuthenticationException) {
            return new PaymentGatewayException(
                "Authentication failed", "AUTH_FAILED");
        } else if (e instanceof APIConnectionException) {
            return new PaymentGatewayUnavailableException(
                "Unable to connect to Stripe");
        } else {
            return new PaymentGatewayException(
                "Unexpected payment error", "INTERNAL_ERROR");
        }
    }
    
    private PaymentStatus translateStatus(String stripeStatus) {
        switch (stripeStatus) {
            case "succeeded": return PaymentStatus.COMPLETED;
            case "requires_payment_method": return PaymentStatus.FAILED;
            case "requires_action": return PaymentStatus.PENDING;
            case "processing": return PaymentStatus.PROCESSING;
            case "canceled": return PaymentStatus.CANCELLED;
            default: return PaymentStatus.UNKNOWN;
        }
    }
} 