package transcribe.domain.application_user.service;

import org.mapstruct.CollectionMappingStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValueCheckStrategy;
import org.mapstruct.ReportingPolicy;
import transcribe.domain.application_user.data.ApplicationUserEntity;

@Mapper(collectionMappingStrategy = CollectionMappingStrategy.ADDER_PREFERRED,
        nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS,
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        componentModel = MappingConstants.ComponentModel.SPRING)
public interface ApplicationUserMapper {

    @Mapping(target = "password", source = "hashedPassword")
    ApplicationUserEntity toEntity(CreateApplicationUserCommand command, String hashedPassword);

    @Mapping(target = "id", ignore = true)
    ApplicationUserEntity asEntity(@MappingTarget ApplicationUserEntity entity, UpdateApplicationUserCommand command);

    @Mapping(target = "password", source = "hashedPassword")
    @Mapping(target = "id", ignore = true)
    ApplicationUserEntity asEntity(@MappingTarget ApplicationUserEntity entity, String hashedPassword);

    UpdateApplicationUserCommand toUpdateCommand(ApplicationUserEntity entity);

}
