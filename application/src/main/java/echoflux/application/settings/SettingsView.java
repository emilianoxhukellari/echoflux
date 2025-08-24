package echoflux.application.settings;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import echoflux.application.core.jooq.grid.JooqGrid;
import echoflux.application.core.jooq.grid.JooqGridControls;
import echoflux.application.core.security.AuthenticatedUser;
import echoflux.domain.core.security.PermissionType;
import echoflux.domain.core.security.RequiredPermissions;
import echoflux.domain.jooq.tables.records.SettingsRecord;
import echoflux.domain.settings.endpoint.SettingsEndpoint;
import org.jooq.DSLContext;
import org.vaadin.lineawesome.LineAwesomeIcon;
import echoflux.application.core.operation.Operation;
import echoflux.application.core.operation.OperationCallable;
import echoflux.application.layout.MainLayout;

import static echoflux.domain.jooq.Tables.SETTINGS;

@PageTitle("Settings")
@Route(value = "settings", layout = MainLayout.class)
@RequiredPermissions(PermissionType.SETTINGS_VIEW)
public class SettingsView extends VerticalLayout {

    private final SettingsEndpoint settingsEndpoint;
    private final DSLContext ctx;

    public SettingsView(DSLContext ctx, SettingsEndpoint settingsEndpoint) {
        this.ctx = ctx;
        this.settingsEndpoint = settingsEndpoint;

        var grid = newGrid();
        var controls = newGridControls(grid);

        addAndExpand(controls);
    }

    private JooqGrid<SettingsRecord, Long> newGrid() {
        var grid = new JooqGrid<>(ctx, SETTINGS, SETTINGS.ID);
        grid.addColumn(SETTINGS.KEY).setDefaultFilter();
        grid.addColumn(SETTINGS.NAME).setDefaultFilter();
        grid.addIdColumn().setDefaultFilter();
        grid.addAuditColumns().forEach(JooqGrid.JooqGridColumn::setDefaultFilter);
        grid.setDefaultOrderBy(SETTINGS.NAME);

        if (AuthenticatedUser.checkPermission(PermissionType.SETTINGS_RESET)) {
            grid.addConfirmContextMenuItem(
                    "Reset",
                    item -> Operation.<Long>builder()
                            .name("Resetting settings")
                            .callable(() -> settingsEndpoint.reset(item.getKey()))
                            .onSuccess(grid::refreshItemById)
                            .build()
                            .runBackground()
            );
        }

        return grid;
    }

    private JooqGridControls<SettingsRecord, Long> newGridControls(JooqGrid<SettingsRecord, Long> grid) {
        var controls = grid.withControls();

        if (AuthenticatedUser.checkPermission(PermissionType.SETTINGS_SYNCHRONIZE)) {
            var synchronizeButton = new Button(
                    LineAwesomeIcon.SYNC_ALT_SOLID.create(),
                    _ -> Operation.builder()
                            .name("Synchronizing settings")
                            .callable(OperationCallable.ofRunnable(settingsEndpoint::synchronizeAll))
                            .onFinally(grid::refreshAll)
                            .build()
                            .runBackground()
            );
            synchronizeButton.setTooltipText("Synchronize settings");
            controls.addTopRight(synchronizeButton);
        }

        return controls;
    }

}
