/**
 * Interface for data sources that provide records for processing
 */
public interface DataSource extends AutoCloseable {
    List<DataRecord> fetchRecords() throws SourceUnavailableException;
    boolean hasMore();
    void reset() throws SourceUnavailableException;
    SourceMetadata getMetadata();
} 