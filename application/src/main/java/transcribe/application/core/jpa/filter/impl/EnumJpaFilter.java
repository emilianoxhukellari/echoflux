package transcribe.application.core.jpa.filter.impl;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.combobox.ComboBox;
import org.apache.commons.lang3.Validate;
import org.springframework.data.jpa.domain.Specification;
import transcribe.application.core.jpa.filter.JpaFilter;
import transcribe.application.core.jpa.filter.JpaFilterUtils;
import transcribe.core.core.utils.MoreArrays;
import transcribe.core.core.utils.MoreEnums;

public class EnumJpaFilter<ENTITY> extends JpaFilter<ENTITY> {

    private final ComboBox<Enum<?>> comboBox;

    public EnumJpaFilter(String attribute, String property, Class<?> enumClass, boolean asCollection) {
        super(attribute, property, asCollection);
        Validate.isTrue(enumClass.isEnum(), "Class must be an enum");

        this.comboBox = new ComboBox<>();
        comboBox.setItems(MoreArrays.collect(enumClass.getEnumConstants(), v -> (Enum<?>) v));
        comboBox.setItemLabelGenerator(MoreEnums::toDisplayName);
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
                -> criteriaBuilder.isMember(comboBox.getValue(), JpaFilterUtils.get(root, attribute))
                : (root, _, criteriaBuilder)
                -> criteriaBuilder.equal(JpaFilterUtils.get(root, attribute), comboBox.getValue());
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
