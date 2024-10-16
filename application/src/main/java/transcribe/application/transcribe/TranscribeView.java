package transcribe.application.transcribe;

import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.DetachEvent;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.data.renderer.TextRenderer;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.PermitAll;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.DurationFormatUtils;
import org.vaadin.lineawesome.LineAwesomeIcon;
import transcribe.application.core.icon.IconFactory;
import transcribe.application.core.jpa.grid.JpaGrid;
import transcribe.application.core.jpa.grid.JpaGridControls;
import transcribe.application.core.progress.BallClipRotatePulseProgress;
import transcribe.application.core.ui.UiUtils;
import transcribe.application.main.MainLayout;
import transcribe.application.security.AuthenticatedUser;
import transcribe.domain.core.broadcaster.Broadcaster;
import transcribe.domain.core.broadcaster.Subscription;
import transcribe.domain.transcription.data.TranscriptionEntity;
import transcribe.domain.transcription.data.TranscriptionRepository;
import transcribe.domain.transcription.event.TranscriptionCreateUserEvent;
import transcribe.domain.transcription.event.TranscriptionUpdateUserEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@PageTitle("Transcribe")
@Route(value = "transcribe", layout = MainLayout.class)
@PermitAll
@Slf4j
public class TranscribeView extends Composite<VerticalLayout> {

    private final Broadcaster broadcaster;
    private final List<Subscription> subscriptions = new ArrayList<>();
    private final JpaGrid<TranscriptionEntity, TranscriptionRepository> grid;
    private final Long applicationUserId;

    public TranscribeView(Broadcaster broadcaster, AuthenticatedUser authenticatedUser, TranscriptionRepository repository) {
        this.broadcaster = broadcaster;
        this.applicationUserId = authenticatedUser.get().getId();

        var button = new Button("Transcribe", LineAwesomeIcon.PODCAST_SOLID.create());
        button.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        button.addClickListener(_ -> new TranscribeDialog().open());
        this.grid = new JpaGrid<>(
                TranscriptionEntity.class,
                repository,
                (root, _, criteriaBuilder) -> criteriaBuilder.equal(root.get("applicationUserId"), applicationUserId)
        );
        grid.removeThemeVariants(GridVariant.LUMO_COLUMN_BORDERS);
        grid.addThemeVariants(GridVariant.LUMO_NO_BORDER);
        grid.addColumns("name", "language", "status", "createdAt");
        grid.addColumn("lengthMillis")
                .setRenderer(new TextRenderer<>(TranscribeView::newDuration))
                .setHeader("Duration");
        grid.addColumn(new ComponentRenderer<>(TranscribeView::newStatus))
                .setHeader("Status");

        grid.setAllColumnsResizable();
        grid.addFilters("name", "language", "status", "createdAt");

        var controls = new JpaGridControls<>(grid);
        controls.addTopRight(button);

        var content = getContent();
        content.setAlignItems(FlexComponent.Alignment.CENTER);
        content.setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER);
        content.addAndExpand(controls);
    }

    private static String newDuration(TranscriptionEntity entity) {
        long duration = Objects.requireNonNullElse(entity.getLengthMillis(), 0L);

        return DurationFormatUtils.formatDuration(duration, "H'h' m'm' s's'");
    }

    private static Component newStatus(TranscriptionEntity entity) {
        var status = entity.getStatus();
        var layout = new HorizontalLayout();
        layout.setAlignItems(FlexComponent.Alignment.CENTER);
        layout.setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER);

        switch (status) {
            case FAILED -> layout.add(IconFactory.newIcon(VaadinIcon.CLOSE::create, "red", "1.5rem",  "Error"));
            case COMPLETED -> layout.add(IconFactory.newIcon(VaadinIcon.CHECK::create, "green", "1.5rem",  "Success"));
            case CREATED -> {}
            case DOWNLOADING_PUBLIC, PROCESSING -> {
                var progress = new BallClipRotatePulseProgress();
                layout.add(progress);
            }
            case TRANSCRIBING -> {
                var percent = Objects.toString(entity.getTranscribeProgress(), "0") + "%";
                layout.add(new Text(percent));
            }
        }

        return layout;
    }

    @Override
    protected void onAttach(AttachEvent attachEvent) {
        super.onAttach(attachEvent);
        var ui = attachEvent.getUI();

        var sub1 = broadcaster.subscribe(
                TranscriptionCreateUserEvent.class,
                _ -> UiUtils.safeAccess(ui, grid::refreshAll),
                e -> Objects.equals(e.getApplicationUserId(), applicationUserId)
        );

        var sub2 = broadcaster.subscribe(
                TranscriptionUpdateUserEvent.class,
                e -> UiUtils.safeAccess(ui, () -> grid.refreshItem(e.getEntity())),
                e -> Objects.equals(e.getEntity().getApplicationUserId(), applicationUserId)
        );

        subscriptions.addAll(List.of(sub1, sub2));
    }

    @Override
    protected void onDetach(DetachEvent detachEvent) {
        super.onDetach(detachEvent);

        subscriptions.forEach(Subscription::remove);
    }

}
