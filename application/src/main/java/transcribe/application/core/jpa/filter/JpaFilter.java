package transcribe.application.core.jpa.filter;


import com.vaadin.flow.component.Component;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.Validate;
import org.springframework.data.jpa.domain.Specification;
import transcribe.core.common.no_op.NoOp;

@Getter
@Setter
public abstract class JpaFilter<T> {

    protected final String property;
    protected final boolean asCollection;
    protected Runnable listener = NoOp.runnable();

    public JpaFilter(String property, boolean ofCollection) {
        this.property = Validate.notBlank(property);
        this.asCollection = ofCollection;
    }

    public abstract Specification<T> getSpecification();

    public abstract Component getComponent();

}
