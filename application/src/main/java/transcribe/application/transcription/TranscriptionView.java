package transcribe.application.transcription;

import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.BeforeEvent;
import com.vaadin.flow.router.HasDynamicTitle;
import com.vaadin.flow.router.HasUrlParameter;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.PermitAll;
import transcribe.application.core.component.AudioTextConnector;
import transcribe.application.layout.MainLayout;
import transcribe.domain.transcription.data.TranscriptionProjection;
import transcribe.domain.transcription.service.TranscriptionService;


@Route(value = "transcription", layout = MainLayout.class)
@PermitAll
public class TranscriptionView extends Composite<VerticalLayout> implements HasUrlParameter<Long>, HasDynamicTitle {

    private final TranscriptionService transcriptionService;
    private TranscriptionProjection transcription;

    public TranscriptionView(TranscriptionService transcriptionService) {
        this.transcriptionService = transcriptionService;
    }

    @Override
    public void setParameter(BeforeEvent event, Long parameter) {
        this.transcription = transcriptionService.projectById(parameter);

        var audioTextConnector = new AudioTextConnector(transcription.id());
        getContent().addAndExpand(audioTextConnector);
    }

    @Override
    public String getPageTitle() {
        return transcription.name();
    }

}
