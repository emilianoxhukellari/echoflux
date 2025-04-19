package echoflux.domain.application_user.data;

import java.util.Set;

public record ApplicationUserProjection(Long id,
                                        String username,
                                        String name,
                                        String password,
                                        Boolean enabled,
                                        Set<Role> roles) {
}
