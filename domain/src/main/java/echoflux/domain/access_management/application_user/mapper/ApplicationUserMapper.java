package echoflux.domain.access_management.application_user.mapper;

import echoflux.domain.core.security.ApplicationUserDetails;
import echoflux.domain.jooq.tables.pojos.VApplicationUser;
import org.mapstruct.CollectionMappingStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.NullValueCheckStrategy;
import org.mapstruct.ReportingPolicy;

@Mapper(collectionMappingStrategy = CollectionMappingStrategy.ADDER_PREFERRED,
        unmappedTargetPolicy = ReportingPolicy.ERROR,
        nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS,
        componentModel = MappingConstants.ComponentModel.SPRING)
public interface ApplicationUserMapper {

    ApplicationUserDetails toDetails(VApplicationUser applicationUser);

}
