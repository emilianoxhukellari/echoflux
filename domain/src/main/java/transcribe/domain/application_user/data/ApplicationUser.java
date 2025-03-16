package transcribe.domain.application_user.data;

import java.util.Set;

public interface ApplicationUser {

    Long getId();

    String getUsername();

    String getName();

    String getPassword();

    Boolean getEnabled();

    Set<Role> getRoles();

}