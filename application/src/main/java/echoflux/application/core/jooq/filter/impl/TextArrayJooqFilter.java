package echoflux.application.core.jooq.filter.impl;

import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.value.ValueChangeMode;
import echoflux.application.core.jooq.filter.JooqFilter;
import org.jooq.Condition;
import org.jooq.Field;
import org.jooq.impl.DSL;

public class TextArrayJooqFilter extends JooqFilter<String[]> {

    private final TextField textField;

    public TextArrayJooqFilter(Field<String[]> field) {
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

        return DSL.arrayAnyMatch(field, value -> value.likeIgnoreCase("%" + textField.getValue() + "%"));
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
