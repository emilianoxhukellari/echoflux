package transcribe.application.transcription;

import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.RolesAllowed;
import transcribe.application.core.jpa.grid.JpaGrid;
import transcribe.application.core.jpa.grid.JpaGridControls;
import transcribe.application.layout.MainLayout;

@PageTitle("Transcriptions")
@Route(value = "transcriptions", layout = MainLayout.class)
@RolesAllowed("ADMIN")
public class TranscriptionsView extends Composite<VerticalLayout> {

    public TranscriptionsView() {
        var grid = new JpaGrid<>(TranscriptionJpaDto.class);
        grid.addAllColumns();
        grid.setAllColumnsResizable();
        grid.setAllColumnsAutoWidth(true);
        grid.addAllFilters();
        grid.addCrudActions();
        grid.addContextMenuItem("Open", t -> UI.getCurrent().navigate(TranscriptionView.class, t.getId()));

        getContent().addAndExpand(new JpaGridControls<>(grid));
    }

}
