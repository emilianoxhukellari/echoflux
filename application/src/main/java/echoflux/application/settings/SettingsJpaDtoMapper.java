package echoflux.application.settings;

import org.mapstruct.CollectionMappingStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;
import echoflux.application.core.jpa.dto.JpaDtoMapper;
import echoflux.domain.settings.data.SettingsEntity;

@Mapper(collectionMappingStrategy = CollectionMappingStrategy.ADDER_PREFERRED,
        unmappedTargetPolicy = ReportingPolicy.ERROR,
        componentModel = MappingConstants.ComponentModel.SPRING)
public interface SettingsJpaDtoMapper extends JpaDtoMapper<SettingsJpaDto, SettingsEntity> {

    @Override
    SettingsJpaDto toDto(SettingsEntity entity);

    @Override
    SettingsEntity toEntity(SettingsJpaDto dto);

    @Override
    @Mapping(target = "version", ignore = true)
    SettingsEntity updateEntity(@MappingTarget SettingsEntity entity, SettingsJpaDto dto);

    @Override
    default Class<SettingsJpaDto> getBeanType() {
        return SettingsJpaDto.class;
    }

}
