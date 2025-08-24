package echoflux.application.core.jooq.grid;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import echoflux.application.core.icon.CustomizedIcon;
import lombok.Getter;
import org.jooq.Record;
import org.vaadin.lineawesome.LineAwesomeIcon;

import java.util.Objects;

public class JooqGridControls<R extends Record, ID> extends VerticalLayout {

    @Getter
    private final JooqGrid<R, ID> grid;

    private final HorizontalLayout topLeft;
    private final HorizontalLayout topRight;

    public JooqGridControls(JooqGrid<R, ID> grid) {
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
    }

    public JooqGridControls<R, ID> addCreateButton(Runnable onClick) {
        Objects.requireNonNull(onClick, "On click action must not be null");

        var button = new Button(LineAwesomeIcon.PLUS_SOLID.create(), _ -> onClick.run());
        button.setTooltipText("Create");

        addTopRight(button);

        return this;
    }

    public JooqGridControls<R, ID> addTopLeft(Component component) {
        topLeft.add(component);

        return this;
    }

    public JooqGridControls<R, ID> addTopRight(Component component) {
        topRight.addComponentAsFirst(component);

        return this;
    }

}
