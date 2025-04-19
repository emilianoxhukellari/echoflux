package echoflux.domain.settings.mapper;

import org.mapstruct.BeanMapping;
import org.mapstruct.CollectionMappingStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.NullValueCheckStrategy;
import org.mapstruct.ReportingPolicy;
import echoflux.domain.settings.data.SettingsEntity;
import echoflux.domain.settings.data.SettingsProjection;
import echoflux.domain.settings.service.CreateSettingsCommand;

import java.util.List;

@Mapper(collectionMappingStrategy = CollectionMappingStrategy.ADDER_PREFERRED,
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS,
        componentModel = MappingConstants.ComponentModel.SPRING)
public interface SettingsMapper {

    SettingsEntity toEntity(CreateSettingsCommand command);

    @BeanMapping(unmappedTargetPolicy = ReportingPolicy.ERROR)
    SettingsProjection toProjection(SettingsEntity entity);

    @BeanMapping(unmappedTargetPolicy = ReportingPolicy.ERROR)
    List<SettingsProjection> toProjections(List<SettingsEntity> entities);

}
