package transcribe.application.security;

import org.apache.commons.collections4.SetUtils;
import org.mapstruct.CollectionMappingStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.NullValueCheckStrategy;
import org.mapstruct.ReportingPolicy;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import transcribe.domain.application_user.data.ApplicationUserEntity;

@Mapper(collectionMappingStrategy = CollectionMappingStrategy.ADDER_PREFERRED,
        nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS,
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        componentModel = MappingConstants.ComponentModel.SPRING)
public interface SecurityMapper {

    default UserDetails toDetails(ApplicationUserEntity entity) {
        if (entity == null) {
            return null;
        }

        var roles = SetUtils.emptyIfNull(entity.getRoles())
                .stream()
                .map(Enum::name)
                .toArray(String[]::new);

        return User.builder()
                .username(entity.getUsername())
                .password(entity.getPassword())
                .disabled(!Boolean.TRUE.equals(entity.getEnabled()))
                .roles(roles)
                .build();
    }

}
