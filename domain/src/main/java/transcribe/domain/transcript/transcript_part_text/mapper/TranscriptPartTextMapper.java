package transcribe.domain.transcript.transcript_part_text.mapper;

import org.mapstruct.*;
import transcribe.domain.transcript.transcript_part_text.data.TranscriptPartTextEntity;
import transcribe.domain.transcript.transcript_part_text.service.AddTranscriptPartTextCommand;
import transcribe.domain.transcript.transcript_part_text.service.CreateTranscriptPartTextCommand;

@Mapper(collectionMappingStrategy = CollectionMappingStrategy.ADDER_PREFERRED,
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS,
        componentModel = MappingConstants.ComponentModel.SPRING)
public interface TranscriptPartTextMapper {

    TranscriptPartTextEntity toEntity(CreateTranscriptPartTextCommand command);

    TranscriptPartTextEntity toEntity(AddTranscriptPartTextCommand command, Integer version);

}
