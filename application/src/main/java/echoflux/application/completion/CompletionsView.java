package echoflux.application.completion;

import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import echoflux.application.core.jooq.grid.JooqGrid;
import echoflux.domain.core.security.PermissionType;
import echoflux.domain.core.security.RequiredPermissions;
import echoflux.application.layout.MainLayout;
import org.jooq.DSLContext;

import static echoflux.domain.jooq.Tables.V_COMPLETION;

@PageTitle("Completions")
@Route(value = "completions", layout = MainLayout.class)
@RequiredPermissions(PermissionType.COMPLETIONS_VIEW)
public class CompletionsView extends VerticalLayout {

    public CompletionsView(DSLContext ctx) {
        var grid = new JooqGrid<>(ctx, V_COMPLETION, V_COMPLETION.ID);
        grid.addColumn(V_COMPLETION.INPUT).setDefaultFilter();
        grid.addColumn(V_COMPLETION.OUTPUT).setDefaultFilter();
        grid.addColumn(V_COMPLETION.INPUT_TOKENS).setDefaultFilter();
        grid.addColumn(V_COMPLETION.OUTPUT_TOKENS).setDefaultFilter();
        grid.addColumn(V_COMPLETION.MODEL).setDefaultFilter();
        grid.addColumn(V_COMPLETION.TEMPERATURE).setDefaultFilter();
        grid.addColumn(V_COMPLETION.TOP_P).setDefaultFilter();
        grid.addColumn(V_COMPLETION.STATUS).setDefaultFilter();
        grid.addColumn(V_COMPLETION.DURATION).setDefaultFilter();
        grid.addColumn(V_COMPLETION.ERROR).setDefaultFilter();
        grid.addColumn(V_COMPLETION.TRANSCRIPTION_ID).setDefaultFilter();
        grid.addColumn(V_COMPLETION.TRANSCRIPTION_NAME).setDefaultFilter();
        grid.addColumn(V_COMPLETION.APPLICATION_USER_ID).setDefaultFilter();
        grid.addColumn(V_COMPLETION.APPLICATION_USER_USERNAME).setDefaultFilter();
        grid.addColumn(V_COMPLETION.APPLICATION_USER_NAME).setDefaultFilter();
        grid.addIdColumn().setDefaultFilter();
        grid.addAuditColumns().forEach(JooqGrid.JooqGridColumn::setDefaultFilter);

        addAndExpand(grid.withControls());
    }

}
