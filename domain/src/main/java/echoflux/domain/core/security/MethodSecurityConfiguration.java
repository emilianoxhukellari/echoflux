package echoflux.domain.core.security;

import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.expression.method.MethodSecurityExpressionHandler;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;

@Configuration
@EnableMethodSecurity
public class MethodSecurityConfiguration {

    @Bean
    MethodSecurityExpressionHandler methodSecurityExpressionHandler(ApplicationContext applicationContext) {
        var handler = new RequiredPermissionExpressionHandler();
        handler.setApplicationContext(applicationContext);

        return handler;
    }

}
