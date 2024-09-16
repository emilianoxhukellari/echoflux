package transcribe.core.core.log;

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

    private static final String OBSERVED_ENTER = "[OBSERVED ENTER]";
    private static final String OBSERVED_EXECUTION_TIME = "[OBSERVED EXECUTION TIME]";
    private static final String OBSERVED_EXIT = "[OBSERVED EXIT]";
    private static final String OBSERVED_ERROR = "[OBSERVED ERROR]";

    @Around("@annotation(transcribe.core.core.log.LoggedMethodExecution)")
    public Object logMethodExecution(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
        var methodSignature = (MethodSignature) proceedingJoinPoint.getSignature();
        var method = methodSignature.getMethod();
        var logMethodExecution = method.getAnnotation(LoggedMethodExecution.class);

        var methodName = methodSignature.toShortString();
        var args = proceedingJoinPoint.getArgs();

        if (logMethodExecution.logArgs()){
            log.info("{} Method [{}] with args [{}]", OBSERVED_ENTER, methodName, args);
        } else {
            log.info("{} Method [{}]", OBSERVED_ENTER, methodName);
        }

        var start = System.nanoTime();
        Object result;
        try {
            result = proceedingJoinPoint.proceed();
        } catch (Throwable e) {
            if (logMethodExecution.logException()) {
                log.error("{} Method [{}] with error [{}]", OBSERVED_ERROR, methodName, e.getMessage());
            }
            throw e;
        }
        var executionTime = (System.nanoTime() - start) / 1_000_000;

        if (logMethodExecution.logExecutionTime()) {
            log.info("{} Method [{}] executed in [{}]ms", OBSERVED_EXECUTION_TIME, methodName, executionTime);
        }

        if (logMethodExecution.logReturn()) {
            log.info("{} Method [{}] returning [{}]", OBSERVED_EXIT, methodName, result);
        } else {
            log.info("{} Method [{}]", OBSERVED_EXIT, methodName);
        }

        return result;
    }

}
