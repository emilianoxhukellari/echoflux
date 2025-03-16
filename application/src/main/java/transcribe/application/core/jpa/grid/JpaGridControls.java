package transcribe.application.core.jpa.grid;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.data.provider.Query;
import org.vaadin.lineawesome.LineAwesomeIcon;
import transcribe.application.core.dialog.Dialogs;
import transcribe.application.core.icon.CustomizedIcon;
import transcribe.application.core.jpa.dialog.save.JpaSaveCorePropertiesDialog;
import transcribe.core.core.bean.MoreBeans;

import java.util.Objects;

public class JpaGridControls<DTO, ENTITY, ID> extends VerticalLayout {

    private final JpaGrid<DTO, ENTITY, ID> grid;

    private final HorizontalLayout topLeft;
    private final HorizontalLayout topRight;

    public JpaGridControls(JpaGrid<DTO, ENTITY, ID> grid) {
        this.grid = grid;
        grid.setSizeFull();

        this.topLeft = new HorizontalLayout();
        topLeft.setAlignItems(Alignment.CENTER);
        topLeft.setJustifyContentMode(JustifyContentMode.START);

        this.topRight = new HorizontalLayout();
        topRight.setAlignItems(Alignment.CENTER);
        topRight.setJustifyContentMode(JustifyContentMode.END);

        var header = new HorizontalLayout(topLeft, topRight);
        header.setWidthFull();
        header.setAlignItems(Alignment.CENTER);
        header.setJustifyContentMode(JustifyContentMode.BETWEEN);

        add(header, grid);
        setSizeFull();
        expand(grid);
        setPadding(false);
        setSpacing(false);
        getThemeList().add("spacing-s");

        initializeComponents();
    }

    private void initializeComponents() {
        var refreshButton = new Button(LineAwesomeIcon.SYNC_SOLID.create(), _ -> grid.refreshAll());
        refreshButton.setTooltipText("Refresh all");
        addTopLeft(refreshButton);

        var clearFiltersButton = new Button(CustomizedIcon.FILTER_SLASH.create(), _ -> grid.clearFilters());
        clearFiltersButton.setTooltipText("Clear filters");
        addTopLeft(clearFiltersButton);

        var sizeButton = new Button(
                VaadinIcon.HASH.create(),
                _ -> Dialogs.info("Total items: " + grid.getDataProvider().size(new Query<>()))
        );
        sizeButton.setTooltipText("Size");
        addTopLeft(sizeButton);

        if (grid.getCrudActionsData() != null && grid.getCrudActionsData().isWithCrudActions()) {
            addCreateEntityButton(
                    () -> new JpaSaveCorePropertiesDialog<>(
                            MoreBeans.invokeBuilderOrNoArgsConstructorNested(grid.getBeanType()),
                            grid.getBeanType(),
                            grid.getService(),
                            grid.getCrudActionsData().getExcludedPropertiesList()
                    )
                            .setSaveListener(_ -> grid.refreshAll())
                            .open()
            );
        }
    }

    public JpaGridControls<DTO, ENTITY, ID> addCreateEntityButton(Runnable onClick) {
        Objects.requireNonNull(onClick, "On click action must not be null");

        var button = new Button(LineAwesomeIcon.PLUS_SOLID.create(), _ -> onClick.run());
        button.setTooltipText("Create");

        addTopRight(button);

        return this;
    }

    public JpaGridControls<DTO, ENTITY, ID> addTopLeft(Component component) {
        topLeft.add(component);

        return this;
    }

    public JpaGridControls<DTO, ENTITY, ID> addTopRight(Component component) {
        topRight.addComponentAsFirst(component);

        return this;
    }

}
