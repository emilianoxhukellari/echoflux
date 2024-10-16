package transcribe.application.core.jpa.filter.impl;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.value.ValueChangeMode;
import jakarta.persistence.criteria.JoinType;
import lombok.Getter;
import org.springframework.data.jpa.domain.Specification;
import transcribe.application.core.jpa.filter.JpaFilter;

@Getter
public class TextJpaFilter<T> extends JpaFilter<T> {

    private final TextField textField;

    public TextJpaFilter(String property, boolean asCollection) {
        super(property, asCollection);
        this.textField = new TextField();

        textField.setPlaceholder("Filter");
        textField.setWidth("10.6rem");
        textField.setClearButtonVisible(true);
        textField.setValueChangeMode(ValueChangeMode.EAGER);
    }

    @Override
    public Specification<T> getSpecification() {
        if (textField.isEmpty()) {
            return (_, _, criteriaBuilder) -> criteriaBuilder.conjunction();
        }
        var pattern = "%" + textField.getValue().toLowerCase() + "%";

        return asCollection
                ? (root, _, criteriaBuilder) ->
                criteriaBuilder.like(criteriaBuilder.lower(root.join(property, JoinType.LEFT).as(String.class)), pattern)
                : (root, _, criteriaBuilder) ->
                criteriaBuilder.like(criteriaBuilder.lower(root.get(property).as(String.class)), pattern);
    }

    @Override
    public Component getComponent() {
        return textField;
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
