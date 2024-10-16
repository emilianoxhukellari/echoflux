package transcribe.core.core.executor;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.concurrent.DelegatingSecurityContextExecutorService;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Configuration
public class ExecutorConfiguration {

    @Bean
    public ExecutorService executorService() {
        return Executors.newVirtualThreadPerTaskExecutor();
    }

    @Bean
    public DelegatingSecurityContextExecutorService delegatingSecurityContextExecutorService(ExecutorService executorService) {
        return new DelegatingSecurityContextExecutorService(executorService);
    }

}
