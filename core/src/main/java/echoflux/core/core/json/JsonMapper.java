package echoflux.core.core.json;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.SneakyThrows;

import java.util.Optional;

public final class JsonMapper {

    private final static ObjectMapper objectMapper;

    static {
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);
        objectMapper.configure(JsonParser.Feature.ALLOW_SINGLE_QUOTES, true);
        objectMapper.configure(SerializationFeature.INDENT_OUTPUT, true);
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
    }

    public static <T> JsonNode toNode(T object) {
        return objectMapper.valueToTree(object);
    }

    @SneakyThrows
    public static JsonNode toNode(String json) {
        return objectMapper.readTree(json);
    }

    public static Optional<JsonNode> tryToNode(String json) {
        try {
            return Optional.of(toNode(json));
        } catch (Throwable e) {
            return Optional.empty();
        }
    }

    @SneakyThrows
    public static <T> T toValue(JsonNode node, Class<T> beanType) {
        return objectMapper.treeToValue(node, beanType);
    }

    public static <T> T toValue(String json, Class<T> beanType) {
        var node = toNode(json);

        return toValue(node, beanType);
    }

    @SneakyThrows
    public static <T> String toString(T object) {
        return objectMapper.writeValueAsString(object);
    }

}
