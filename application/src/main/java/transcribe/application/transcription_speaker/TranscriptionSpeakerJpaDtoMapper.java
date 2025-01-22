package transcribe.application.transcription_speaker;

import org.mapstruct.CollectionMappingStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;
import transcribe.application.core.jpa.dto.JpaDtoMapper;
import transcribe.domain.transcription_speaker.data.TranscriptionSpeakerEntity;

@Mapper(collectionMappingStrategy = CollectionMappingStrategy.ADDER_PREFERRED,
        unmappedTargetPolicy = ReportingPolicy.ERROR,
        componentModel = MappingConstants.ComponentModel.SPRING)
public interface TranscriptionSpeakerJpaDtoMapper extends JpaDtoMapper<TranscriptionSpeakerJpaDto, TranscriptionSpeakerEntity> {

    @Override
    TranscriptionSpeakerJpaDto toDto(TranscriptionSpeakerEntity entity);

    @Override
    @Mapping(target = "transcriptionWords", ignore = true)
    TranscriptionSpeakerEntity toEntity(TranscriptionSpeakerJpaDto dto);

    @Override
    @Mapping(target = "transcriptionWords", ignore = true)
    TranscriptionSpeakerEntity updateEntity(@MappingTarget TranscriptionSpeakerEntity entity, TranscriptionSpeakerJpaDto dto);

    @Override
    default Class<TranscriptionSpeakerJpaDto> getBeanType() {
        return TranscriptionSpeakerJpaDto.class;
    }

}
