/**
 * Thread-safe message queue implementation using the producer-consumer pattern
 */
public class MessageQueue<T> {
    private final Queue<T> queue;
    private final int capacity;
    private final ReentrantLock lock;
    private final Condition notFull;
    private final Condition notEmpty;
    private final AtomicInteger messageCount;
    private final QueueStats stats;
    
    public MessageQueue(int capacity) {
        this.capacity = capacity;
        this.queue = new LinkedList<>();
        this.lock = new ReentrantLock();
        this.notFull = lock.newCondition();
        this.notEmpty = lock.newCondition();
        this.messageCount = new AtomicInteger(0);
        this.stats = new QueueStats();
    }
    
    /**
     * Adds a message to the queue
     */
    public boolean offer(T message, long timeout, TimeUnit unit) 
            throws InterruptedException {
        lock.lock();
        try {
            long nanos = unit.toNanos(timeout);
            while (queue.size() == capacity) {
                if (nanos <= 0) {
                    stats.recordRejectedMessage();
                    return false;
                }
                nanos = notFull.awaitNanos(nanos);
            }
            
            queue.offer(message);
            messageCount.incrementAndGet();
            stats.recordProducedMessage();
            notEmpty.signal();
            return true;
        } finally {
            lock.unlock();
        }
    }
    
    /**
     * Retrieves and removes a message from the queue
     */
    public T poll(long timeout, TimeUnit unit) throws InterruptedException {
        lock.lock();
        try {
            long nanos = unit.toNanos(timeout);
            while (queue.isEmpty()) {
                if (nanos <= 0) {
                    stats.recordFailedPoll();
                    return null;
                }
                nanos = notEmpty.awaitNanos(nanos);
            }
            
            T message = queue.poll();
            messageCount.decrementAndGet();
            stats.recordConsumedMessage();
            notFull.signal();
            return message;
        } finally {
            lock.unlock();
        }
    }
    
    /**
     * Drains messages to the given collection
     */
    public int drainTo(Collection<? super T> collection, int maxElements) {
        lock.lock();
        try {
            int count = 0;
            while (!queue.isEmpty() && count < maxElements) {
                collection.add(queue.poll());
                messageCount.decrementAndGet();
                count++;
            }
            if (count > 0) {
                stats.recordBatchConsume(count);
                notFull.signalAll();
            }
            return count;
        } finally {
            lock.unlock();
        }
    }
    
    public int size() {
        return messageCount.get();
    }
    
    public boolean isEmpty() {
        return size() == 0;
    }
    
    public QueueStats getStats() {
        return stats;
    }
} 