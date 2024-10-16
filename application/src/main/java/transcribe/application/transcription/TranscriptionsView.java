package transcribe.application.transcription;

import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.RolesAllowed;
import transcribe.application.core.jpa.grid.JpaGrid;
import transcribe.application.core.jpa.grid.JpaGridControls;
import transcribe.application.main.MainLayout;
import transcribe.domain.transcription.data.TranscriptionEntity;
import transcribe.domain.transcription.data.TranscriptionRepository;

@PageTitle("Transcriptions")
@Route(value = "transcriptions", layout = MainLayout.class)
@RolesAllowed("ADMIN")
public class TranscriptionsView extends Composite<VerticalLayout> {

    public TranscriptionsView(TranscriptionRepository repository) {
        var grid = new JpaGrid<>(TranscriptionEntity.class, repository);
        grid.addAllColumns();
        grid.setAllColumnsResizable();
        grid.addAllFilters();
        grid.addCrudActions();

        getContent().addAndExpand(new JpaGridControls<>(grid));
    }

}
