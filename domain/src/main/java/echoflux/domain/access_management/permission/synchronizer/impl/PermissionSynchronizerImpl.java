package echoflux.domain.access_management.permission.synchronizer.impl;

import echoflux.core.core.initialize.Initialize;
import echoflux.core.core.initialize.InitializeOrder;
import echoflux.core.core.utils.MoreLists;
import echoflux.domain.access_management.permission.synchronizer.PermissionSynchronizer;
import echoflux.domain.core.security.PermissionType;
import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.jooq.impl.DSL;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static echoflux.domain.jooq.tables.Permission.PERMISSION;

@Component
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PermissionSynchronizerImpl implements PermissionSynchronizer, Initialize {

    private final DSLContext ctx;

    @Transactional
    @Override
    public void synchronizeAll() {
        var permissions = List.of(PermissionType.values());
        var keys = MoreLists.collect(permissions, PermissionType::getKey);

        ctx.deleteFrom(PERMISSION)
                .where(PERMISSION.KEY.notIn(keys))
                .execute();

        var upsert = ctx.insertInto(PERMISSION, PERMISSION.KEY, PERMISSION.TYPE, PERMISSION.DESCRIPTION);

        for (var p : permissions) {
            upsert = upsert.values(p.getKey(), p, p.getDescription());
        }

        upsert.onConflict(PERMISSION.KEY)
                .doUpdate()
                .set(PERMISSION.TYPE, DSL.excluded(PERMISSION.TYPE))
                .set(PERMISSION.DESCRIPTION, DSL.excluded(PERMISSION.DESCRIPTION))
                .execute();
    }

    @Transactional
    @Override
    public void initialize() {
        synchronizeAll();
    }

    @Override
    public InitializeOrder getOrder() {
        return InitializeOrder.LAST;
    }

}
