/**
 * Tests for the SchemaDataValidator implementation
 */
public class SchemaDataValidatorTest {
    private SchemaDataValidator validator;
    
    @BeforeEach
    void setUp() {
        validator = new SchemaDataValidator(ValidationLevel.NORMAL);
    }
    
    @Test
    void testValidString() {
        DataRecord record = createRecord("string", "valid value");
        ValidationResult result = validator.validate(record);
        assertTrue(result.isValid());
    }
    
    @Test
    void testInvalidString() {
        DataRecord record = createRecord("string", "");
        ValidationResult result = validator.validate(record);
        assertFalse(result.isValid());
        assertTrue(result.getErrors().containsKey("string"));
    }
    
    @Test
    void testValidNumber() {
        DataRecord record = createRecord("number", 42.0);
        ValidationResult result = validator.validate(record);
        assertTrue(result.isValid());
    }
    
    @Test
    void testInvalidNumber() {
        DataRecord record = createRecord("number", 1000000.0);
        ValidationResult result = validator.validate(record);
        assertFalse(result.isValid());
        assertTrue(result.getErrors().containsKey("number"));
    }
    
    @Test
    void testValidEmail() {
        DataRecord record = createRecord("email", 
            "test@example.com");
        ValidationResult result = validator.validate(record);
        assertTrue(result.isValid());
    }
    
    @Test
    void testInvalidEmail() {
        DataRecord record = createRecord("email", 
            "invalid-email");
        ValidationResult result = validator.validate(record);
        assertFalse(result.isValid());
        assertTrue(result.getErrors().containsKey("email"));
    }
    
    @Test
    void testStrictValidation() {
        validator.setValidationLevel(ValidationLevel.STRICT);
        
        Map<String, Object> data = new HashMap<>();
        data.put("unknown", "value");
        DataRecord record = new DataRecord("test", data);
        
        ValidationResult result = validator.validate(record);
        assertFalse(result.isValid());
        assertTrue(result.getErrors().containsKey("unknown"));
    }
    
    @Test
    void testLenientValidation() {
        validator.setValidationLevel(ValidationLevel.LENIENT);
        
        Map<String, Object> data = new HashMap<>();
        data.put("unknown", "value");
        DataRecord record = new DataRecord("test", data);
        
        ValidationResult result = validator.validate(record);
        assertTrue(result.isValid());
    }
    
    private DataRecord createRecord(String field, Object value) {
        Map<String, Object> data = new HashMap<>();
        data.put(field, value);
        return new DataRecord("test", data);
    }
} 