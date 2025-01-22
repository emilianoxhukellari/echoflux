package transcribe.application.transcription;

import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.RolesAllowed;
import transcribe.application.core.jpa.grid.JpaGrid;
import transcribe.application.core.jpa.grid.JpaGridControls;
import transcribe.application.main.MainLayout;

@PageTitle("Transcriptions")
@Route(value = "transcriptions", layout = MainLayout.class)
@RolesAllowed("ADMIN")
public class TranscriptionsView extends Composite<VerticalLayout> {

    public TranscriptionsView() {
        var grid = new JpaGrid<>(TranscriptionJpaDto.class);
        grid.addAllColumns();
        grid.setAllColumnsResizable();
        grid.addAllFilters();
        grid.addCrudActions();

        getContent().addAndExpand(new JpaGridControls<>(grid));
    }

}
