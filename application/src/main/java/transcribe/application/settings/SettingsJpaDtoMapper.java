package transcribe.application.settings;

import org.mapstruct.CollectionMappingStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;
import transcribe.application.core.jpa.dto.JpaDtoMapper;
import transcribe.domain.settings.data.SettingsEntity;

@Mapper(collectionMappingStrategy = CollectionMappingStrategy.ADDER_PREFERRED,
        unmappedTargetPolicy = ReportingPolicy.ERROR,
        componentModel = MappingConstants.ComponentModel.SPRING)
public interface SettingsJpaDtoMapper extends JpaDtoMapper<SettingsJpaDto, SettingsEntity> {

    @Override
    SettingsJpaDto toDto(SettingsEntity entity);

    @Override
    SettingsEntity toEntity(SettingsJpaDto dto);

    @Override
    SettingsEntity updateEntity(@MappingTarget SettingsEntity entity, SettingsJpaDto dto);

    @Override
    default Class<SettingsJpaDto> getBeanType() {
        return SettingsJpaDto.class;
    }

}
