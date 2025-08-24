package echoflux.domain.access_management.application_user.service.impl;

import echoflux.domain.jooq.tables.pojos.ApplicationUser;
import echoflux.domain.jooq.tables.records.ApplicationUserRoleRecord;
import lombok.RequiredArgsConstructor;
import org.apache.commons.collections4.SetUtils;
import org.jooq.DSLContext;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import echoflux.domain.access_management.application_user.service.ApplicationUserService;
import echoflux.domain.access_management.application_user.service.ChangePasswordCommand;
import echoflux.domain.access_management.application_user.service.CreateApplicationUserCommand;
import echoflux.domain.access_management.application_user.service.UpdateApplicationUserCommand;

import java.util.ArrayList;

import static echoflux.domain.jooq.Tables.APPLICATION_USER;
import static echoflux.domain.jooq.Tables.APPLICATION_USER_ROLE;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ApplicationUserServiceImpl implements ApplicationUserService {

    private final DSLContext ctx;
    private final PasswordEncoder passwordEncoder;

    @Override
    public ApplicationUser getById(Long id) {
        return ctx.fetchSingle(APPLICATION_USER, APPLICATION_USER.ID.eq(id))
                .into(ApplicationUser.class);
    }

    @Override
    @Transactional
    public Long create(CreateApplicationUserCommand command) {
        var hashedPassword = passwordEncoder.encode(command.getPassword());

        var userRecord = ctx.newRecord(APPLICATION_USER);
        userRecord.setUsername(command.getUsername());
        userRecord.setName(command.getName());
        userRecord.setPassword(hashedPassword);
        userRecord.setEnabled(command.getEnabled());
        userRecord.setCountry(command.getCountry());
        userRecord.setZoneId(command.getZoneId());
        userRecord.store();

        if (!command.getRoleIds().isEmpty()) {
            var applicationUserRoleRecords = new ArrayList<ApplicationUserRoleRecord>(command.getRoleIds().size());

            for (var roleId : command.getRoleIds()) {
                var applicationUserRoleRecord = ctx.newRecord(APPLICATION_USER_ROLE);
                applicationUserRoleRecord.setApplicationUserId(userRecord.getId());
                applicationUserRoleRecord.setRoleId(roleId);
                applicationUserRoleRecords.add(applicationUserRoleRecord);
            }

            ctx.batchInsert(applicationUserRoleRecords).execute();
        }

        return userRecord.getId();
    }

    @Override
    @Transactional
    public Long update(UpdateApplicationUserCommand command) {
        var userRecord = ctx.fetchSingle(APPLICATION_USER, APPLICATION_USER.ID.eq(command.getId()));
        userRecord.setUsername(command.getUsername());
        userRecord.setName(command.getName());
        userRecord.setEnabled(command.getEnabled());
        userRecord.setCountry(command.getCountry());
        userRecord.setZoneId(command.getZoneId());
        userRecord.update();

        var newRoleIds = command.getRoleIds();
        var oldRoleIds = ctx.select(APPLICATION_USER_ROLE.ROLE_ID)
                .from(APPLICATION_USER_ROLE)
                .where(APPLICATION_USER_ROLE.APPLICATION_USER_ID.eq(command.getId()))
                .fetchSet(APPLICATION_USER_ROLE.ROLE_ID);

        var roleIdsToInsert = SetUtils.difference(newRoleIds, oldRoleIds);
        var roleIdsToDelete = SetUtils.difference(oldRoleIds, newRoleIds);

        if (!roleIdsToDelete.isEmpty()) {
            ctx.deleteFrom(APPLICATION_USER_ROLE)
                    .where(APPLICATION_USER_ROLE.APPLICATION_USER_ID.eq(command.getId()))
                    .and(APPLICATION_USER_ROLE.ROLE_ID.in(roleIdsToDelete))
                    .execute();
        }

        if (!roleIdsToInsert.isEmpty()) {
            var applicationUserRoleRecordsToInsert = new ArrayList<ApplicationUserRoleRecord>(roleIdsToInsert.size());

            for (var roleId : roleIdsToInsert) {
                var applicationUserRoleRecord = ctx.newRecord(APPLICATION_USER_ROLE);
                applicationUserRoleRecord.setApplicationUserId(userRecord.getId());
                applicationUserRoleRecord.setRoleId(roleId);
                applicationUserRoleRecordsToInsert.add(applicationUserRoleRecord);
            }

            ctx.batchInsert(applicationUserRoleRecordsToInsert).execute();
        }

        return userRecord.getId();
    }

    @Override
    @Transactional
    public Long changePassword(ChangePasswordCommand command) {
        var hashedPassword = passwordEncoder.encode(command.getPassword());

        var userRecord = ctx.fetchSingle(APPLICATION_USER, APPLICATION_USER.ID.eq(command.getId()));
        userRecord.setPassword(hashedPassword);
        userRecord.update();

        return userRecord.getId();
    }

}
