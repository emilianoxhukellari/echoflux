package transcribe.application.user;

import org.mapstruct.CollectionMappingStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;
import transcribe.application.core.jpa.dto.JpaDtoMapper;
import transcribe.domain.application_user.data.ApplicationUserEntity;
import transcribe.domain.application_user.service.UpdateApplicationUserCommand;

@Mapper(collectionMappingStrategy = CollectionMappingStrategy.ADDER_PREFERRED,
        unmappedTargetPolicy = ReportingPolicy.ERROR,
        componentModel = MappingConstants.ComponentModel.SPRING)
public interface ApplicationUserJpaDtoMapper extends JpaDtoMapper<ApplicationUserJpaDto, ApplicationUserEntity> {

    @Override
    ApplicationUserJpaDto toDto(ApplicationUserEntity entity);

    @Override
    ApplicationUserEntity toEntity(ApplicationUserJpaDto dto);

    @Override
    ApplicationUserEntity updateEntity(@MappingTarget ApplicationUserEntity entity, ApplicationUserJpaDto dto);

    UpdateApplicationUserCommand toUpdateCommand(ApplicationUserJpaDto dto);

    @Override
    default Class<ApplicationUserJpaDto> getBeanType() {
        return ApplicationUserJpaDto.class;
    }

}
