package transcribe.core.core.json;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.mapstruct.CollectionMappingStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.NullValueCheckStrategy;
import org.mapstruct.ReportingPolicy;

import java.util.Optional;

@Mapper(collectionMappingStrategy = CollectionMappingStrategy.ADDER_PREFERRED,
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS,
        componentModel = MappingConstants.ComponentModel.SPRING)
public interface JsonMapper {

    ObjectMapper objectMapper = new ObjectMapper();

    default <T> JsonNode toNode(T object) {
        return objectMapper.valueToTree(object);
    }

    @SneakyThrows
    default JsonNode toNode(String json) {
        return objectMapper.readTree(json);
    }

    default Optional<JsonNode> tryToNode(String json) {
        try {
            return Optional.of(toNode(json));
        } catch (Throwable e) {
            return Optional.empty();
        }
    }

    @SneakyThrows
    default <T> T toValue(JsonNode node, Class<T> clazz) {
        return objectMapper.treeToValue(node, clazz);
    }

    @SneakyThrows
    default <T> String toString(T object) {
        return objectMapper.writeValueAsString(object);
    }

}
