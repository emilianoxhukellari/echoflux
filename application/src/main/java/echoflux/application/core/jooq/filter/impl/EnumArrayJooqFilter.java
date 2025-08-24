package echoflux.application.core.jooq.filter.impl;

import com.vaadin.flow.component.combobox.ComboBox;
import echoflux.application.core.jooq.filter.JooqFilter;
import echoflux.core.core.utils.MoreArrays;
import echoflux.core.core.utils.MoreEnums;
import echoflux.core.core.validate.guard.Guard;
import org.jooq.Condition;
import org.jooq.Field;
import org.jooq.impl.DSL;

public class EnumArrayJooqFilter extends JooqFilter<Enum<?>[]> {

    private final ComboBox<Enum<?>> comboBox;

    public EnumArrayJooqFilter(Field<Enum<?>[]> field) {
        super(field);

        var componentType = field.getDataType().getArrayComponentType();
        Guard.notNull(componentType, "componentType");

        this.comboBox = new ComboBox<>();
        comboBox.setItems(MoreArrays.collect(componentType.getEnumConstants(), v -> (Enum<?>) v));
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
        var valueAsArray = new Enum<?>[]{comboBox.getValue()};

        return field.contains(DSL.array(valueAsArray).cast(field.getDataType()));
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
