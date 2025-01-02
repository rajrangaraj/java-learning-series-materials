/**
 * Represents the processing state of a data record
 */
public enum ProcessingState {
    NEW,
    TRANSFORMING,
    TRANSFORMED,
    VALIDATING,
    VALIDATED,
    WRITING,
    COMPLETED,
    ERROR,
    RETRY
} 