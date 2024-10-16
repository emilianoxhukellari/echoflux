package transcribe.core.completions.google;

import com.google.cloud.vertexai.api.GenerateContentResponse;
import com.google.cloud.vertexai.generativeai.ResponseHandler;
import org.apache.commons.collections4.ListUtils;
import org.mapstruct.CollectionMappingStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.NullValueCheckStrategy;
import org.mapstruct.ReportingPolicy;
import transcribe.core.completions.Tokens;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Mapper(collectionMappingStrategy = CollectionMappingStrategy.ADDER_PREFERRED,
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS,
        componentModel = MappingConstants.ComponentModel.SPRING)
public interface GoogleCompletionsMapper {

    default Tokens toTokens(GenerateContentResponse response) {
        return Optional.ofNullable(response)
                .map(GenerateContentResponse::getUsageMetadata)
                .map(usage -> new Tokens(usage.getPromptTokenCount(), usage.getCandidatesTokenCount()))
                .orElse(Tokens.empty());
    }

    default Tokens toTokens(List<GenerateContentResponse> responseList) {
        return ListUtils.emptyIfNull(responseList)
                .stream()
                .map(this::toTokens)
                .reduce(Tokens::add)
                .orElseGet(Tokens::empty);
    }

    default String toOutput(List<GenerateContentResponse> responseList) {
        return ListUtils.emptyIfNull(responseList)
                .stream()
                .map(ResponseHandler::getText)
                .collect(Collectors.joining());
    }

}
