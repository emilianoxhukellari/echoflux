package echoflux.application.transcription_word;

import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import echoflux.application.core.jooq.grid.JooqGrid;
import echoflux.domain.core.security.PermissionType;
import echoflux.domain.core.security.RequiredPermissions;
import echoflux.application.layout.MainLayout;
import org.jooq.DSLContext;

import static echoflux.domain.jooq.Tables.V_TRANSCRIPTION_WORD;

@PageTitle("Transcription Words")
@Route(value = "transcription-words", layout = MainLayout.class)
@RequiredPermissions(PermissionType.TRANSCRIPTION_WORDS_VIEW)
public class TranscriptionWordsView extends VerticalLayout {

    public TranscriptionWordsView(DSLContext ctx) {
        var grid = new JooqGrid<>(ctx, V_TRANSCRIPTION_WORD, V_TRANSCRIPTION_WORD.ID);
        grid.addColumn(V_TRANSCRIPTION_WORD.CONTENT).setDefaultFilter();
        grid.addColumn(V_TRANSCRIPTION_WORD.SPEAKER_NAME).setDefaultFilter();
        grid.addColumn(V_TRANSCRIPTION_WORD.START_OFFSET_MILLIS).setDefaultFilter();
        grid.addColumn(V_TRANSCRIPTION_WORD.END_OFFSET_MILLIS).setDefaultFilter();
        grid.addColumn(V_TRANSCRIPTION_WORD.SEQUENCE).setDefaultFilter();
        grid.addColumn(V_TRANSCRIPTION_WORD.TRANSCRIPTION_ID).setDefaultFilter();
        grid.addColumn(V_TRANSCRIPTION_WORD.TRANSCRIPTION_NAME).setDefaultFilter();
        grid.addColumn(V_TRANSCRIPTION_WORD.APPLICATION_USER_ID).setDefaultFilter();
        grid.addColumn(V_TRANSCRIPTION_WORD.APPLICATION_USER_USERNAME).setDefaultFilter();
        grid.addColumn(V_TRANSCRIPTION_WORD.APPLICATION_USER_NAME).setDefaultFilter();
        grid.addIdColumn().setDefaultFilter();
        grid.addAuditColumns().forEach(JooqGrid.JooqGridColumn::setDefaultFilter);

        addAndExpand(grid.withControls());
    }

}
