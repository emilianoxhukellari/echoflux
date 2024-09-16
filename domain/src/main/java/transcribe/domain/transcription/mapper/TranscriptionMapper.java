package transcribe.domain.transcription.mapper;

import org.mapstruct.CollectionMappingStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;
import transcribe.domain.transcription.data.TranscriptionEntity;
import transcribe.domain.transcription.data.TranscriptionStatus;
import transcribe.domain.transcription.service.CreateTranscriptionCommand;
import transcribe.domain.transcription.service.TranscriptionPipelineCommand;
import transcribe.domain.transcription.service.UpdateTranscriptionCommand;

@Mapper(collectionMappingStrategy = CollectionMappingStrategy.ADDER_PREFERRED,
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        componentModel = MappingConstants.ComponentModel.SPRING)
public interface TranscriptionMapper {

    @Mapping(target = "status", constant = "CREATED")
    TranscriptionEntity toEntity(CreateTranscriptionCommand command);

    @Mapping(target = "id", ignore = true)
    TranscriptionEntity asEntity(@MappingTarget TranscriptionEntity entity, UpdateTranscriptionCommand command);

    TranscriptionEntity asEntity(@MappingTarget TranscriptionEntity entity, TranscriptionStatus status);

    UpdateTranscriptionCommand toUpdateCommand(TranscriptionEntity entity);

    CreateTranscriptionCommand toCreateCommand(TranscriptionPipelineCommand command);

}
