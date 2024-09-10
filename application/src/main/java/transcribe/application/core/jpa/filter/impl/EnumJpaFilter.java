package transcribe.application.core.jpa.filter.impl;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.combobox.ComboBox;
import org.apache.commons.lang3.Validate;
import org.springframework.data.jpa.domain.Specification;
import transcribe.application.core.jpa.filter.JpaFilter;

public class EnumJpaFilter<T> extends JpaFilter<T> {

    private final ComboBox<Object> comboBox;

    public EnumJpaFilter(String property, Class<?> enumClass, boolean asCollection) {
        super(property, asCollection);
        Validate.isTrue(enumClass.isEnum(), "Class must be an enum");

        this.comboBox = new ComboBox<>();
        this.comboBox.setItems(enumClass.getEnumConstants());
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
                ? (root, _, criteriaBuilder) -> criteriaBuilder.isMember(comboBox.getValue(), root.get(property))
                : (root, _, criteriaBuilder) -> criteriaBuilder.equal(root.get(property), comboBox.getValue());
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

}
