package transcribe.application.core.jpa.dialog.bound_field.impl;

import com.vaadin.flow.component.AbstractField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.PropertyDefinition;
import com.vaadin.flow.data.binder.Result;
import com.vaadin.flow.data.binder.Setter;
import com.vaadin.flow.data.converter.Converter;
import com.vaadin.flow.function.ValueProvider;
import com.vaadin.flow.spring.annotation.SpringComponent;
import org.apache.commons.lang3.StringUtils;
import transcribe.application.core.jpa.core.JpaSupportedType;
import transcribe.application.core.jpa.dialog.bound_field.BoundFieldCreator;
import transcribe.core.core.utils.UriUtils;

import java.net.URI;
import java.util.Objects;

@SpringComponent
public class UriBoundFieldCreator implements BoundFieldCreator {

    @Override
    @SuppressWarnings("unchecked")
    public <T, V> AbstractField<TextField, String> newBoundField(PropertyDefinition<T, V> property,
                                                                 Binder<T> binder,
                                                                 boolean required) {
        var field = new TextField(property.getCaption());
        var getter = (ValueProvider<T, URI>) property.getGetter();
        var setter = (Setter<T, URI>) property.getSetter().orElse(null);
        var builder = binder.forField(field).withConverter(
                Converter.from(
                        v -> Result.ok(UriUtils.newUri(v)),
                        v -> Objects.toString(v, StringUtils.EMPTY)
                )
        );
        if (required) {
            builder.asRequired();
        }
        builder.bind(getter, setter);

        return field;
    }

    @Override
    public boolean supportsType(JpaSupportedType type) {
        return JpaSupportedType.URI.equals(type);
    }

}
