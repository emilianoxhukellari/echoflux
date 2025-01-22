package transcribe.application.transcribe;

import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.DetachEvent;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.contextmenu.ContextMenu;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.data.renderer.TextRenderer;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.PermitAll;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.DurationFormatUtils;
import org.vaadin.lineawesome.LineAwesomeIcon;
import transcribe.application.core.component.HelperDownloadAnchor;
import transcribe.application.core.icon.IconFactory;
import transcribe.application.core.jpa.dto.impl.SimpleJpaDtoService;
import transcribe.application.core.jpa.grid.JpaGrid;
import transcribe.application.core.jpa.grid.JpaGridConfiguration;
import transcribe.application.core.jpa.grid.JpaGridControls;
import transcribe.application.core.operation.Operation;
import transcribe.application.core.operation.OperationRunner;
import transcribe.application.core.progress.BallClipRotatePulseProgress;
import transcribe.application.core.ui.UiUtils;
import transcribe.application.main.MainLayout;
import transcribe.application.security.AuthenticatedUser;
import transcribe.application.transcription.TranscriptionJpaDto;
import transcribe.core.cloud_storage.CloudStorage;
import transcribe.core.core.tuple.Tuple2;
import transcribe.domain.core.broadcaster.Broadcaster;
import transcribe.domain.core.broadcaster.Subscription;
import transcribe.domain.transcript.transcript_manager.TranscriptManager;
import transcribe.domain.transcript.transcript_part.part.PartModel;
import transcribe.domain.transcription.data.TranscriptionEntity;
import transcribe.domain.transcription.event.TranscriptionCreateUserEvent;
import transcribe.domain.transcription.event.TranscriptionUpdateUserEvent;

import java.net.URL;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@PageTitle("Transcribe")
@Route(value = "transcribe", layout = MainLayout.class)
@PermitAll
@Slf4j
public class TranscribeView extends Composite<VerticalLayout> {

    private final Broadcaster broadcaster;
    private final TranscriptManager transcriptManager;
    private final OperationRunner operationRunner;
    private final CloudStorage cloudStorage;
    private final List<Subscription> subscriptions = new ArrayList<>();
    private final JpaGrid<TranscriptionJpaDto, TranscriptionEntity, Long> grid;
    private final Long applicationUserId;
    private final HelperDownloadAnchor.Factory helperDownloadAnchorFactory;

