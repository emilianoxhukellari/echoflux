package transcribe.application.transcript_part_text;

import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.RolesAllowed;
import transcribe.application.core.jpa.grid.JpaGrid;
import transcribe.application.core.jpa.grid.JpaGridControls;
import transcribe.application.main.MainLayout;
import transcribe.domain.transcript.transcript_part_text.data.TranscriptPartTextEntity;
import transcribe.domain.transcript.transcript_part_text.data.TranscriptPartTextRepository;

@PageTitle("Transcript Part Texts")
@Route(value = "transcript-part-texts", layout = MainLayout.class)
@RolesAllowed("ADMIN")
public class TranscriptPartTextsView extends Composite<VerticalLayout> {

    public TranscriptPartTextsView(TranscriptPartTextRepository repository) {
        var grid = new JpaGrid<>(TranscriptPartTextEntity.class, repository);
        grid.addAllColumns();
        grid.setAllColumnsResizable();
        grid.addAllFilters();
        grid.addCrudActions();

        getContent().addAndExpand(new JpaGridControls<>(grid));
    }

}
