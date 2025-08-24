package echoflux.application.core.jooq.filter.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.value.ValueChangeMode;
import echoflux.application.core.jooq.filter.JooqFilter;
import echoflux.core.core.validate.guard.Guard;
import org.jooq.Condition;
import org.jooq.Field;
import org.jooq.impl.DSL;

import java.net.URI;
import java.time.ZoneId;
import java.util.Objects;

public class TextJooqFilter extends JooqFilter<String> {

    private final TextField textField;

    public static TextJooqFilter ofUriField(Field<URI> field) {
        Guard.notNull(field, "field");
        var stringField = field.convertFrom(uri -> Objects.toString(uri, null));

        return new TextJooqFilter(stringField);
    }

    public static TextJooqFilter ofZoneIdField(Field<ZoneId> field) {
        Guard.notNull(field, "field");
        var stringField = field.convertFrom(zoneId -> zoneId == null ? null : zoneId.getId());

        return new TextJooqFilter(stringField);
    }

    public static TextJooqFilter ofJsonfield(Field<JsonNode> field) {
        Guard.notNull(field, "field");
        var stringField = field.convertFrom(jsonNode -> jsonNode == null ? null : jsonNode.toString());

        return new TextJooqFilter(stringField);
    }

    public TextJooqFilter(Field<String> field) {
        super(field);

        this.textField = new TextField();

        textField.setPlaceholder("Filter");
        textField.setClearButtonVisible(true);
        textField.setValueChangeMode(ValueChangeMode.LAZY);
        textField.setMinWidth("0");

        addAndExpand(textField);
    }

    @Override
    public Condition getCondition() {
        if (textField.isEmpty()) {
            return DSL.noCondition();
        }

        return field.likeIgnoreCase("%" + textField.getValue() + "%");
    }

    @Override
    public void addValueChangeListener(Runnable listener) {
        textField.addValueChangeListener(_ -> listener.run());
    }

    @Override
    public void clear() {
        textField.clear();
    }

}
