package echoflux.application.transcription;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import echoflux.application.core.jooq.grid.JooqGrid;
import echoflux.domain.core.security.PermissionType;
import echoflux.domain.core.security.RequiredPermissions;
import echoflux.application.layout.MainLayout;
import org.jooq.DSLContext;

import static echoflux.domain.jooq.Tables.V_TRANSCRIPTION;

@PageTitle("Transcriptions")
@Route(value = "transcriptions", layout = MainLayout.class)
@RequiredPermissions(PermissionType.TRANSCRIPTIONS_VIEW)
public class TranscriptionsView extends VerticalLayout {

    public TranscriptionsView(DSLContext ctx) {
        var grid = new JooqGrid<>(ctx, V_TRANSCRIPTION, V_TRANSCRIPTION.ID);
        grid.addColumn(V_TRANSCRIPTION.STATUS).setDefaultFilter();
        grid.addColumn(V_TRANSCRIPTION.SOURCE_URI).setDefaultFilter();
        grid.addColumn(V_TRANSCRIPTION.URI).setDefaultFilter();
        grid.addColumn(V_TRANSCRIPTION.STORAGE_PROVIDER).setDefaultFilter();
        grid.addColumn(V_TRANSCRIPTION.LANGUAGE).setDefaultFilter();
        grid.addColumn(V_TRANSCRIPTION.NAME).setDefaultFilter();
        grid.addColumn(V_TRANSCRIPTION.LENGTH).setDefaultFilter();
        grid.addColumn(V_TRANSCRIPTION.ERROR).setDefaultFilter();
        grid.addColumn(V_TRANSCRIPTION.APPLICATION_USER_ID).setDefaultFilter();
        grid.addColumn(V_TRANSCRIPTION.APPLICATION_USER_USERNAME).setDefaultFilter();
        grid.addColumn(V_TRANSCRIPTION.APPLICATION_USER_NAME).setDefaultFilter();
        grid.addIdColumn().setDefaultFilter();
        grid.addAuditColumns().forEach(JooqGrid.JooqGridColumn::setDefaultFilter);
        grid.addContextMenuItem("Open", t -> UI.getCurrent().navigate(TranscriptionView.class, t.getId()));

        addAndExpand(grid.withControls());
    }

}
