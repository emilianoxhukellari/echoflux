package transcribe.core.core.executor;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.concurrent.DelegatingSecurityContextExecutorService;
import transcribe.core.core.qualifier.Qualifiers;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Configuration
public class ExecutorConfiguration {

    @Bean(Qualifiers.VIRTUAL_THREAD_EXECUTOR)
    public ExecutorService executorService() {
        return Executors.newVirtualThreadPerTaskExecutor();
    }

    @Bean(Qualifiers.DELEGATING_SECURITY_VIRTUAL_THREAD_EXECUTOR)
    public DelegatingSecurityContextExecutorService delegatingSecurityContextExecutorService(
            @Qualifier(Qualifiers.VIRTUAL_THREAD_EXECUTOR) ExecutorService executorService
    ) {
        return new DelegatingSecurityContextExecutorService(executorService);
    }

}
