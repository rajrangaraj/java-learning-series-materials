/**
 * A monad for handling both success and failure cases functionally
 */
public class Result<T> {
    private final T value;
    private final Throwable error;
    
    private Result(T value, Throwable error) {
        this.value = value;
        this.error = error;
    }
    
    public static <T> Result<T> success(T value) {
        return new Result<>(value, null);
    }
    
    public static <T> Result<T> failure(Throwable error) {
        return new Result<>(null, error);
    }
    
    public boolean isSuccess() {
        return error == null;
    }
    
    public boolean isFailure() {
        return error != null;
    }
    
    public T get() throws Throwable {
        if (isFailure()) {
            throw error;
        }
        return value;
    }
    
    public T getOrElse(T defaultValue) {
        return isSuccess() ? value : defaultValue;
    }
    
    public T getOrElseGet(Supplier<T> supplier) {
        return isSuccess() ? value : supplier.get();
    }
    
    public <U> Result<U> map(Function<T, U> mapper) {
        if (isFailure()) {
            return failure(error);
        }
        try {
            return success(mapper.apply(value));
        } catch (Exception e) {
            return failure(e);
        }
    }
    
    public <U> Result<U> flatMap(Function<T, Result<U>> mapper) {
        if (isFailure()) {
            return failure(error);
        }
        try {
            return mapper.apply(value);
        } catch (Exception e) {
            return failure(e);
        }
    }
    
    public Result<T> recover(Function<Throwable, T> recovery) {
        if (isSuccess()) {
            return this;
        }
        try {
            return success(recovery.apply(error));
        } catch (Exception e) {
            return failure(e);
        }
    }
    
    public Result<T> recoverWith(Function<Throwable, Result<T>> recovery) {
        if (isSuccess()) {
            return this;
        }
        try {
            return recovery.apply(error);
        } catch (Exception e) {
            return failure(e);
        }
    }
    
    public void forEach(Consumer<T> consumer) {
        if (isSuccess()) {
            consumer.accept(value);
        }
    }
    
    public Result<T> peek(Consumer<T> consumer) {
        if (isSuccess()) {
            consumer.accept(value);
        }
        return this;
    }
} 