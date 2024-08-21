package transcribe.core.common.log;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import transcribe.core.common.utils.RunnableUtils;

@Aspect
@Component
@Slf4j
public class LoggingAspect {

    private static final String OBSERVED_ENTER = "[OBSERVED ENTER]";
    private static final String OBSERVED_EXECUTION_TIME = "[OBSERVED EXECUTION TIME]";
    private static final String OBSERVED_EXIT = "[OBSERVED EXIT]";
    private static final String OBSERVED_ERROR = "[OBSERVED ERROR]";

    @Around("@annotation(transcribe.core.common.log.LoggedMethodExecution)")
    public Object logMethodExecution(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
        var methodSignature = (MethodSignature) proceedingJoinPoint.getSignature();
        var method = methodSignature.getMethod();
        var logMethodExecution = method.getAnnotation(LoggedMethodExecution.class);

        var methodName = methodSignature.toShortString();
        var args = proceedingJoinPoint.getArgs();

        RunnableUtils.runIfElse(
            logMethodExecution.logArgs(),
            () -> log.info("{} Method [{}] with args [{}]", OBSERVED_ENTER, methodName, args),
            () -> log.info("{} Method [{}]", OBSERVED_ENTER, methodName)
        );

        var start = System.nanoTime();
        Object result;
        try {
            result = proceedingJoinPoint.proceed();
        } catch (Throwable e) {
            RunnableUtils.runIf(
                logMethodExecution.logException(),
                () -> log.error("{} Method [{}] with error [{}]", OBSERVED_ERROR, methodName, e.getMessage())
            );
            throw e;
        }
        var executionTime = (System.nanoTime() - start) / 1_000_000;

        RunnableUtils.runIf(
            logMethodExecution.logExecutionTime(),
            () -> log.info("{} Method [{}] executed in [{}]ms", OBSERVED_EXECUTION_TIME, methodName, executionTime)
        );

        RunnableUtils.runIfElse(
            logMethodExecution.logReturn(),
            () -> log.info("{} Method [{}] returning [{}]", OBSERVED_EXIT, methodName, result),
            () -> log.info("{} Method [{}]", OBSERVED_EXIT, methodName)
        );

        return result;
    }

}
