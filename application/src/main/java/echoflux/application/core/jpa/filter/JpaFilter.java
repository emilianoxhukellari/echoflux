package echoflux.application.core.jpa.filter;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import lombok.Getter;
import org.apache.commons.lang3.Validate;
import org.springframework.data.jpa.domain.Specification;

public abstract class JpaFilter<ENTITY> extends HorizontalLayout {

    @Getter
    protected final String attribute;
    @Getter
    protected final String property;
    protected final boolean asCollection;

    public JpaFilter(String attribute, String property, boolean ofCollection) {
        this.attribute = Validate.notBlank(attribute);
        this.property = Validate.notBlank(property);
        this.asCollection = ofCollection;

        setPadding(false);
        setSpacing(false);
    }

    public abstract Specification<ENTITY> getSpecification();

    public abstract void addValueChangeListener(Runnable listener);

    public abstract void clear();

}
