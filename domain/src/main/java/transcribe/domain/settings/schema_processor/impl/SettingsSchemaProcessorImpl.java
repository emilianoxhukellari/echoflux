package transcribe.domain.settings.schema_processor.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import transcribe.core.core.json.JsonMapper;
import transcribe.core.core.bean.utils.MoreBeans;
import transcribe.domain.settings.schema_processor.SettingsSchemaProcessor;

@Component
@RequiredArgsConstructor
public class SettingsSchemaProcessorImpl implements SettingsSchemaProcessor {

    private final JsonMapper jsonMapper;

    @Override
    public <T> JsonNode create(Class<T> beanType) {
        var object = MoreBeans.invokeBuilderOrNoArgsConstructor(beanType);

        return jsonMapper.toNode(object);
    }

    @Override
    public <T> JsonNode adaptToSchema(Class<T> beanType, JsonNode current) {
        var source = create(beanType);

        if (current.isNull()) {
            return source;
        }

        var result = (ObjectNode) current.deepCopy();
        removeExtraFields(source, result);
        addMissingFields(source, result);

        return result;
    }

    private static void removeExtraFields(JsonNode source, ObjectNode target) {
        var targetFieldNames = target.fieldNames();
        while (targetFieldNames.hasNext()) {
            var fieldName = targetFieldNames.next();
            if (!source.has(fieldName)) {
                targetFieldNames.remove();
                target.remove(fieldName);
            } else {
                var sourceValue = source.get(fieldName);
                var targetValue = target.get(fieldName);
                if (sourceValue.isObject() && targetValue.isObject()) {
                    removeExtraFields(sourceValue, (ObjectNode) targetValue);
                }
            }
        }
    }

    private static void addMissingFields(JsonNode source, ObjectNode target) {
        var fields = source.fields();
        while (fields.hasNext()) {
            var entry = fields.next();
            var fieldName = entry.getKey();
            var sourceValue = entry.getValue();

            if (!target.has(fieldName)) {
                target.set(fieldName, sourceValue);
            } else {
                var targetValue = target.get(fieldName);
                if (sourceValue.isObject() && targetValue.isObject()) {
                    addMissingFields(sourceValue, (ObjectNode) targetValue);
                }
            }
        }
    }

}
