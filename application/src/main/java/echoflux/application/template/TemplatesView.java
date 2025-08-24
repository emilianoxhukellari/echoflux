package echoflux.application.template;

import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import echoflux.application.core.jooq.grid.JooqGrid;
import echoflux.application.core.operation.OperationRunner;
import echoflux.application.core.security.AuthenticatedUser;
import echoflux.core.core.bean.accessor.BeanAccessor;
import echoflux.domain.core.security.PermissionType;
import echoflux.domain.core.security.RequiredPermissions;
import echoflux.domain.jooq.tables.pojos.Template;
import echoflux.domain.template.endpoint.TemplateEndpoint;
import echoflux.application.layout.MainLayout;
import org.jooq.DSLContext;

import static echoflux.domain.jooq.Tables.TEMPLATE;

@PageTitle("Templates")
@Route(value = "templates", layout = MainLayout.class)
@RequiredPermissions(PermissionType.TEMPLATES_VIEW)
public class TemplatesView extends VerticalLayout {

    public TemplatesView(DSLContext ctx, TemplateEndpoint templateEndpoint, BeanAccessor beanAccessor) {
        var grid = new JooqGrid<>(ctx, TEMPLATE, TEMPLATE.ID);
        grid.addColumn(TEMPLATE.NAME)
                .setDefaultFilter();
        grid.addColumn(TEMPLATE.CONTENT)
                .setDefaultFilter()
                .setWidth("400px");
        grid.addIdColumn()
                .setDefaultFilter();
        grid.addAuditColumns()
                .forEach(JooqGrid.JooqGridColumn::setDefaultFilter);

        if (AuthenticatedUser.checkPermission(PermissionType.TEMPLATE_UPDATE)) {
            grid.addContextMenuItemWithDoubleClickListener(
                    "Edit",
                    item -> SaveTemplateDialog.newUpdate(item.into(Template.class), beanAccessor)
                            .withSaveListener(grid::refreshItemById)
                            .open()
            );
        }
        if (AuthenticatedUser.checkPermission(PermissionType.TEMPLATE_DELETE)) {
            grid.addConfirmContextMenuItem(
                    "Delete",
                    item -> OperationRunner.run(
                            "Deleting template with ID [%s]".formatted(item.getId()),
                            () -> templateEndpoint.deleteById(item.getId()),
                            grid::refreshAll
                    )
            );
        }

        var controls = grid.withControls();
        if (AuthenticatedUser.checkPermission(PermissionType.TEMPLATE_CREATE)) {
            controls.addCreateButton(
                    () -> SaveTemplateDialog.newCreate(beanAccessor)
                            .withSaveListener(_ -> grid.refreshAll())
                            .open()
            );
        }

        addAndExpand(controls);
    }

}
