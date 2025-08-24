package echoflux.application.core.jooq.filter;

import echoflux.core.core.validate.guard.Guard;
import org.jooq.Condition;
import org.jooq.impl.DSL;

import java.util.List;

public record CombinedJooqFilter(List<JooqFilter<?>> filters) {

    public CombinedJooqFilter {
        Guard.notNull(filters, "filters");
    }

    public static CombinedJooqFilter empty() {
        return new CombinedJooqFilter(List.of());
    }

    public static CombinedJooqFilter of(List<JooqFilter<?>> filters) {
        return new CombinedJooqFilter(filters);
    }

    public Condition getCondition() {
        return filters.stream()
                .map(JooqFilter::getCondition)
                .reduce(Condition::and)
                .orElse(DSL.noCondition());
    }

}
