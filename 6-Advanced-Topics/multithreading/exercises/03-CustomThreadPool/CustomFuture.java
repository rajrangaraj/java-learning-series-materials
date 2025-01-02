/**
 * Future implementation for custom thread pool
 */
public class CustomFuture<T> implements RunnableFuture<T> {
    private final Object lock = new Object();
    private final Callable<T> callable;
    private volatile T result;
    private volatile Throwable exception;
    private volatile boolean done;
    private volatile boolean cancelled;
    
    public CustomFuture(Callable<T> callable) {
        this.callable = callable;
    }
    
    public CustomFuture(Runnable runnable, T result) {
        this.callable = () -> {
            runnable.run();
            return result;
        };
    }
    
    @Override
    public void run() {
        if (isDone() || isCancelled()) {
            return;
        }
        
        try {
            T value = callable.call();
            set(value);
        } catch (Throwable t) {
            setException(t);
        }
    }
    
    @Override
    public boolean cancel(boolean mayInterruptIfRunning) {
        synchronized (lock) {
            if (done) {
                return false;
            }
            cancelled = true;
            done = true;
            lock.notifyAll();
        }
        return true;
    }
    
    @Override
    public boolean isCancelled() {
        return cancelled;
    }
    
    @Override
    public boolean isDone() {
        return done;
    }
    
    @Override
    public T get() throws InterruptedException, ExecutionException {
        synchronized (lock) {
            while (!isDone()) {
                lock.wait();
            }
            return getResult();
        }
    }
    
    @Override
    public T get(long timeout, TimeUnit unit) 
            throws InterruptedException, ExecutionException, TimeoutException {
        synchronized (lock) {
            long remaining = unit.toNanos(timeout);
            long deadline = System.nanoTime() + remaining;
            
            while (!isDone() && remaining > 0) {
                TimeUnit.NANOSECONDS.timedWait(lock, remaining);
                remaining = deadline - System.nanoTime();
            }
            
            if (!isDone()) {
                throw new TimeoutException();
            }
            
            return getResult();
        }
    }
    
    private void set(T value) {
        synchronized (lock) {
            if (done) return;
            result = value;
            done = true;
            lock.notifyAll();
        }
    }
    
    private void setException(Throwable t) {
        synchronized (lock) {
            if (done) return;
            exception = t;
            done = true;
            lock.notifyAll();
        }
    }
    
    private T getResult() throws ExecutionException {
        if (cancelled) {
            throw new CancellationException();
        }
        if (exception != null) {
            throw new ExecutionException(exception);
        }
        return result;
    }
} 