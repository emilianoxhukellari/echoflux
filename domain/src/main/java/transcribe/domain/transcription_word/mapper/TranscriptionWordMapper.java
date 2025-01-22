package transcribe.domain.transcription_word.mapper;

import org.mapstruct.BeanMapping;
import org.mapstruct.CollectionMappingStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValueCheckStrategy;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;
import transcribe.domain.transcription_word.data.TranscriptionWordEntity;
import transcribe.domain.transcription_word.service.CreateTranscriptionWordCommand;
import transcribe.domain.transcription_word.service.PatchTranscriptionWordCommand;

import java.util.List;

@Mapper(collectionMappingStrategy = CollectionMappingStrategy.ADDER_PREFERRED,
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS,
        componentModel = MappingConstants.ComponentModel.SPRING)
public interface TranscriptionWordMapper {

    TranscriptionWordEntity toEntity(CreateTranscriptionWordCommand command);

    List<TranscriptionWordEntity> toEntities(List<CreateTranscriptionWordCommand> commands);

    @Mapping(target = "id", ignore = true)
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    TranscriptionWordEntity patch(@MappingTarget TranscriptionWordEntity entity, PatchTranscriptionWordCommand command);

}
