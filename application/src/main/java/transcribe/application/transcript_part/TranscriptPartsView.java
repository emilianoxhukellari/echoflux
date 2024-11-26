package transcribe.application.transcript_part;

import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.RolesAllowed;
import transcribe.application.core.jpa.grid.JpaGrid;
import transcribe.application.core.jpa.grid.JpaGridControls;
import transcribe.application.main.MainLayout;
import transcribe.domain.transcript.transcript_part.data.TranscriptPartEntity;
import transcribe.domain.transcript.transcript_part.data.TranscriptPartRepository;

@PageTitle("Transcript Parts")
@Route(value = "transcript-parts", layout = MainLayout.class)
@RolesAllowed("ADMIN")
public class TranscriptPartsView extends Composite<VerticalLayout> {

    public TranscriptPartsView(TranscriptPartRepository repository) {
        var grid = new JpaGrid<>(TranscriptPartEntity.class, repository);
        grid.addAllColumns();
        grid.setAllColumnsResizable();
        grid.addAllFilters();
        grid.addCrudActions();

        getContent().addAndExpand(new JpaGridControls<>(grid));
    }

}
