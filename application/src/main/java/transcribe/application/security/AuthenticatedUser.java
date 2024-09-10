package transcribe.application.security;

import com.vaadin.flow.spring.security.AuthenticationContext;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import transcribe.core.common.utils.MoreSets;
import transcribe.domain.application_user.data.ApplicationUserEntity;
import transcribe.domain.application_user.data.ApplicationUserRepository;
import transcribe.domain.application_user.data.Role;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class AuthenticatedUser {

    private final ApplicationUserRepository applicationUserRepository;
    private final AuthenticationContext authenticationContext;

    public Optional<ApplicationUserEntity> get() {
        return authenticationContext.getAuthenticatedUser(UserDetails.class)
                .flatMap(ud -> applicationUserRepository.findByUsername(ud.getUsername()));
    }

    public void logout() {
        authenticationContext.logout();
    }

    public boolean hasRole(Role role) {
        return get().map(u -> MoreSets.contains(u.getRoles(), role))
                .orElse(false);
    }

    public boolean isAdmin() {
        return hasRole(Role.ADMIN);
    }

}
