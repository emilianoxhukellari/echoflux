package echoflux.application.core.jpa.filter.impl;

import com.vaadin.flow.component.combobox.ComboBox;
import echoflux.core.core.validate.guard.Guard;
import echoflux.domain.core.criteria.CriteriaPathResolver;
import org.springframework.data.jpa.domain.Specification;
import echoflux.application.core.jpa.filter.JpaFilter;
import echoflux.core.core.utils.MoreArrays;
import echoflux.core.core.utils.MoreEnums;

public class EnumJpaFilter<ENTITY> extends JpaFilter<ENTITY> {

    private final ComboBox<Enum<?>> comboBox;

    public EnumJpaFilter(String property, Class<?> enumClass, boolean asCollection) {
        super(property, asCollection);
        Guard.enumType(enumClass);

        this.comboBox = new ComboBox<>();
        comboBox.setItems(MoreArrays.collect(enumClass.getEnumConstants(), v -> (Enum<?>) v));
        comboBox.setItemLabelGenerator(MoreEnums::toDisplayName);
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

        if (asCollection) {
            return (root, _, cb) -> cb.isMember(comboBox.getValue(), CriteriaPathResolver.resolve(root, property));
        }

        return (root, _, cb) -> cb.equal(CriteriaPathResolver.resolve(root, property), comboBox.getValue());
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
