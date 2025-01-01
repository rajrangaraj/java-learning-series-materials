/**
 * Generic transformation stage
 */
public class TransformStage<I, O> implements Stage<I, O> {
    private final Function<I, O> transformer;
    private final Predicate<I> filter;
    
    public TransformStage(Function<I, O> transformer) {
        this(transformer, input -> true);
    }
    
    public TransformStage(Function<I, O> transformer, Predicate<I> filter) {
        this.transformer = transformer;
        this.filter = filter;
    }
    
    @Override
    public O process(I input) throws Exception {
        return filter.test(input) ? transformer.apply(input) : null;
    }
} 