package com.example.application.common.json;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.SneakyThrows;

public class JsonUtils {
    private static final ObjectMapper mapper;

    static {
        mapper = new ObjectMapper();
        mapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);
        mapper.configure(JsonParser.Feature.ALLOW_SINGLE_QUOTES, true);
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        mapper.enable(SerializationFeature.INDENT_OUTPUT);
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        mapper.registerModule(new JavaTimeModule());
    }

    public static ObjectMapper mapper() {
        return mapper.copy();
    }

    public static JsonNode toNode(Object value) {
        return mapper.valueToTree(value);
    }

    @SneakyThrows
    public static JsonNode toNode(String value) {
        return mapper.readTree(value);
    }

    @SneakyThrows
    public static String toString(Object value) {
        return mapper.writeValueAsString(value);
    }

    @SneakyThrows
    public static <T> T toObject(JsonNode jsonNode, Class<T> targetClass) {
        return mapper.treeToValue(jsonNode, targetClass);
    }

    @SneakyThrows
    public static <T> T toObject(String value, Class<T> targetClass) {
        return mapper.readValue(value, targetClass);
    }

}
