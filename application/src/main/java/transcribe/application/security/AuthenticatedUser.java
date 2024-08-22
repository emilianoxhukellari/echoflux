package transcribe.application.security;

import com.vaadin.flow.spring.security.AuthenticationContext;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import transcribe.domain.application_user.data.ApplicationUserEntity;
import transcribe.domain.application_user.data.ApplicationUserRepository;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class AuthenticatedUser {

    private final ApplicationUserRepository applicationUserRepository;
    private final AuthenticationContext authenticationContext;

    @Transactional
    public Optional<ApplicationUserEntity> get() {
        return authenticationContext.getAuthenticatedUser(UserDetails.class)
                .flatMap(ud -> applicationUserRepository.findByUsername(ud.getUsername()));
    }

    public void logout() {
        authenticationContext.logout();
    }

}
