/**
 * Annotation for marking methods to be logged
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Logged {
    LogLevel value() default LogLevel.DEBUG;
    boolean includeArgs() default true;
    boolean includeResult() default true;
    boolean measureTime() default true;
} 