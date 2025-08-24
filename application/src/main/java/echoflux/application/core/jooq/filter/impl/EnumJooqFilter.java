package echoflux.application.core.jooq.filter.impl;

import com.vaadin.flow.component.combobox.ComboBox;
import echoflux.application.core.jooq.filter.JooqFilter;
import echoflux.core.core.utils.MoreArrays;
import echoflux.core.core.utils.MoreEnums;
import org.jooq.Condition;
import org.jooq.Field;
import org.jooq.impl.DSL;

public class EnumJooqFilter extends JooqFilter<Enum<?>> {

    private final ComboBox<Enum<?>> comboBox;

    public EnumJooqFilter(Field<Enum<?>> field) {
        super(field);

        this.comboBox = new ComboBox<>();
        comboBox.setItems(MoreArrays.collect(field.getType().getEnumConstants(), v -> (Enum<?>) v));
        comboBox.setItemLabelGenerator(MoreEnums::toDisplayName);
        comboBox.setPlaceholder("Filter");
        comboBox.setClearButtonVisible(true);
        comboBox.setMinWidth("0");

        addAndExpand(comboBox);
    }

    @Override
    public Condition getCondition() {
        if (comboBox.isEmpty()) {
            return DSL.noCondition();
        }

        return field.eq(comboBox.getValue());
    }

    @Override
    public void addValueChangeListener(Runnable listener) {
        comboBox.addValueChangeListener(_ -> listener.run());
    }

    @Override
    public void clear() {
        comboBox.clear();
    }

}
