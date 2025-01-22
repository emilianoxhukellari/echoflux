package transcribe.application.core.jpa.filter.impl;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.combobox.ComboBox;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import transcribe.application.core.jpa.filter.JpaFilter;
import transcribe.application.core.jpa.filter.JpaFilterUtils;

public class BooleanJpaFilter<ENTITY> extends JpaFilter<ENTITY> {

    private final ComboBox<BooleanState> comboBox;

    public BooleanJpaFilter(String attribute, String property, boolean asCollection) {
        super(attribute, property, asCollection);

        this.comboBox = new ComboBox<>();
        comboBox.setItems(BooleanState.values());
        comboBox.setItemLabelGenerator(BooleanState::getPrettyName);
        comboBox.setPlaceholder("Filter");
        comboBox.setWidth("10.6rem");
        comboBox.setClearButtonVisible(true);
    }

    @Override
    public Specification<ENTITY> getSpecification() {
        if (comboBox.isEmpty()) {
            return (_, _, criteriaBuilder) -> criteriaBuilder.conjunction();
        }

        return asCollection
                ? (root, _, criteriaBuilder)
                -> criteriaBuilder.isMember(comboBox.getValue().getBooleanValue(), JpaFilterUtils.get(root, attribute))
                : (root, _, criteriaBuilder)
                -> criteriaBuilder.equal(JpaFilterUtils.get(root, attribute), comboBox.getValue().getBooleanValue());
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
