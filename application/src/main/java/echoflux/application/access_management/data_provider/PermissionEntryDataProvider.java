package echoflux.application.access_management.data_provider;

import com.vaadin.flow.data.provider.AbstractBackEndDataProvider;
import com.vaadin.flow.data.provider.Query;
import echoflux.application.access_management.permission.PermissionEntry;
import echoflux.core.core.utils.MoreStrings;
import echoflux.core.core.validate.guard.Guard;
import echoflux.domain.core.security.Endpoint;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.jooq.Condition;
import org.jooq.DSLContext;
import org.jooq.Records;
import org.jooq.impl.DSL;

import java.util.Set;
import java.util.stream.Stream;

import static echoflux.domain.jooq.Tables.PERMISSION;
import static echoflux.domain.jooq.Tables.ROLE_PERMISSION;

@Endpoint
@RequiredArgsConstructor
public class PermissionEntryDataProvider extends AbstractBackEndDataProvider<PermissionEntry, String> {

    private final DSLContext ctx;

    public Set<PermissionEntry> findEntriesByRoleId(Long roleId) {
        return ctx.select(PERMISSION.ID, PERMISSION.TYPE, PERMISSION.DESCRIPTION)
                .from(PERMISSION)
                .join(ROLE_PERMISSION).on(PERMISSION.ID.eq(ROLE_PERMISSION.PERMISSION_ID))
                .where(ROLE_PERMISSION.ROLE_ID.eq(roleId))
                .fetchSet(Records.mapping(PermissionEntry::new));
    }

    @Override
    protected Stream<PermissionEntry> fetchFromBackEnd(Query<PermissionEntry, String> query) {
        var condition = newCondition(query);

        return ctx.select(PERMISSION.ID, PERMISSION.TYPE, PERMISSION.DESCRIPTION)
                .from(PERMISSION)
                .where(condition)
                .orderBy(PERMISSION.TYPE)
                .offset(query.getOffset())
                .limit(query.getLimit())
                .fetch(Records.mapping(PermissionEntry::new))
                .stream();
    }

    @Override
    protected int sizeInBackEnd(Query<PermissionEntry, String> query) {
        var condition = newCondition(query);

        return ctx.fetchCount(PERMISSION, condition);
    }

    private static Condition newCondition(Query<PermissionEntry, String> query) {
        Guard.notNull(query, "query");

        if (query.getFilter().isEmpty()) {
            return DSL.noCondition();
        }

        var normalizedFilter = StringUtils.replace(query.getFilter().get(), MoreStrings.UNDERSCORE, MoreStrings.SPACE);
        var normalizedTypeField = DSL.replace(PERMISSION.TYPE.coerce(String.class), MoreStrings.UNDERSCORE, MoreStrings.SPACE);

        var typeCondition = normalizedTypeField.containsIgnoreCase(normalizedFilter);
        var descriptionCondition = PERMISSION.DESCRIPTION.containsIgnoreCase(normalizedFilter);

        return DSL.or(typeCondition, descriptionCondition);
    }

}
