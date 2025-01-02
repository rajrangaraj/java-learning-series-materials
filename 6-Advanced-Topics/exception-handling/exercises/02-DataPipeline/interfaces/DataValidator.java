/**
 * Interface for validating data records
 */
public interface DataValidator {
    ValidationResult validate(DataRecord record) throws ValidationException;
    List<ValidationResult> validateBatch(List<DataRecord> records) throws ValidationException;
    void setValidationLevel(ValidationLevel level);
    ValidationLevel getValidationLevel();
} 