package transcribe.application.transcription_speaker;

import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.RolesAllowed;
import transcribe.application.core.jpa.grid.JpaGrid;
import transcribe.application.core.jpa.grid.JpaGridControls;
import transcribe.application.main.MainLayout;

@PageTitle("Transcription Speakers")
@Route(value = "transcription-speakers", layout = MainLayout.class)
@RolesAllowed("ADMIN")
public class TranscriptionSpeakersView extends Composite<VerticalLayout> {

    public TranscriptionSpeakersView() {
        var grid = new JpaGrid<>(TranscriptionSpeakerJpaDto.class);
        grid.addAllColumns();
        grid.setAllColumnsResizable();
        grid.addAllFilters();
        grid.addCrudActions();

        getContent().addAndExpand(new JpaGridControls<>(grid));
    }

}
