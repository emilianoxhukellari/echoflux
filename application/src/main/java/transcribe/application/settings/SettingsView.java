package transcribe.application.settings;

import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.RolesAllowed;
import org.vaadin.lineawesome.LineAwesomeIcon;
import transcribe.application.core.jpa.grid.JpaGrid;
import transcribe.application.core.jpa.grid.JpaGridControls;
import transcribe.application.core.operation.Operation;
import transcribe.application.core.operation.OperationCallable;
import transcribe.application.core.operation.OperationRunner;
import transcribe.application.main.MainLayout;
import transcribe.domain.operation.data.OperationType;
import transcribe.domain.settings.data.SettingsEntity;
import transcribe.domain.settings.data.SettingsRepository;
import transcribe.domain.settings.synchronizer.SettingsSynchronizer;

@PageTitle("Settings")
@Route(value = "settings", layout = MainLayout.class)
@RolesAllowed("ADMIN")
public class SettingsView extends Composite<VerticalLayout> {

    private final SettingsRepository repository;
    private final SettingsSynchronizer synchronizer;
    private final OperationRunner operationRunner;

    public SettingsView(SettingsRepository repository,
                        SettingsSynchronizer synchronizer,
                        OperationRunner operationRunner) {
        this.repository = repository;
        this.synchronizer = synchronizer;
        this.operationRunner = operationRunner;

        var grid = newGrid();
        var controls = newGridControls(grid);

        getContent().addAndExpand(controls);
    }

    private JpaGrid<SettingsEntity, SettingsRepository> newGrid() {
        var grid = new JpaGrid<>(SettingsEntity.class, repository);

        grid.addCoreAttributeColumnsExcluding("value");
        grid.addAuditColumns();
        grid.addIdColumn();
        grid.setAllColumnsResizable();

        grid.addCoreAttributeFiltersExcluding("value");
        grid.addAuditFilters();
        grid.addIdFilter();

        grid.addCrudActions();

        grid.addConfirmedContextMenuItem("Reset", item -> {
            var operation = Operation.builder()
                    .name("Resetting settings")
                    .callable(OperationCallable.ofRunnable(() -> synchronizer.reset(item.getKey())))
                    .type(OperationType.NON_BLOCKING)
                    .onFinally(grid::refreshAll)
                    .build();

            operationRunner.run(operation, UI.getCurrent());
        });

        return grid;
    }

    private JpaGridControls<SettingsEntity, SettingsRepository> newGridControls(JpaGrid<SettingsEntity, SettingsRepository> grid) {
        var synchronizeButton = new Button(
                LineAwesomeIcon.SYNC_ALT_SOLID.create(),
                _ -> {
                    var operation = Operation.builder()
                            .name("Synchronizing settings")
                            .callable(OperationCallable.ofRunnable(synchronizer::synchronize))
                            .type(OperationType.NON_BLOCKING)
                            .onFinally(grid::refreshAll)
                            .build();

                    operationRunner.run(operation, UI.getCurrent());
                }
        );
        synchronizeButton.setTooltipText("Synchronize settings");

        return new JpaGridControls<>(grid)
                .addTopRight(synchronizeButton);
    }

}
