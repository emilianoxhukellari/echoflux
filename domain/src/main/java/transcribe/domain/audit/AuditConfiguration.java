package transcribe.domain.audit;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.lang.NonNull;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;

import java.util.Optional;

@Configuration
@EnableJpaAuditing
@Slf4j
public class AuditConfiguration {

    //todo: fix created and updated by
    @Bean
    public AuditorAware<String> auditorAware() {

        return new AuditorAware<>() {
            @Override
            public @NonNull Optional<String> getCurrentAuditor() {
                return Optional.ofNullable(SecurityContextHolder.getContext().getAuthentication())
                        .filter(Authentication::isAuthenticated)
                        .map(Authentication::getPrincipal)
                        .map(User.class::cast)
                        .map(User::getUsername);
            }
        };
    }

}
