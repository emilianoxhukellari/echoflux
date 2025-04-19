package echoflux.core.core.log;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface LoggedMethodExecution {

    boolean logArgs() default true;

    boolean logReturn() default true;

    boolean logExecutionTime() default true;

    boolean logError() default true;

}
