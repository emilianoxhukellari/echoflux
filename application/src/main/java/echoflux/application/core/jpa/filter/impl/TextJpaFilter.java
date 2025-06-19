package echoflux.application.core.jpa.filter.impl;

import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.value.ValueChangeMode;
import echoflux.domain.core.criteria.CriteriaPathResolver;
import jakarta.persistence.criteria.JoinType;
import lombok.Getter;
import org.springframework.data.jpa.domain.Specification;
import echoflux.application.core.jpa.filter.JpaFilter;

@Getter
public class TextJpaFilter<E> extends JpaFilter<E> {

    private final TextField textField;

    public TextJpaFilter(String property, boolean asCollection) {
        super(property, asCollection);
        this.textField = new TextField();

        textField.setPlaceholder("Filter");
        textField.setClearButtonVisible(true);
        textField.setValueChangeMode(ValueChangeMode.LAZY);
        textField.setMinWidth("0");

        addAndExpand(textField);
    }

    @Override
    public Specification<E> getSpecification() {
        if (textField.isEmpty()) {
            return (_, _, cb) -> cb.conjunction();
        }
        var pattern = "%" + textField.getValue().toLowerCase() + "%";

        return (root, _, cb) -> {
            var path = CriteriaPathResolver.resolve(root, property, JoinType.LEFT, asCollection);

            return cb.like(cb.lower(path.as(String.class)), pattern);
        };
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
