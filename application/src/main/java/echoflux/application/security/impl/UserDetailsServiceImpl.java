package echoflux.application.security.impl;

import echoflux.domain.application_user.data.ApplicationUser;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import echoflux.domain.application_user.data.ApplicationUserRepository;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserDetailsServiceImpl implements UserDetailsService {

    private final ApplicationUserRepository applicationUserRepository;

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return applicationUserRepository.findProjectedByUsername(username)
                .map(ApplicationUser::new)
                .orElseThrow(() -> new UsernameNotFoundException("No user present with username: " + username));
    }

}