    public TranscribeView(Broadcaster broadcaster,
                          TranscriptManager transcriptManager,
                          OperationRunner operationRunner,
                          CloudStorage cloudStorage,
                          AuthenticatedUser authenticatedUser) {
        this.broadcaster = broadcaster;
        this.transcriptManager = transcriptManager;
        this.operationRunner = operationRunner;
        this.cloudStorage = cloudStorage;
        this.applicationUserId = authenticatedUser.get().getId();
        this.helperDownloadAnchorFactory = HelperDownloadAnchor.newFactory(getContent());

        this.grid = new JpaGrid<>(
                JpaGridConfiguration.<TranscriptionJpaDto, TranscriptionEntity, Long>builder()
                        .beanType(TranscriptionJpaDto.class)
                        .service(SimpleJpaDtoService.ofBeanType(TranscriptionJpaDto.class))
                        .defaultSpecification((root, _, criteriaBuilder)
                                -> criteriaBuilder.equal(root.get("applicationUserId"), applicationUserId))
                        .build()
        );
        grid.removeThemeVariants(GridVariant.LUMO_COLUMN_BORDERS);
        grid.addThemeVariants(GridVariant.LUMO_NO_BORDER);
        grid.addColumns("name", "language", "createdAt");
        grid.addColumn("lengthMillis")
                .setRenderer(new TextRenderer<>(TranscribeView::newDuration))
                .setHeader("Length");
        grid.addColumn("status")
                .setSortable(false);
        grid.addComponentColumn(TranscribeView::newProgress)
                .setWidth("6rem");
        grid.addComponentColumn(this::newActions)
                .setWidth("6rem");

        grid.setAllColumnsResizable();
        grid.addFilters("name", "language", "createdAt");
        grid.addItemDoubleClickListener(e -> openTranscript(e.getItem()));

        var transcribeButton = new Button("Transcribe", LineAwesomeIcon.PODCAST_SOLID.create());
        transcribeButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        transcribeButton.addClickListener(_ -> new TranscribeDialog().open());

        var controls = new JpaGridControls<>(grid);
        controls.addTopRight(transcribeButton);

        var content = getContent();
        content.setAlignItems(FlexComponent.Alignment.CENTER);
        content.setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER);
        content.addAndExpand(controls);
    }

    private static String newDuration(TranscriptionJpaDto transcription) {
        long duration = Objects.requireNonNullElse(transcription.getLengthMillis(), 0L);

        return DurationFormatUtils.formatDuration(duration, "H'h' m'm' s's'");
    }

    private static Component newProgress(TranscriptionJpaDto transcription) {
        var status = transcription.getStatus();
        var layout = new HorizontalLayout();
        layout.setAlignItems(FlexComponent.Alignment.CENTER);
        layout.setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER);

        switch (status) {
            case FAILED -> layout.add(IconFactory.newIcon(VaadinIcon.CLOSE::create, "red", "1.5rem", "Error"));
            case COMPLETED -> layout.add(IconFactory.newIcon(VaadinIcon.CHECK::create, "green", "1.5rem", "Success"));
            case CREATED, ENHANCING_FAILED -> {
            }
            case DOWNLOADING_PUBLIC, PROCESSING, ENHANCING, TRANSCRIBING -> {
                var progress = new BallClipRotatePulseProgress();
                layout.add(progress);
            }
        }

        return layout;
    }

    private Button newActions(TranscriptionJpaDto transcription) {
        var button = new Button(LineAwesomeIcon.ELLIPSIS_V_SOLID.create());
        button.addThemeVariants(ButtonVariant.LUMO_SMALL);

        var contextMenu = new ContextMenu(button);
        contextMenu.setOpenOnClick(true);

        var viewTranscriptionHl = new HorizontalLayout(LineAwesomeIcon.FILE_ALT_SOLID.create(), new Text("Open"));
        contextMenu.addItem(viewTranscriptionHl, _ -> openTranscript(transcription));

        var renameHl = new HorizontalLayout(LineAwesomeIcon.EDIT_SOLID.create(), new Text("Rename"));
        contextMenu.addItem(
                renameHl, _ -> new RenameTranscriptionDialog(transcription)
                        .setSaveListener(grid::refreshItem)
                        .open()
        );

        var downloadHl = new HorizontalLayout(LineAwesomeIcon.DOWNLOAD_SOLID.create(), new Text("Download"));
        contextMenu.addItem(
                downloadHl,
                _ -> new DownloadTranscriptDialog(
                        transcription.getId(),
                        transcription.getName(),
                        helperDownloadAnchorFactory
                ).open()
        );

        return button;
    }

    private void openTranscript(TranscriptionJpaDto transcription) {
        var operation = Operation.<Tuple2<List<PartModel>, URL>>builder()
                .name("Fetching transcript")
                .description("Fetching transcript and signed URL on a long-running operation")
                .callable(() -> {
                    var partModels = transcriptManager.getTranscriptPartModels(transcription.getId());
                    var signedUrl = cloudStorage.getSignedUrl(transcription.getCloudUri(), Duration.ofDays(1));

                    return Tuple2.of(partModels, signedUrl);
                })
                .onSuccess(r ->
                        new OpenTranscriptDialog(transcription.getId(), transcription.getName(), r.getT1(), r.getT2())
                                .open()
                )
                .onSuccessNotify(false)
                .build();

        operationRunner.run(operation, UI.getCurrent());
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
                e -> UiUtils.safeAccess(
                        ui,
                        () -> grid.refreshItem(
                                grid.getService()
                                        .getById(e.getTranscriptionId())
                        )
                ),
                e -> Objects.equals(e.getApplicationUserId(), applicationUserId)
        );

        subscriptions.addAll(List.of(sub1, sub2));
    }

    @Override
    protected void onDetach(DetachEvent detachEvent) {
        super.onDetach(detachEvent);

        subscriptions.forEach(Subscription::remove);
        subscriptions.clear();
    }

}
