package echoflux.domain.access_management.role.service.impl;

import echoflux.domain.access_management.role.service.RoleService;
import echoflux.domain.access_management.role.service.SaveRoleCommand;
import echoflux.domain.jooq.tables.pojos.Role;
import echoflux.domain.jooq.tables.records.RolePermissionRecord;
import lombok.RequiredArgsConstructor;
import org.apache.commons.collections4.SetUtils;
import org.jooq.DSLContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Set;

import static echoflux.domain.jooq.Tables.ROLE;
import static echoflux.domain.jooq.Tables.ROLE_PERMISSION;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RoleServiceImpl implements RoleService {

    private final DSLContext ctx;

    @Override
    public Role getById(Long id) {
        return ctx.fetchSingle(ROLE, ROLE.ID.eq(id))
                .into(Role.class);
    }

    @Transactional
    @Override
    public Long save(SaveRoleCommand command) {
        boolean createMode = command.getId() == null;

        var roleRecord = createMode
                ? ctx.newRecord(ROLE)
                : ctx.fetchSingle(ROLE, ROLE.ID.eq(command.getId()));

        roleRecord.setName(command.getName());
        roleRecord.setDescription(command.getDescription());
        roleRecord.store();


        var newPermissionIds = command.getPermissionIds();
        Set<Long> oldPermissionIds;
        if (createMode) {
            oldPermissionIds = Set.of();
        } else {
            oldPermissionIds = ctx.select(ROLE_PERMISSION.PERMISSION_ID)
                    .from(ROLE_PERMISSION)
                    .where(ROLE_PERMISSION.ROLE_ID.eq(roleRecord.getId()))
                    .fetchSet(ROLE_PERMISSION.PERMISSION_ID);
        }

        var permissionIdsToInsert = SetUtils.difference(newPermissionIds, oldPermissionIds);
        var permissionIdsToDelete = SetUtils.difference(oldPermissionIds, newPermissionIds);

        if (!permissionIdsToDelete.isEmpty()) {
            ctx.deleteFrom(ROLE_PERMISSION)
                    .where(ROLE_PERMISSION.ROLE_ID.eq(roleRecord.getId()))
                    .and(ROLE_PERMISSION.PERMISSION_ID.in(permissionIdsToDelete))
                    .execute();
        }

        if (!permissionIdsToInsert.isEmpty()) {
            var rolePermissionRecordsToInsert = new ArrayList<RolePermissionRecord>(permissionIdsToInsert.size());

            for (var permissionId : permissionIdsToInsert) {
                var rolePermissionRecord = ctx.newRecord(ROLE_PERMISSION);
                rolePermissionRecord.setRoleId(roleRecord.getId());
                rolePermissionRecord.setPermissionId(permissionId);
                rolePermissionRecordsToInsert.add(rolePermissionRecord);
            }

            ctx.batchInsert(rolePermissionRecordsToInsert).execute();
        }

        return roleRecord.getId();
    }

}
