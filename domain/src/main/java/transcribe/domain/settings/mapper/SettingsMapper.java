package transcribe.domain.settings.mapper;

import org.mapstruct.BeanMapping;
import org.mapstruct.CollectionMappingStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValueCheckStrategy;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;
import transcribe.domain.settings.data.SettingsEntity;
import transcribe.domain.settings.service.CreateSettingsCommand;
import transcribe.domain.settings.service.UpdateSettingsCommand;

@Mapper(collectionMappingStrategy = CollectionMappingStrategy.ADDER_PREFERRED,
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS,
        componentModel = MappingConstants.ComponentModel.SPRING)
public interface SettingsMapper {

    SettingsEntity toEntity(CreateSettingsCommand command);

    @Mapping(target = "id", ignore = true)
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    SettingsEntity asEntity(@MappingTarget SettingsEntity entity, UpdateSettingsCommand command);

}
