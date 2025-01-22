package transcribe.application.completion;

import org.mapstruct.CollectionMappingStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;
import transcribe.application.core.jpa.dto.JpaDtoMapper;
import transcribe.domain.completion.data.CompletionEntity;

@Mapper(collectionMappingStrategy = CollectionMappingStrategy.ADDER_PREFERRED,
        unmappedTargetPolicy = ReportingPolicy.ERROR,
        componentModel = MappingConstants.ComponentModel.SPRING)
public interface CompletionJpaDtoMapper extends JpaDtoMapper<CompletionJpaDto, CompletionEntity> {

    @Override
    CompletionJpaDto toDto(CompletionEntity entity);

    @Override
    CompletionEntity toEntity(CompletionJpaDto dto);

    @Override
    CompletionEntity updateEntity(@MappingTarget CompletionEntity entity, CompletionJpaDto dto);

    @Override
    default Class<CompletionJpaDto> getBeanType() {
        return CompletionJpaDto.class;
    }

}
