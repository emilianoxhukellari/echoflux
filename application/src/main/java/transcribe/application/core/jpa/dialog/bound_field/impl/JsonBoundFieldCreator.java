package transcribe.application.core.jpa.dialog.bound_field.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.NullNode;
import com.vaadin.flow.component.AbstractField;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.PropertyDefinition;
import com.vaadin.flow.data.binder.Result;
import com.vaadin.flow.data.binder.Setter;
import com.vaadin.flow.data.converter.Converter;
import com.vaadin.flow.function.ValueProvider;
import com.vaadin.flow.spring.annotation.SpringComponent;
import lombok.RequiredArgsConstructor;
import transcribe.application.core.jpa.core.JpaSupportedType;
import transcribe.application.core.jpa.dialog.bound_field.BoundFieldCreator;
import transcribe.core.core.json.JsonMapper;

@SpringComponent
@RequiredArgsConstructor
public class JsonBoundFieldCreator implements BoundFieldCreator {

    private final JsonMapper jsonMapper;

    @Override
    @SuppressWarnings("unchecked")
    public <T, V> AbstractField<TextArea, String> newBoundField(PropertyDefinition<T, V> property,
                                                                Binder<T> binder,
                                                                boolean required) {
        var field = new TextArea(property.getCaption());
        field.setMaxHeight("400px");
        var getter = (ValueProvider<T, JsonNode>) property.getGetter();
        var setter = (Setter<T, JsonNode>) property.getSetter().orElse(null);
        var builder = binder.forField(field)
                .withConverter(Converter.from(
                        this::toModel,
                        v -> v == null ? NullNode.getInstance().toString() : v.toPrettyString()
                ));
        if (required) {
            builder.asRequired();
        }
        builder.bind(getter, setter);

        return field;
    }

    @Override
    public boolean supportsType(JpaSupportedType type) {
        return JpaSupportedType.JSON.equals(type);
    }

    private Result<JsonNode> toModel(String json) {
        return jsonMapper.tryToNode(json)
                .map(Result::ok)
                .orElseGet(() -> Result.error("Invalid JSON"));
    }

}
