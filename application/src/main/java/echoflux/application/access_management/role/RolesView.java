package echoflux.application.access_management.role;

import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import echoflux.application.core.jooq.grid.JooqGrid;
import echoflux.application.core.security.AuthenticatedUser;
import echoflux.application.layout.MainLayout;
import echoflux.core.core.bean.accessor.BeanAccessor;
import echoflux.domain.core.security.PermissionType;
import echoflux.domain.core.security.RequiredPermissions;
import org.jooq.DSLContext;

import static echoflux.domain.jooq.Tables.V_ROLE;

@PageTitle("Roles")
@Route(value = "roles", layout = MainLayout.class)
@RequiredPermissions(PermissionType.ACCESS_MANAGEMENT_ROLES_VIEW)
public class RolesView extends VerticalLayout {

    public RolesView(DSLContext ctx, BeanAccessor beanAccessor) {
        var grid = new JooqGrid<>(ctx, V_ROLE, V_ROLE.ID);
        grid.setDefaultOrderBy(V_ROLE.NAME);
        grid.addColumn(V_ROLE.NAME).setDefaultFilter();
        grid.addColumn(V_ROLE.DESCRIPTION).setDefaultFilter();
        grid.addColumn(V_ROLE.PERMISSIONS).setDefaultFilter();
        grid.addIdColumn().setDefaultFilter();
        grid.addAuditColumns().forEach(JooqGrid.JooqGridColumn::setDefaultFilter);

        if (AuthenticatedUser.checkPermission(PermissionType.ACCESS_MANAGEMENT_ROLE_UPDATE)) {
            grid.addContextMenuItemWithDoubleClickListener(
                    "Edit",
                    item -> SaveRoleDialog.newUpdate(item.getId(), beanAccessor)
                            .withSaveListener(grid::refreshItemById)
                            .open()
            );

        }

        var gridControls = grid.withControls();
        if (AuthenticatedUser.checkPermission(PermissionType.ACCESS_MANAGEMENT_ROLE_CREATE)) {
            gridControls.addCreateButton(
                    () -> SaveRoleDialog.newCreate(beanAccessor)
                            .withSaveListener(_ -> grid.refreshAll())
                            .open()
            );
        }

        addAndExpand(gridControls);
    }

}
