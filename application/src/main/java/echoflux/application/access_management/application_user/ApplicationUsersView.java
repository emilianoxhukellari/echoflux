package echoflux.application.access_management.application_user;

import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import echoflux.application.core.jooq.grid.JooqGrid;
import echoflux.application.core.security.AuthenticatedUser;
import echoflux.domain.core.security.PermissionType;
import echoflux.domain.core.security.RequiredPermissions;
import echoflux.application.layout.MainLayout;
import echoflux.core.core.bean.accessor.BeanAccessor;
import org.jooq.DSLContext;

import static echoflux.domain.jooq.Tables.V_APPLICATION_USER;

@PageTitle("Application Users")
@Route(value = "application-users", layout = MainLayout.class)
@RequiredPermissions(PermissionType.ACCESS_MANAGEMENT_APPLICATION_USERS_VIEW)
public class ApplicationUsersView extends VerticalLayout {

    public ApplicationUsersView(DSLContext ctx, BeanAccessor beanAccessor) {
        var grid = new JooqGrid<>(ctx, V_APPLICATION_USER, V_APPLICATION_USER.ID);
        grid.addColumn(V_APPLICATION_USER.NAME).setDefaultFilter();
        grid.addColumn(V_APPLICATION_USER.USERNAME).setDefaultFilter();
        grid.addColumn(V_APPLICATION_USER.ENABLED).setDefaultFilter();
        grid.addColumn(V_APPLICATION_USER.COUNTRY).setDefaultFilter();
        grid.addColumn(V_APPLICATION_USER.ZONE_ID).setDefaultFilter();
        grid.addColumn(V_APPLICATION_USER.ROLES).setDefaultFilter();
        grid.addColumn(V_APPLICATION_USER.PERMISSIONS).setDefaultFilter();
        grid.addIdColumn().setDefaultFilter();
        grid.addAuditColumns().forEach(JooqGrid.JooqGridColumn::setDefaultFilter);

        if (AuthenticatedUser.checkPermission(PermissionType.ACCESS_MANAGEMENT_APPLICATION_USER_UPDATE)) {
            grid.addContextMenuItemWithDoubleClickListener(
                    "Edit",
                    item -> new UpdateUserDialog(item.getId(), beanAccessor)
                            .withSaveListener(grid::refreshItemById)
                            .open()
            );
        }
        if (AuthenticatedUser.checkAllPermissions(PermissionType.ACCESS_MANAGEMENT_APPLICATION_USER_UPDATE)) {
            grid.addContextMenuItem(
                    "Change password",
                    r -> new ChangePasswordDialog(r.getId(), beanAccessor).open()
            );
        }

        var gridControls = grid.withControls();
        if (AuthenticatedUser.checkAllPermissions(PermissionType.ACCESS_MANAGEMENT_APPLICATION_USER_CREATE)) {
            gridControls.addCreateButton(
                    () -> new CreateUserDialog(beanAccessor)
                            .withSaveListener(_ -> grid.refreshAll())
                            .open()
            );
        }

        addAndExpand(gridControls);
    }

}
