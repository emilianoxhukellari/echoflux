package echoflux.application.settings;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import echoflux.application.core.jpa.grid.JpaGrid;
import echoflux.domain.settings.data.SettingsEntity_;
import echoflux.domain.settings.data.SettingsProjection;
import echoflux.domain.settings.data.SettingsRepository;
import jakarta.annotation.security.RolesAllowed;
import org.springframework.data.domain.Sort;
import org.vaadin.lineawesome.LineAwesomeIcon;
import echoflux.application.core.jpa.grid.JpaGridControls;
import echoflux.application.core.operation.Operation;
import echoflux.application.core.operation.OperationCallable;
import echoflux.application.layout.MainLayout;
import echoflux.domain.settings.data.SettingsEntity;
import echoflux.domain.settings.synchronizer.SettingsSynchronizer;

import java.util.Set;

@PageTitle("Settings")
@Route(value = "settings", layout = MainLayout.class)
@RolesAllowed("ADMIN")
public class SettingsView extends VerticalLayout {

    private final SettingsSynchronizer synchronizer;

    public SettingsView(SettingsSynchronizer synchronizer, SettingsRepository settingsRepository) {
        this.synchronizer = synchronizer;

        var grid = newGrid(settingsRepository);
        var controls = newGridControls(grid);

        addAndExpand(controls);
    }

    private JpaGrid<SettingsProjection, SettingsEntity, Long> newGrid(SettingsRepository settingsRepository) {
        var attributePaths = Set.of(
                SettingsEntity_.KEY,
                SettingsEntity_.NAME,
                SettingsEntity_.ID,
                SettingsEntity_.CREATED_AT,
                SettingsEntity_.CREATED_BY,
                SettingsEntity_.UPDATED_AT,
                SettingsEntity_.UPDATED_BY
        );
        var grid = new JpaGrid<>(SettingsProjection.class, settingsRepository, attributePaths);
        grid.addColumnWithFilter(SettingsEntity_.KEY);
        grid.addColumnWithFilter(SettingsEntity_.NAME);
        grid.addIdColumnWithFilter();
        grid.addAuditColumnsWithFilter();

        grid.setDefaultSortAttribute(SettingsEntity_.NAME);
        grid.setDefaultSortDirection(Sort.Direction.ASC);
        grid.addConfirmContextMenuItem(
                "Reset",
                item -> Operation.<SettingsProjection>builder()
                .name("Resetting settings")
                .callable(() -> synchronizer.reset(item.getKey()))
                .onSuccess(grid::refreshItem)
                .build()
                .runBackground()
        );

        return grid;
    }

    private JpaGridControls<SettingsProjection, SettingsEntity, Long> newGridControls(JpaGrid<SettingsProjection, SettingsEntity, Long> grid) {
        var synchronizeButton = new Button(
                LineAwesomeIcon.SYNC_ALT_SOLID.create(),
                _ -> Operation.builder()
                        .name("Synchronizing settings")
                        .callable(OperationCallable.ofRunnable(synchronizer::synchronize))
                        .onFinally(grid::refreshAll)
                        .build()
                        .runBackground()
        );
        synchronizeButton.setTooltipText("Synchronize settings");

        return grid
                .withControls()
                .addTopRight(synchronizeButton);
    }

}
