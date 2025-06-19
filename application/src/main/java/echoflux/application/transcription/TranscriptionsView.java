package echoflux.application.transcription;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import echoflux.application.core.jpa.grid.JpaGrid;
import echoflux.domain.application_user.data.ApplicationUserEntity_;
import echoflux.domain.transcription.data.TranscriptionEntity_;
import echoflux.domain.transcription.data.TranscriptionProjection;
import echoflux.domain.transcription.data.TranscriptionRepository;
import jakarta.annotation.security.RolesAllowed;
import echoflux.application.layout.MainLayout;

@PageTitle("Transcriptions")
@Route(value = "transcriptions", layout = MainLayout.class)
@RolesAllowed("ADMIN")
public class TranscriptionsView extends VerticalLayout {

    public TranscriptionsView(TranscriptionRepository transcriptionRepository) {
        var grid = new JpaGrid<>(TranscriptionProjection.class, transcriptionRepository);
        grid.addColumnWithFilter(TranscriptionEntity_.STATUS);
        grid.addColumnWithFilter(TranscriptionEntity_.SOURCE_URI);
        grid.addColumnWithFilter(TranscriptionEntity_.URI);
        grid.addColumnWithFilter(TranscriptionEntity_.STORAGE_PROVIDER);
        grid.addColumnWithFilter(TranscriptionEntity_.LANGUAGE);
        grid.addColumnWithFilter(TranscriptionEntity_.NAME);
        grid.addColumnWithFilter(TranscriptionEntity_.LENGTH);
        grid.addColumnWithFilter(TranscriptionEntity_.ERROR);
        grid.addColumnWithFilter(TranscriptionEntity_.APPLICATION_USER, ApplicationUserEntity_.ID);
        grid.addColumnWithFilter(TranscriptionEntity_.APPLICATION_USER, ApplicationUserEntity_.USERNAME);
        grid.addColumnWithFilter(TranscriptionEntity_.APPLICATION_USER, ApplicationUserEntity_.NAME);
        grid.addIdColumnWithFilter();
        grid.addAuditColumnsWithFilter();
        grid.addContextMenuItem("Open", t -> UI.getCurrent().navigate(TranscriptionView.class, t.getId()));

        addAndExpand(grid.withControls());
    }

}
