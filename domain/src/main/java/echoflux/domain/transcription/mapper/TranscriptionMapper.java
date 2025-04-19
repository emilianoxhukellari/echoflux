package echoflux.domain.transcription.mapper;

import org.mapstruct.BeanMapping;
import org.mapstruct.CollectionMappingStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValueCheckStrategy;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;
import echoflux.domain.transcription.data.TranscriptionProjection;
import echoflux.domain.transcription.data.TranscriptionEntity;
import echoflux.domain.transcription.pipeline.TranscriptionPipelineCommand;
import echoflux.domain.transcription.service.CreateTranscriptionCommand;
import echoflux.domain.transcription.service.PatchTranscriptionCommand;

@Mapper(collectionMappingStrategy = CollectionMappingStrategy.ADDER_PREFERRED,
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS,
        componentModel = MappingConstants.ComponentModel.SPRING)
public interface TranscriptionMapper {

    @Mapping(target = "status", constant = "CREATED")
    TranscriptionEntity toEntity(CreateTranscriptionCommand command);

    @BeanMapping(unmappedTargetPolicy = ReportingPolicy.ERROR)
    TranscriptionProjection toProjection(TranscriptionEntity entity);

    @Mapping(target = "id", ignore = true)
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    TranscriptionEntity patch(@MappingTarget TranscriptionEntity entity, PatchTranscriptionCommand command);

    CreateTranscriptionCommand toCommand(TranscriptionPipelineCommand command);

}
