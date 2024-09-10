package transcribe.application.core.jpa.filter;


import com.vaadin.flow.component.Component;
import lombok.Getter;
import org.apache.commons.lang3.Validate;
import org.springframework.data.jpa.domain.Specification;

public abstract class JpaFilter<T> {

    @Getter
    protected final String property;
    protected final boolean asCollection;

    public JpaFilter(String property, boolean ofCollection) {
        this.property = Validate.notBlank(property);
        this.asCollection = ofCollection;
    }

    public abstract Specification<T> getSpecification();

    public abstract Component getComponent();

    public abstract void addValueChangeListener(Runnable listener);

    public abstract void clear();

}
