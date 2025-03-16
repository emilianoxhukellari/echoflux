package transcribe.application.transcription;

import org.mapstruct.CollectionMappingStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;
import transcribe.application.core.jpa.dto.JpaDtoMapper;
import transcribe.domain.transcription.data.TranscriptionEntity;

@Mapper(collectionMappingStrategy = CollectionMappingStrategy.ADDER_PREFERRED,
        unmappedTargetPolicy = ReportingPolicy.ERROR,
        componentModel = MappingConstants.ComponentModel.SPRING)
public interface TranscriptionJpaDtoMapper extends JpaDtoMapper<TranscriptionJpaDto, TranscriptionEntity> {

    @Override
    TranscriptionJpaDto toDto(TranscriptionEntity entity);

    @Override
    @Mapping(target = "words", ignore = true)
    @Mapping(target = "completions", ignore = true)
    @Mapping(target = "applicationUser.transcriptions", ignore = true)
    TranscriptionEntity toEntity(TranscriptionJpaDto dto);

    @Override
    @Mapping(target = "words", ignore = true)
    @Mapping(target = "completions", ignore = true)
    @Mapping(target = "applicationUser.transcriptions", ignore = true)
    @Mapping(target = "applicationUser.version", ignore = true)
    @Mapping(target = "version", ignore = true)
    TranscriptionEntity updateEntity(@MappingTarget TranscriptionEntity entity, TranscriptionJpaDto dto);

    @Override
    default Class<TranscriptionJpaDto> getBeanType() {
        return TranscriptionJpaDto.class;
    }

}
