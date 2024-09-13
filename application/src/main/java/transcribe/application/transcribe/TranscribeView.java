package transcribe.application.transcribe;

import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.DetachEvent;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.NativeLabel;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.progressbar.ProgressBar;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.PermitAll;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.ListUtils;
import org.vaadin.lineawesome.LineAwesomeIcon;
import transcribe.application.core.broadcaster.Broadcaster;
import transcribe.application.core.broadcaster.Subscription;
import transcribe.application.core.spring.SpringContext;
import transcribe.application.core.ui.UiUtils;
import transcribe.application.main.MainLayout;
import transcribe.application.security.AuthenticatedUser;

import java.util.List;
import java.util.Objects;


@PageTitle("Transcribe")
@Route(value = "transcribe", layout = MainLayout.class)
@PermitAll
@Slf4j
public class TranscribeView extends Composite<VerticalLayout> {

    private final Broadcaster broadcaster = SpringContext.getBean(Broadcaster.class);
    private final AuthenticatedUser authenticatedUser = SpringContext.getBean(AuthenticatedUser.class);
    private final Long authenticatedUserId = authenticatedUser.find().orElseThrow().getId();
    private final NativeLabel status = new NativeLabel();
    private final ProgressBar downloadProgress = new ProgressBar();
    private List<Subscription> subscriptions;

    public TranscribeView() {
        downloadProgress.setMin(0);
        downloadProgress.setMax(100);
        var button = new Button("Transcribe", LineAwesomeIcon.PODCAST_SOLID.create());
        button.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_LARGE);
        button.addClickListener(_ -> new TranscribeDialog().open());

        getContent().add(button, status, downloadProgress);
    }

    @Override
    protected void onAttach(AttachEvent attachEvent) {
        var ui = attachEvent.getUI();
        var sub1 = broadcaster.subscribe(
                TranscribeDialog.DetailedStatusEvent.class,
                e -> UiUtils.safeAccess(ui, () -> status.setText(e.status().name())),
                e -> Objects.equals(e.userId(), authenticatedUserId)
        );
        var sub2 = broadcaster.subscribe(
                TranscribeDialog.DownloadProgressEvent.class,
                e -> UiUtils.safeAccess(ui, () -> downloadProgress.setValue(e.progress())),
                e -> Objects.equals(e.userId(), authenticatedUserId)
        );

        this.subscriptions = List.of(sub1, sub2);
    }

    @Override
    protected void onDetach(DetachEvent detachEvent) {
        ListUtils.emptyIfNull(subscriptions).forEach(Subscription::remove);
    }

}
