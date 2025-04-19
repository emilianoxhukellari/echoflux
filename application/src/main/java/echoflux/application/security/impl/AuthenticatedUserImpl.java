package echoflux.application.security.impl;

import com.vaadin.flow.spring.security.AuthenticationContext;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import echoflux.application.security.AuthenticatedUser;
import echoflux.domain.application_user.data.ApplicationUser;
import echoflux.core.core.utils.TsSets;
import echoflux.domain.application_user.data.ApplicationUserRepository;
import echoflux.domain.application_user.data.Role;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class AuthenticatedUserImpl implements AuthenticatedUser {

    private final ApplicationUserRepository applicationUserRepository;
    private final AuthenticationContext authenticationContext;

    @Override
    public Optional<ApplicationUser> find() {
        return authenticationContext.getAuthenticatedUser(UserDetails.class)
                .flatMap(ud -> applicationUserRepository.findByUsername(ud.getUsername()));
    }

    @Override
    public void logout() {
        authenticationContext.logout();
    }

    @Override
    public boolean hasRole(Role role) {
        return find().map(u -> TsSets.contains(u.getRoles(), role))
                .orElse(false);
    }

    @Override
    public boolean isAdmin() {
        return hasRole(Role.ADMIN);
    }

}
