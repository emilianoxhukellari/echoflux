package transcribe.application.core.jpa.filter.impl;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.combobox.ComboBox;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import transcribe.application.core.jpa.filter.JpaFilter;

public class BooleanJpaFilter<T> extends JpaFilter<T> {

    private final ComboBox<BooleanState> comboBox;

    public BooleanJpaFilter(String property, boolean asCollection) {
        super(property, asCollection);

        this.comboBox = new ComboBox<>();
        comboBox.setItems(BooleanState.values());
        comboBox.setItemLabelGenerator(BooleanState::getPrettyName);
        comboBox.setPlaceholder("Filter");
        comboBox.setWidth("10.6rem");
        comboBox.setClearButtonVisible(true);
    }

    @Override
    public Specification<T> getSpecification() {
        if (comboBox.isEmpty()) {
            return (_, _, criteriaBuilder) -> criteriaBuilder.conjunction();
        }

        return asCollection
                ? (root, _, criteriaBuilder) -> criteriaBuilder.isMember(comboBox.getValue().getBooleanValue(), root.get(property))
                : (root, _, criteriaBuilder) -> criteriaBuilder.equal(root.get(property), comboBox.getValue().getBooleanValue());
    }

    @Override
    public Component getComponent() {
        return comboBox;
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

        TRUE(Boolean.TRUE, "True"),
        FALSE(Boolean.FALSE, "False"),
        UNSET(null, "Unset");

        private final Boolean booleanValue;
        private final String prettyName;

    }

}
