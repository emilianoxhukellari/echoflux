package transcribe.application.transcribe;

import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.DetachEvent;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.virtuallist.VirtualList;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.PermitAll;
import lombok.extern.slf4j.Slf4j;
import org.vaadin.lineawesome.LineAwesomeIcon;
import transcribe.application.core.spring.SpringContext;
import transcribe.application.main.MainLayout;
import transcribe.core.core.executor.CommonExecutor;
import transcribe.domain.core.broadcaster.Broadcaster;
import transcribe.domain.core.broadcaster.Subscription;
import transcribe.domain.transcription.data.TranscriptionMetadataEntity;
import transcribe.domain.transcription.data.TranscriptionMetadataRepository;
import transcribe.domain.transcription.event.TranscriptionCreateUserEvent;
import transcribe.domain.transcription.event.TranscriptionUpdateUserEvent;

import java.util.ArrayList;
import java.util.List;


@PageTitle("Transcribe")
@Route(value = "transcribe", layout = MainLayout.class)
@PermitAll
@Slf4j
public class TranscribeView extends Composite<VerticalLayout> {

    private final CommonExecutor executor = SpringContext.getBean(CommonExecutor.class);
    private final Broadcaster broadcaster = SpringContext.getBean(Broadcaster.class);
    private final List<Subscription> subscriptions = new ArrayList<>();
    private final TranscriptionMetadataRepository repository = SpringContext.getBean(TranscriptionMetadataRepository.class);

    public TranscribeView() {
        var button = new Button("Transcribe", LineAwesomeIcon.PODCAST_SOLID.create());
        button.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_LARGE);
        button.addClickListener(_ -> new TranscribeDialog().open());

        var virtualList = new VirtualList<TranscriptionMetadataEntity>();

        getContent().add(button, virtualList);
    }

    @Override
    protected void onAttach(AttachEvent attachEvent) {
        super.onAttach(attachEvent);

        var sub1 = broadcaster.subscribe(
                TranscriptionCreateUserEvent.class,
                e -> log.info("Created: {}", e)
        );

        var sub2 = broadcaster.subscribe(
                TranscriptionUpdateUserEvent.class,
                e -> log.info("Updated: {}", e)
        );

        subscriptions.addAll(List.of(sub1, sub2));
    }

    protected void onDetach(DetachEvent detachEvent) {
        super.onDetach(detachEvent);

        subscriptions.forEach(Subscription::remove);
    }

}
