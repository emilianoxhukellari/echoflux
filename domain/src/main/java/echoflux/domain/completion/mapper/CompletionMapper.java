package echoflux.domain.completion.mapper;

import echoflux.domain.completion.data.ScalarCompletionProjection;
import org.mapstruct.BeanMapping;
import org.mapstruct.CollectionMappingStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValueCheckStrategy;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;
import echoflux.core.completions.CompletionResult;
import echoflux.domain.completion.data.CompletionEntity;
import echoflux.domain.completion.service.CreateCompletionCommand;
import echoflux.domain.completion.service.PatchCompletionCommand;

@Mapper(collectionMappingStrategy = CollectionMappingStrategy.ADDER_PREFERRED,
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS,
        componentModel = MappingConstants.ComponentModel.SPRING)
public interface CompletionMapper {

    @Mapping(target = "status", constant = "CREATED")
    CompletionEntity toEntity(CreateCompletionCommand command);

    @BeanMapping(unmappedTargetPolicy = ReportingPolicy.ERROR)
    ScalarCompletionProjection toProjection(CompletionEntity entity);

    @Mapping(target = "id", ignore = true)
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    CompletionEntity patch(@MappingTarget CompletionEntity entity, PatchCompletionCommand command);

    PatchCompletionCommand toCommand(CompletionResult result);

}
