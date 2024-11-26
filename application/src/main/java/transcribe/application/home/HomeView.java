package transcribe.application.home;

import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.PermitAll;
import lombok.extern.slf4j.Slf4j;
import transcribe.application.main.MainLayout;
import transcribe.domain.transcript.transcript_part.service.TranscriptPartService;
import transcribe.domain.transcript.transcript_part_text.service.TranscriptPartTextService;

@PageTitle("Home")
@Route(value = "", layout = MainLayout.class)
@PermitAll
@Slf4j
public class HomeView extends Composite<VerticalLayout> {

    public HomeView(TranscriptPartService transcriptPartService, TranscriptPartTextService transcriptPartTextService) {
        getContent().add(new VerticalLayout());
    }

}
