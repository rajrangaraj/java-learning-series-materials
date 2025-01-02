/**
 * Interface for writing processed records to a destination
 */
public interface DataSink extends AutoCloseable {
    void write(DataRecord record) throws SinkException;
    void writeBatch(List<DataRecord> records) throws SinkException;
    void flush() throws SinkException;
    SinkMetadata getMetadata();
} 