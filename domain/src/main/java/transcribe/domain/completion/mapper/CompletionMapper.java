package transcribe.domain.completion.mapper;

import org.mapstruct.BeanMapping;
import org.mapstruct.CollectionMappingStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValueCheckStrategy;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;
import transcribe.core.completions.CompletionResult;
import transcribe.domain.completion.data.CompletionEntity;
import transcribe.domain.completion.data.CompletionStatus;
import transcribe.domain.completion.pipeline.CompletionPipelineResult;
import transcribe.domain.completion.service.CreateCompletionCommand;
import transcribe.domain.completion.service.UpdateCompletionCommand;

@Mapper(collectionMappingStrategy = CollectionMappingStrategy.ADDER_PREFERRED,
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS,
        componentModel = MappingConstants.ComponentModel.SPRING)
public interface CompletionMapper {

    @Mapping(target = "status", constant = "CREATED")
    CompletionEntity toEntity(CreateCompletionCommand command);

    @Mapping(target = "id", ignore = true)
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    CompletionEntity asEntity(@MappingTarget CompletionEntity entity, UpdateCompletionCommand command);

    UpdateCompletionCommand toCommand(CompletionResult result, CompletionStatus status, Long durationMillis);

    @Mapping(target = "completionId", source = "id")
    CompletionPipelineResult toResult(CompletionEntity entity);

}
