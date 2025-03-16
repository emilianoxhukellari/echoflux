package transcribe.application.template;

import org.mapstruct.CollectionMappingStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;
import transcribe.application.core.jpa.dto.JpaDtoMapper;
import transcribe.domain.template.data.TemplateEntity;

@Mapper(collectionMappingStrategy = CollectionMappingStrategy.ADDER_PREFERRED,
        unmappedTargetPolicy = ReportingPolicy.ERROR,
        componentModel = MappingConstants.ComponentModel.SPRING)
public interface TemplateJpaDtoMapper extends JpaDtoMapper<TemplateJpaDto, TemplateEntity> {

    @Override
    TemplateJpaDto toDto(TemplateEntity entity);

    @Override
    TemplateEntity toEntity(TemplateJpaDto dto);

    @Override
    @Mapping(target = "version", ignore = true)
    TemplateEntity updateEntity(@MappingTarget TemplateEntity entity, TemplateJpaDto dto);

    @Override
    default Class<TemplateJpaDto> getBeanType() {
        return TemplateJpaDto.class;
    }

}
