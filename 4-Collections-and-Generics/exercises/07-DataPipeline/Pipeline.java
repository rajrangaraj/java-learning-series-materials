/**
 * Generic data processing pipeline
 */
public class Pipeline<I, O> {
    private final List<Stage<?, ?>> stages;
    private final PipelineStats stats;
    private final int bufferSize;
    private final ExecutorService executor;
    
    public Pipeline(int bufferSize, int threadCount) {
        this.stages = new ArrayList<>();
        this.stats = new PipelineStats();
        this.bufferSize = bufferSize;
        this.executor = Executors.newFixedThreadPool(threadCount);
    }
    
    public <T> Pipeline<I, O> addStage(Stage<?, T> stage) {
        stages.add(stage);
        return this;
    }
    
    @SuppressWarnings("unchecked")
    public List<O> process(List<I> input) {
        BlockingQueue<Object> currentBuffer = new ArrayBlockingQueue<>(bufferSize);
        
        // Add input to first buffer
        input.forEach(item -> {
            try {
                currentBuffer.put(item);
                stats.recordInput();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });
        
        // Process through stages
        for (Stage<?, ?> stage : stages) {
            BlockingQueue<Object> nextBuffer = new ArrayBlockingQueue<>(bufferSize);
            CompletableFuture.runAsync(() -> 
                processStage(stage, currentBuffer, nextBuffer), executor);
            currentBuffer = nextBuffer;
        }
        
        // Collect results
        List<O> results = new ArrayList<>();
        while (!currentBuffer.isEmpty()) {
            results.add((O) currentBuffer.poll());
            stats.recordOutput();
        }
        
        return results;
    }
    
    @SuppressWarnings("unchecked")
    private <IN, OUT> void processStage(Stage<IN, OUT> stage, 
                                      BlockingQueue<Object> input,
                                      BlockingQueue<Object> output) {
        while (!input.isEmpty()) {
            try {
                IN item = (IN) input.take();
                try {
                    OUT result = stage.process(item);
                    if (result != null) {
                        output.put(result);
                        stats.recordProcessed();
                    }
                } catch (Exception e) {
                    stats.recordError();
                    stage.handleError(item, e);
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
    }
    
    public PipelineStats getStats() {
        return stats;
    }
    
    public void shutdown() {
        executor.shutdown();
    }
} 