package echoflux.application.core.jpa.filter.impl;

import com.vaadin.flow.component.combobox.ComboBox;
import echoflux.domain.core.criteria.CriteriaPathResolver;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import echoflux.application.core.jpa.filter.JpaFilter;
import echoflux.core.core.display_name.DisplayName;
import echoflux.core.core.utils.MoreEnums;

public class BooleanJpaFilter<E> extends JpaFilter<E> {

    private final ComboBox<BooleanState> comboBox;

    public BooleanJpaFilter(String property, boolean asCollection) {
        super(property, asCollection);

        this.comboBox = new ComboBox<>();
        comboBox.setItems(BooleanState.values());
        comboBox.setItemLabelGenerator(MoreEnums::toDisplayName);
        comboBox.setPlaceholder("Filter");
        comboBox.setClearButtonVisible(true);

        addAndExpand(comboBox);
    }

    @Override
    public Specification<E> getSpecification() {
        if (comboBox.isEmpty()) {
            return (_, _, criteriaBuilder) -> criteriaBuilder.conjunction();
        }

        return (root, _, cb) -> {
            if (asCollection) {
                return cb.isMember(comboBox.getValue().getBooleanValue(), CriteriaPathResolver.resolve(root, property));
            } else {
                return cb.equal(CriteriaPathResolver.resolve(root, property), comboBox.getValue().getBooleanValue());
            }
        };
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
