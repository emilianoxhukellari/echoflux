package transcribe.domain.application_user.service;

import org.mapstruct.BeanMapping;
import org.mapstruct.CollectionMappingStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValueCheckStrategy;
import org.mapstruct.ReportingPolicy;
import transcribe.domain.application_user.data.ApplicationUserEntity;
import transcribe.domain.application_user.data.ApplicationUserProjection;

@Mapper(collectionMappingStrategy = CollectionMappingStrategy.ADDER_PREFERRED,
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS,
        componentModel = MappingConstants.ComponentModel.SPRING)
public interface ApplicationUserMapper {

    @Mapping(target = "password", source = "hashedPassword")
    ApplicationUserEntity toEntity(CreateApplicationUserCommand command, String hashedPassword);

    @BeanMapping(unmappedTargetPolicy = ReportingPolicy.ERROR)
    ApplicationUserProjection toProjection(ApplicationUserEntity entity);

    @Mapping(target = "id", ignore = true)
    ApplicationUserEntity patch(@MappingTarget ApplicationUserEntity entity, UpdateApplicationUserCommand command);

    @Mapping(target = "password", source = "hashedPassword")
    @Mapping(target = "id", ignore = true)
    ApplicationUserEntity patch(@MappingTarget ApplicationUserEntity entity, String hashedPassword);

}
