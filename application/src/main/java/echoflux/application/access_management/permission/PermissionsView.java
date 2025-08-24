package echoflux.application.access_management.permission;

import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import echoflux.application.core.jooq.grid.JooqGrid;
import echoflux.application.layout.MainLayout;
import echoflux.domain.core.security.PermissionType;
import echoflux.domain.core.security.RequiredPermissions;
import org.jooq.DSLContext;

import static echoflux.domain.jooq.Tables.PERMISSION;

@PageTitle("Permissions")
@Route(value = "permissions", layout = MainLayout.class)
@RequiredPermissions(PermissionType.ACCESS_MANAGEMENT_PERMISSIONS_VIEW)
public class PermissionsView extends VerticalLayout {

    public PermissionsView(DSLContext ctx) {
        var grid = new JooqGrid<>(ctx, PERMISSION, PERMISSION.ID);
        grid.setDefaultOrderBy(PERMISSION.TYPE);
        grid.addColumn(PERMISSION.TYPE).setDefaultFilter().setAutoWidth(true);
        grid.addColumn(PERMISSION.DESCRIPTION).setDefaultFilter();
        grid.addColumn(PERMISSION.KEY).setDefaultFilter();
        grid.addIdColumn().setDefaultFilter();
        grid.addAuditColumns().forEach(JooqGrid.JooqGridColumn::setDefaultFilter);

        addAndExpand(grid.withControls());
    }

}
