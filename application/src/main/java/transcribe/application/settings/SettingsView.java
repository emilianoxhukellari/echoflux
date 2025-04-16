package transcribe.application.settings;

import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.RolesAllowed;
import org.springframework.data.domain.Sort;
import org.vaadin.lineawesome.LineAwesomeIcon;
import transcribe.application.core.jpa.dto.impl.SimpleJpaDtoService;
import transcribe.application.core.jpa.grid.JpaGrid;
import transcribe.application.core.jpa.grid.JpaGridConfiguration;
import transcribe.application.core.jpa.grid.JpaGridControls;
import transcribe.application.core.operation.Operation;
import transcribe.application.core.operation.OperationCallable;
import transcribe.application.core.operation.OperationRunner;
import transcribe.application.layout.MainLayout;
import transcribe.core.core.bean.loader.BeanLoader;
import transcribe.domain.operation.data.OperationType;
import transcribe.domain.settings.data.SettingsEntity;
import transcribe.domain.settings.data.SettingsProjection;
import transcribe.domain.settings.synchronizer.SettingsSynchronizer;

@PageTitle("Settings")
@Route(value = "settings", layout = MainLayout.class)
@RolesAllowed("ADMIN")
public class SettingsView extends Composite<VerticalLayout> {

    private final SettingsSynchronizer synchronizer;
    private final OperationRunner operationRunner;
    private final BeanLoader beanLoader;
    private final SimpleJpaDtoService<SettingsJpaDto, SettingsEntity, Long> jpaDtoService;

    public SettingsView(SettingsSynchronizer synchronizer,
                        OperationRunner operationRunner,
                        BeanLoader beanLoader) {
        this.synchronizer = synchronizer;
        this.operationRunner = operationRunner;
        this.beanLoader = beanLoader;
        this.jpaDtoService = new SimpleJpaDtoService<>(SettingsJpaDto.class, beanLoader);

        var grid = newGrid();
        var controls = newGridControls(grid);

        getContent().addAndExpand(controls);
    }

    private JpaGrid<SettingsJpaDto, SettingsEntity, Long> newGrid() {
        var grid = new JpaGrid<>(
                JpaGridConfiguration.<SettingsJpaDto, SettingsEntity, Long>builder()
                        .beanType(SettingsJpaDto.class)
                        .service(jpaDtoService)
                        .beanLoader(beanLoader)
                        .defaultSortAttribute(SettingsJpaDto_.NAME)
                        .defaultSortDirection(Sort.Direction.ASC)
                        .build()
        );

        grid.addCoreAttributeColumnsExcluding(SettingsJpaDto_.VALUE);
        grid.addAuditColumns();
        grid.addIdColumn();
        grid.setAllColumnsResizable();

        grid.addCoreAttributeFiltersExcluding(SettingsJpaDto_.VALUE);
        grid.addAuditFilters();
        grid.addIdFilter();

        grid.addCrudActions();

        grid.addConfirmedContextMenuItem("Reset", item -> {
            var operation = Operation.<SettingsProjection>builder()
                    .name("Resetting settings")
                    .callable(() -> synchronizer.reset(item.getKey()))
                    .type(OperationType.NON_BLOCKING)
                    .onSuccess(settings -> {
                        var dto = jpaDtoService.getById(settings.id());
                        grid.refreshItem(dto);
                    })
                    .build();

            operationRunner.run(operation, UI.getCurrent());
        });

        return grid;
    }

    private JpaGridControls<SettingsJpaDto, SettingsEntity, Long> newGridControls(JpaGrid<SettingsJpaDto, SettingsEntity, Long> grid) {
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
