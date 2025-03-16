package transcribe.application.security;

import org.apache.commons.collections4.SetUtils;
import org.mapstruct.CollectionMappingStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.NullValueCheckStrategy;
import org.mapstruct.ReportingPolicy;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import transcribe.domain.application_user.data.ApplicationUser;

@Mapper(collectionMappingStrategy = CollectionMappingStrategy.ADDER_PREFERRED,
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS,
        componentModel = MappingConstants.ComponentModel.SPRING)
public interface SecurityMapper {

    default UserDetails toDetails(ApplicationUser applicationUser) {
        if (applicationUser == null) {
            return null;
        }

        var roles = SetUtils.emptyIfNull(applicationUser.getRoles())
                .stream()
                .map(Enum::name)
                .toArray(String[]::new);

        return User.builder()
                .username(applicationUser.getUsername())
                .password(applicationUser.getPassword())
                .disabled(!Boolean.TRUE.equals(applicationUser.getEnabled()))
                .roles(roles)
                .build();
    }

}
