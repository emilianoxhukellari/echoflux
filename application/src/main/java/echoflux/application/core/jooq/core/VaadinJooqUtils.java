package echoflux.application.core.jooq.core;

import com.vaadin.flow.data.provider.Query;
import com.vaadin.flow.data.provider.SortDirection;
import echoflux.core.core.utils.MoreStrings;
import echoflux.core.core.validate.guard.Guard;
import echoflux.domain.core.jooq.core.JooqUtils;
import org.apache.commons.lang3.StringUtils;
import org.jooq.Field;
import org.jooq.OrderField;
import org.jooq.Table;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public final class VaadinJooqUtils {

    public static List<OrderField<?>> toOrderFields(Query<?, ?> query, Table<?> table) {
        Guard.notNull(query, "query");

        return query.getSortOrders()
                .stream()
                .map(s -> {
                    var field = JooqUtils.getFieldByQualifiedName(table, s.getSorted());

                    return s.getDirection() == SortDirection.DESCENDING
                            ? field.desc()
                            : field;
                })
                .toList();
    }

    public static <T> String toDisplayName(Field<T> field) {
        Guard.notNull(field, "field");

        return Arrays.stream(StringUtils.split(field.getName(), MoreStrings.UNDERSCORE))
                .map(StringUtils::capitalize)
                .collect(Collectors.joining(MoreStrings.SPACE));
    }

}
