package echoflux.application.core.jpa.filter.impl;

import com.vaadin.flow.component.combobox.ComboBox;
import org.apache.commons.lang3.Validate;
import org.springframework.data.jpa.domain.Specification;
import echoflux.application.core.jpa.filter.JpaFilter;
import echoflux.application.core.jpa.filter.JpaFilterUtils;
import echoflux.core.core.utils.EfArrays;
import echoflux.core.core.utils.EfEnums;

public class EnumJpaFilter<ENTITY> extends JpaFilter<ENTITY> {

    private final ComboBox<Enum<?>> comboBox;

    public EnumJpaFilter(String attribute, String property, Class<?> enumClass, boolean asCollection) {
        super(attribute, property, asCollection);
        Validate.isTrue(enumClass.isEnum(), "Class must be an enum");

        this.comboBox = new ComboBox<>();
        comboBox.setItems(EfArrays.collect(enumClass.getEnumConstants(), v -> (Enum<?>) v));
        comboBox.setItemLabelGenerator(EfEnums::toDisplayName);
        comboBox.setPlaceholder("Filter");
        comboBox.setClearButtonVisible(true);
        comboBox.setMinWidth("0");

        addAndExpand(comboBox);
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
    public void addValueChangeListener(Runnable listener) {
        comboBox.addValueChangeListener(_ -> listener.run());
    }

    @Override
    public void clear() {
        comboBox.clear();
    }

}
