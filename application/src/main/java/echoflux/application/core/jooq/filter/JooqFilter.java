package echoflux.application.core.jooq.filter;

import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import echoflux.core.core.validate.guard.Guard;
import lombok.Getter;
import org.jooq.Condition;
import org.jooq.Field;

@Getter
public abstract class JooqFilter<T> extends HorizontalLayout {

    protected final Field<T> field;

    public JooqFilter(Field<T> field) {
        this.field = Guard.notNull(field, "field");

        setPadding(false);
        setSpacing(false);
    }

    public abstract Condition getCondition();

    public abstract void addValueChangeListener(Runnable listener);

    public abstract void clear();

}
