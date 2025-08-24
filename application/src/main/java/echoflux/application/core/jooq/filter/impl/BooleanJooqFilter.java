package echoflux.application.core.jooq.filter.impl;

import com.vaadin.flow.component.combobox.ComboBox;
import echoflux.application.core.jooq.filter.JooqFilter;
import echoflux.core.core.display_name.DisplayName;
import echoflux.core.core.utils.MoreEnums;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.jooq.Condition;
import org.jooq.Field;
import org.jooq.impl.DSL;

public class BooleanJooqFilter extends JooqFilter<Boolean> {

    private final ComboBox<BooleanState> comboBox;

    public BooleanJooqFilter(Field<Boolean> field) {
        super(field);

        this.comboBox = new ComboBox<>();
        comboBox.setItems(BooleanState.values());
        comboBox.setItemLabelGenerator(MoreEnums::toDisplayName);
        comboBox.setPlaceholder("Filter");
        comboBox.setClearButtonVisible(true);

        addAndExpand(comboBox);
    }

    @Override
    public Condition getCondition() {
        if (comboBox.isEmpty()) {
            return DSL.noCondition();
        }

        return field.eq(comboBox.getValue().getBooleanValue());
    }

    @Override
    public void addValueChangeListener(Runnable listener) {
        comboBox.addValueChangeListener(_ -> listener.run());
    }

    @Override
    public void clear() {
        comboBox.clear();
    }

    @Getter
    @RequiredArgsConstructor
    private enum BooleanState {

        @DisplayName("True")
        TRUE(Boolean.TRUE),

        @DisplayName("False")
        FALSE(Boolean.FALSE),

        @DisplayName("Unset")
        UNSET(null);

        private final Boolean booleanValue;

    }

}
