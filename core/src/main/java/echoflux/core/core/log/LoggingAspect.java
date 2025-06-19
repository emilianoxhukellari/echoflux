package echoflux.core.core.log;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

@Aspect
@Component
@Slf4j
public class LoggingAspect {

    @Around("@annotation(echoflux.core.core.log.LoggedMethodExecution)")
    public Object logMethodExecution(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
        var methodSignature = (MethodSignature) proceedingJoinPoint.getSignature();
        var method = methodSignature.getMethod();
        var logMethodExecution = method.getAnnotation(LoggedMethodExecution.class);

        var methodName = methodSignature.toShortString();
        var args = proceedingJoinPoint.getArgs();

        if (logMethodExecution.logArgs()){
            log.trace("Entered [{}] with args [{}]", methodName, args);
        } else {
            log.trace("Entered [{}]", methodName);
        }

        var start = System.nanoTime();
        Object result;
        try {
            result = proceedingJoinPoint.proceed();
        } catch (Throwable e) {
            if (logMethodExecution.logError()) {
                log.error("Error on [{}] error: [{}]", methodName, e.getMessage());
            }
            throw e;
        }
        var executionTime = (System.nanoTime() - start) / 1_000_000;

        if (logMethodExecution.logExecutionTime()) {
            log.trace("Executed [{}] in [{}]ms", methodName, executionTime);
        }

        if (logMethodExecution.logReturn()) {
            log.trace("[{}] exited returning [{}]", methodName, result);
        } else {
            log.trace("[{}] exited", methodName);
        }

        return result;
    }

}
