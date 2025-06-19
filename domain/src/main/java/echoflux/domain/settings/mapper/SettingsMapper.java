package echoflux.domain.settings.mapper;

import echoflux.domain.settings.data.SettingsProjection;
import org.mapstruct.CollectionMappingStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.NullValueCheckStrategy;
import org.mapstruct.ReportingPolicy;
import echoflux.domain.settings.data.SettingsEntity;
import echoflux.domain.settings.service.CreateSettingsCommand;

@Mapper(collectionMappingStrategy = CollectionMappingStrategy.ADDER_PREFERRED,
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS,
        componentModel = MappingConstants.ComponentModel.SPRING)
public interface SettingsMapper {

    SettingsEntity toEntity(CreateSettingsCommand command);

    SettingsProjection toProjection(SettingsEntity entity);

}
