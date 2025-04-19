package echoflux.application.core.jpa.filter.impl;

import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.value.ValueChangeMode;
import jakarta.persistence.criteria.JoinType;
import lombok.Getter;
import org.springframework.data.jpa.domain.Specification;
import echoflux.application.core.jpa.filter.JpaFilter;
import echoflux.application.core.jpa.filter.JpaFilterUtils;

@Getter
public class TextJpaFilter<ENTITY> extends JpaFilter<ENTITY> {

    private final TextField textField;

    public TextJpaFilter(String attribute, String property, boolean asCollection) {
        super(attribute, property, asCollection);
        this.textField = new TextField();

        textField.setPlaceholder("Filter");
        textField.setClearButtonVisible(true);
        textField.setValueChangeMode(ValueChangeMode.LAZY);
        textField.setMinWidth("0");

        addAndExpand(textField);
    }

    @Override
    public Specification<ENTITY> getSpecification() {
        if (textField.isEmpty()) {
            return (_, _, criteriaBuilder) -> criteriaBuilder.conjunction();
        }
        var pattern = "%" + textField.getValue().toLowerCase() + "%";

        return asCollection
                ? (root, _, criteriaBuilder) ->
                criteriaBuilder.like(criteriaBuilder.lower(root.join(attribute, JoinType.LEFT).as(String.class)), pattern)
                : (root, _, criteriaBuilder) ->
                criteriaBuilder.like(criteriaBuilder.lower(JpaFilterUtils.get(root, attribute).as(String.class)), pattern);
    }

    @Override
    public void addValueChangeListener(Runnable listener) {
        textField.addValueChangeListener(_ -> listener.run());
    }

    @Override
    public void clear() {
        textField.clear();
    }

}
