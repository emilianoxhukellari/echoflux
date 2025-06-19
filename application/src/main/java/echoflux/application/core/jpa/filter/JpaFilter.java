package echoflux.application.core.jpa.filter;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import lombok.Getter;
import org.apache.commons.lang3.Validate;
import org.springframework.data.jpa.domain.Specification;

public abstract class JpaFilter<E> extends HorizontalLayout {

    @Getter
    protected final String property;
    protected final boolean asCollection;

    public JpaFilter(String property, boolean asCollection) {
        this.property = Validate.notBlank(property);
        this.asCollection = asCollection;

        setPadding(false);
        setSpacing(false);
    }

    public abstract Specification<E> getSpecification();

    public abstract void addValueChangeListener(Runnable listener);

    public abstract void clear();

}
