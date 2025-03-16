package transcribe.application.transcription_word;

import org.mapstruct.CollectionMappingStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;
import transcribe.application.core.jpa.dto.JpaDtoMapper;
import transcribe.application.user.ApplicationUserJpaDtoMapper;
import transcribe.domain.transcription_word.data.TranscriptionWordEntity;

@Mapper(collectionMappingStrategy = CollectionMappingStrategy.ADDER_PREFERRED,
        unmappedTargetPolicy = ReportingPolicy.ERROR,
        componentModel = MappingConstants.ComponentModel.SPRING,
        uses = {ApplicationUserJpaDtoMapper.class})
public interface TranscriptionWordJpaDtoMapper extends JpaDtoMapper<TranscriptionWordJpaDto, TranscriptionWordEntity> {

    @Override
    TranscriptionWordJpaDto toDto(TranscriptionWordEntity entity);

    @Override
    @Mapping(target = "transcription.words", ignore = true)
    @Mapping(target = "transcription.completions", ignore = true)
    TranscriptionWordEntity toEntity(TranscriptionWordJpaDto dto);

    @Override
    @Mapping(target = "transcription.words", ignore = true)
    @Mapping(target = "transcription.completions", ignore = true)
    @Mapping(target = "transcription.version", ignore = true)
    @Mapping(target = "version", ignore = true)
    TranscriptionWordEntity updateEntity(@MappingTarget TranscriptionWordEntity entity, TranscriptionWordJpaDto dto);

    @Override
    default Class<TranscriptionWordJpaDto> getBeanType() {
        return TranscriptionWordJpaDto.class;
    }

}
