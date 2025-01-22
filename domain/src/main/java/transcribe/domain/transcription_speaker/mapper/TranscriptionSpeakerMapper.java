package transcribe.domain.transcription_speaker.mapper;

import org.mapstruct.CollectionMappingStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.NullValueCheckStrategy;
import org.mapstruct.ReportingPolicy;
import transcribe.domain.transcription_speaker.data.TranscriptionSpeakerEntity;
import transcribe.domain.transcription_speaker.service.CreateTranscriptionSpeakerCommand;

@Mapper(collectionMappingStrategy = CollectionMappingStrategy.ADDER_PREFERRED,
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS,
        componentModel = MappingConstants.ComponentModel.SPRING)
public interface TranscriptionSpeakerMapper {

    TranscriptionSpeakerEntity toEntity(CreateTranscriptionSpeakerCommand command);

}
