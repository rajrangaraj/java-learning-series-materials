/**
 * Interface for transforming data records
 */
public interface DataTransformer {
    DataRecord transform(DataRecord record) throws TransformationException;
    List<DataRecord> transformBatch(List<DataRecord> records) throws TransformationException;
    TransformationMetadata getMetadata();
} 