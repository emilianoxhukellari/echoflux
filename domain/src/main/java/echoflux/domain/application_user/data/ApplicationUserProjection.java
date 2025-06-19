package echoflux.domain.application_user.data;

import org.immutables.value.Value;

import java.util.Set;

@Value.Immutable
public interface ApplicationUserProjection extends ScalarApplicationUserProjection {

    Set<Role> getRoles();

}
