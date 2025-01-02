/**
 * Implementation of DataTransformer for JSON data
 */
public class JsonDataTransformer implements DataTransformer {
    private static final Logger logger = LoggerFactory.getLogger(JsonDataTransformer.class);
    private final Map<String, TransformationRule> rules;
    private final ObjectMapper objectMapper;
    
    public JsonDataTransformer() {
        this.rules = new HashMap<>();
        this.objectMapper = new ObjectMapper();
        initializeDefaultRules();
    }
    
    private void initializeDefaultRules() {
        rules.put("uppercase", value -> 
            value instanceof String ? ((String) value).toUpperCase() : value);
        rules.put("lowercase", value -> 
            value instanceof String ? ((String) value).toLowerCase() : value);
        rules.put("trim", value -> 
            value instanceof String ? ((String) value).trim() : value);
        rules.put("number", value -> {
            if (value instanceof String) {
                try {
                    return Double.parseDouble((String) value);
                } catch (NumberFormatException e) {
                    throw new TransformationException(
                        "Failed to convert to number: " + value);
                }
            }
            return value;
        });
    }
    
    @Override
    public DataRecord transform(DataRecord record) 
            throws TransformationException {
        try {
            record.setState(ProcessingState.TRANSFORMING);
            
            DataRecord transformed = record.copy();
            for (Map.Entry<String, Object> entry : 
                    record.getData().entrySet()) {
                String field = entry.getKey();
                Object value = entry.getValue();
                
                TransformationRule rule = rules.get(field);
                if (rule != null) {
                    try {
                        Object transformedValue = rule.apply(value);
                        transformed.setValue(field, transformedValue);
                    } catch (Exception e) {
                        throw new TransformationException(
                            "Failed to apply transformation for field: " + 
                            field, e);
                    }
                }
            }
            
            transformed.setState(ProcessingState.TRANSFORMED);
            return transformed;
            
        } catch (Exception e) {
            record.setState(ProcessingState.ERROR);
            throw new TransformationException(
                "Failed to transform record: " + record.getId(), e);
        }
    }
    
    @Override
    public List<DataRecord> transformBatch(List<DataRecord> records) 
            throws TransformationException {
        return records.stream()
            .map(this::transform)
            .collect(Collectors.toList());
    }
    
    @Override
    public TransformationMetadata getMetadata() {
        return new TransformationMetadata(
            "JSON",
            new ArrayList<>(rules.keySet()),
            Collections.emptyMap()
        );
    }
    
    @FunctionalInterface
    private interface TransformationRule {
        Object apply(Object value) throws Exception;
    }
} 