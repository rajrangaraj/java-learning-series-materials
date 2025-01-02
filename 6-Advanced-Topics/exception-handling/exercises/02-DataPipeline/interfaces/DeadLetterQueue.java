/**
 * Interface for handling failed records
 */
public interface DeadLetterQueue extends AutoCloseable {
    void write(DataRecord record, ErrorRecord error);
    void writeBatch(List<DataRecord> records, List<ErrorRecord> errors);
    List<DeadLetterEntry> readFailures(ErrorType type, int maxRecords);
    void markForRetry(String recordId);
    void markAsResolved(String recordId);
    long getFailureCount();
} 