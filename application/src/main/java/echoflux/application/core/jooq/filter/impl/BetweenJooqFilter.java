package echoflux.application.core.jooq.filter.impl;

import echoflux.application.core.jooq.filter.JooqFilter;
import org.jooq.Condition;
import org.jooq.Field;
import org.jooq.impl.DSL;

public abstract class BetweenJooqFilter<T> extends JooqFilter<T> {

    public BetweenJooqFilter(Field<T> field) {
        super(field);
    }

    protected abstract T getFrom();

    protected abstract T getTo();

    @Override
    public Condition getCondition() {
        Condition condition = DSL.noCondition();

        if (getFrom() != null) {
            condition = condition.and(field.ge(getFrom()));
        }

        if (getTo() != null) {
            condition = condition.and(field.le(getTo()));
        }

        return condition;
    }

}
