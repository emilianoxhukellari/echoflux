package echoflux.application.core.security.impl;

import echoflux.domain.access_management.application_user.mapper.ApplicationUserMapper;
import echoflux.domain.core.security.ApplicationUser;
import echoflux.domain.jooq.tables.pojos.VApplicationUser;
import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static echoflux.domain.jooq.Tables.V_APPLICATION_USER;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserDetailsServiceImpl implements UserDetailsService {

    private final DSLContext ctx;
    private final ApplicationUserMapper applicationUserMapper;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        var applicationUser = ctx.selectFrom(V_APPLICATION_USER)
                .where(V_APPLICATION_USER.USERNAME.eq(username))
                .fetchOptionalInto(VApplicationUser.class)
                .orElseThrow(() -> new UsernameNotFoundException("No user present with username: " + username));

        var applicationUserDetails = applicationUserMapper.toDetails(applicationUser);

        return new ApplicationUser(applicationUserDetails);
    }

}
