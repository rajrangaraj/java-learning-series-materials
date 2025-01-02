/**
 * Interface for handling different types of errors in the data pipeline
 */
public interface ErrorHandler {
    void handleSourceError(SourceUnavailableException e);
    void handleTransformationError(DataRecord record, TransformationException e);
    void handleValidationError(DataRecord record, ValidationResult validation);
    void handleSinkError(DataRecord record, SinkException e);
    void handleUnexpectedError(Exception e);
    void handleHighErrorRate(ProcessingStats stats);
} 