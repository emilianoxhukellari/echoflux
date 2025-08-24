package echoflux.domain.access_management.permission.synchronizer;

import org.springframework.validation.annotation.Validated;

@Validated
public interface PermissionSynchronizer {

    void synchronizeAll();

}
