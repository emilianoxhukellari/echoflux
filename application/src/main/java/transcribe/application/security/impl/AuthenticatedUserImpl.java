package transcribe.application.security.impl;

import com.vaadin.flow.spring.security.AuthenticationContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import transcribe.application.security.AuthenticatedUser;
import transcribe.core.core.utils.MoreSets;
import transcribe.domain.application_user.data.ApplicationUserEntity;
import transcribe.domain.application_user.data.ApplicationUserRepository;
import transcribe.domain.application_user.data.Role;

import java.util.Optional;

@Component
@RequiredArgsConstructor
@Slf4j
public class AuthenticatedUserImpl implements AuthenticatedUser {

    private final ApplicationUserRepository applicationUserRepository;
    private final AuthenticationContext authenticationContext;

    @Override
    public Optional<ApplicationUserEntity> find() {
        return authenticationContext.getAuthenticatedUser(UserDetails.class)
                .flatMap(ud -> applicationUserRepository.findByUsername(ud.getUsername()));
    }

    @Override
    public void logout() {
        authenticationContext.logout();
    }

    @Override
    public boolean hasRole(Role role) {
        return find().map(u -> MoreSets.contains(u.getRoles(), role))
                .orElse(false);
    }

    @Override
    public boolean isAdmin() {
        return hasRole(Role.ADMIN);
    }

}
