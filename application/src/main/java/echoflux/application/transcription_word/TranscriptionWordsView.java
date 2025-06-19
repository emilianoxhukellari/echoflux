package echoflux.application.transcription_word;

import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import echoflux.application.core.jpa.grid.JpaGrid;
import echoflux.domain.application_user.data.ApplicationUserEntity_;
import echoflux.domain.transcription.data.TranscriptionEntity_;
import echoflux.domain.transcription_word.data.TranscriptionWordEntity_;
import echoflux.domain.transcription_word.data.TranscriptionWordProjection;
import echoflux.domain.transcription_word.data.TranscriptionWordRepository;
import jakarta.annotation.security.RolesAllowed;
import echoflux.application.layout.MainLayout;

@PageTitle("Transcription Words")
@Route(value = "transcription-words", layout = MainLayout.class)
@RolesAllowed("ADMIN")
public class TranscriptionWordsView extends VerticalLayout {

    public TranscriptionWordsView(TranscriptionWordRepository transcriptionWordRepository) {
        var grid = new JpaGrid<>(TranscriptionWordProjection.class, transcriptionWordRepository);
        grid.addColumnWithFilter(TranscriptionWordEntity_.CONTENT);
        grid.addColumnWithFilter(TranscriptionWordEntity_.SPEAKER_NAME);
        grid.addColumnWithFilter(TranscriptionWordEntity_.START_OFFSET_MILLIS);
        grid.addColumnWithFilter(TranscriptionWordEntity_.END_OFFSET_MILLIS);
        grid.addColumnWithFilter(TranscriptionWordEntity_.SEQUENCE);
        grid.addColumnWithFilter(TranscriptionWordEntity_.TRANSCRIPTION, TranscriptionEntity_.ID);
        grid.addColumnWithFilter(TranscriptionWordEntity_.TRANSCRIPTION, TranscriptionEntity_.NAME);
        grid.addColumnWithFilter(TranscriptionWordEntity_.TRANSCRIPTION, TranscriptionEntity_.APPLICATION_USER, ApplicationUserEntity_.ID);
        grid.addColumnWithFilter(TranscriptionWordEntity_.TRANSCRIPTION, TranscriptionEntity_.APPLICATION_USER, ApplicationUserEntity_.USERNAME);
        grid.addIdColumnWithFilter();
        grid.addAuditColumnsWithFilter();

        addAndExpand(grid.withControls());
    }

}
