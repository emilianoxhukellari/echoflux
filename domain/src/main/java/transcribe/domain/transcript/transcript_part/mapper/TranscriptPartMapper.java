package transcribe.domain.transcript.transcript_part.mapper;

import org.mapstruct.*;
import transcribe.domain.transcript.transcript_part.data.TranscriptPartEntity;
import transcribe.domain.transcript.transcript_part.service.CreateTranscriptPartCommand;

@Mapper(collectionMappingStrategy = CollectionMappingStrategy.ADDER_PREFERRED,
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS,
        componentModel = MappingConstants.ComponentModel.SPRING)
public interface TranscriptPartMapper {

    TranscriptPartEntity toEntity(CreateTranscriptPartCommand command);

}
