package echoflux.application.access_management.data_provider;

import com.vaadin.flow.data.provider.AbstractBackEndDataProvider;
import com.vaadin.flow.data.provider.Query;
import echoflux.core.core.validate.guard.Guard;
import echoflux.application.access_management.role.RoleEntry;
import echoflux.domain.core.security.Endpoint;
import lombok.RequiredArgsConstructor;
import org.jooq.Condition;
import org.jooq.DSLContext;
import org.jooq.Records;
import org.jooq.impl.DSL;

import java.util.Set;
import java.util.stream.Stream;

import static echoflux.domain.jooq.Tables.APPLICATION_USER_ROLE;
import static echoflux.domain.jooq.Tables.ROLE;

@Endpoint
@RequiredArgsConstructor
public class RoleEntryDataProvider extends AbstractBackEndDataProvider<RoleEntry, String> {

    private final DSLContext ctx;

    public Set<RoleEntry> findEntriesByApplicationUserId(Long applicationUserId) {
        return ctx.select(ROLE.ID, ROLE.NAME, ROLE.DESCRIPTION)
                .from(ROLE)
                .join(APPLICATION_USER_ROLE).on(ROLE.ID.eq(APPLICATION_USER_ROLE.ROLE_ID))
                .where(APPLICATION_USER_ROLE.APPLICATION_USER_ID.eq(applicationUserId))
                .fetchSet(Records.mapping(RoleEntry::new));
    }

    @Override
    protected Stream<RoleEntry> fetchFromBackEnd(Query<RoleEntry, String> query) {
        var condition = newCondition(query);

        return ctx.select(ROLE.ID, ROLE.NAME, ROLE.DESCRIPTION)
                .from(ROLE)
                .where(condition)
                .orderBy(ROLE.NAME)
                .offset(query.getOffset())
                .limit(query.getLimit())
                .fetch(Records.mapping(RoleEntry::new))
                .stream();
    }

    @Override
    protected int sizeInBackEnd(Query<RoleEntry, String> query) {
        var condition = newCondition(query);

        return ctx.fetchCount(ROLE, condition);
    }

    private static Condition newCondition(Query<RoleEntry, String> query) {
        Guard.notNull(query, "query");

        if (query.getFilter().isEmpty()) {
            return DSL.noCondition();
        }

        var filter = query.getFilter().get();
        var nameCondition = ROLE.NAME.containsIgnoreCase(filter);
        var descriptionCondition = ROLE.DESCRIPTION.containsIgnoreCase(filter);

        return DSL.or(nameCondition, descriptionCondition);
    }

}
