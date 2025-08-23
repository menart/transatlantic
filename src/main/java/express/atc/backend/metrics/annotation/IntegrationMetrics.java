package express.atc.backend.metrics.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface IntegrationMetrics {
    String integrationName() default "unknown";
    String operationName() default "unknown";
    boolean logRequest() default false;
    boolean logResponse() default false;
}