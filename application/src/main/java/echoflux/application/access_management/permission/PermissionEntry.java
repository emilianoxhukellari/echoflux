package echoflux.application.access_management.permission;

import echoflux.domain.core.security.PermissionType;

public record PermissionEntry(Long id,
                              PermissionType type,
                              String description) {
}
