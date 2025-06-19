package echoflux.application.transcribe;

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
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import echoflux.application.core.jpa.grid.JpaGrid;
import echoflux.application.security.AuthenticatedUser;
import echoflux.domain.transcription.data.TranscriptionProjection;
import echoflux.domain.transcription.data.TranscriptionRepository;
import jakarta.annotation.security.PermitAll;
import org.springframework.data.jpa.domain.Specification;
import org.vaadin.lineawesome.LineAwesomeIcon;
import echoflux.application.core.component.HelperDownloadAnchor;
import echoflux.application.core.icon.IconFactory;
import echoflux.application.core.progress.BallClipRotatePulseProgress;
import echoflux.application.core.ui.UiUtils;
import echoflux.application.layout.MainLayout;
import echoflux.application.transcription.TranscriptionView;
import echoflux.core.core.bean.loader.BeanLoader;
import echoflux.domain.application_user.data.ApplicationUserEntity_;
import echoflux.domain.core.broadcaster.Broadcaster;
import echoflux.domain.core.broadcaster.Subscription;
import echoflux.domain.transcription.data.TranscriptionEntity;
import echoflux.domain.transcription.data.TranscriptionEntity_;
import echoflux.domain.transcription.event.TranscriptionCreateUserEvent;
import echoflux.domain.transcription.event.TranscriptionUpdateUserEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@PageTitle("Transcribe")
@Route(value = "transcribe", layout = MainLayout.class)
@PermitAll
public class TranscribeView extends Composite<VerticalLayout> {

    private final Broadcaster broadcaster;
    private final BeanLoader beanLoader;
    private final List<Subscription> subscriptions = new ArrayList<>();
    private final JpaGrid<TranscriptionProjection, TranscriptionEntity, Long> grid;
    private final Long applicationUserId;
    private final HelperDownloadAnchor.Factory helperDownloadAnchorFactory;

    public TranscribeView(TranscriptionRepository transcriptionRepository, Broadcaster broadcaster, BeanLoader beanLoader) {
        this.broadcaster = broadcaster;
        this.beanLoader = beanLoader;
        this.applicationUserId = AuthenticatedUser.getId();
        this.helperDownloadAnchorFactory = HelperDownloadAnchor.newFactory(getContent());

        Specification<TranscriptionEntity> defaultSpecification = (root, _, cb) -> cb.equal(
                root.get(TranscriptionEntity_.APPLICATION_USER).get(ApplicationUserEntity_.ID), applicationUserId
        );

        this.grid = new JpaGrid<>(TranscriptionProjection.class, transcriptionRepository);
        grid.addColumnWithFilter(TranscriptionEntity_.NAME);
        grid.addColumnWithFilter(TranscriptionEntity_.LANGUAGE);
        grid.addColumnWithFilter(TranscriptionEntity_.CREATED_AT)
                .setHeader("Date");
        grid.addColumnWithFilter(TranscriptionEntity_.LENGTH)
                .setHeader("Length");
        grid.addColumnWithFilter(TranscriptionEntity_.STATUS)
                .setSortable(false);
        grid.addComponentColumn(TranscribeView::newProgress)
                .setWidth("6rem");
        grid.addComponentColumn(this::newActions)
                .setWidth("6rem");
        grid.setDefaultSpecification(defaultSpecification);
        grid.removeThemeVariants(GridVariant.LUMO_COLUMN_BORDERS);
        grid.addClassName("body-cell-cursor-pointer");
        grid.addItemClickListener(
                e -> UI.getCurrent().navigate(TranscriptionView.class, e.getItem().getId())
        );

        var transcribeButton = new Button("Transcribe", LineAwesomeIcon.PODCAST_SOLID.create());
        transcribeButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        transcribeButton.addClickListener(
                _ -> new TranscribeDialog(beanLoader).open()
        );

        var controls = grid.withControls();
        controls.addTopLeft(transcribeButton);

        var content = getContent();
        content.setAlignItems(FlexComponent.Alignment.CENTER);
        content.setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER);
        content.addAndExpand(controls);
    }

    private static Component newProgress(TranscriptionProjection transcription) {
        var status = transcription.getStatus();
        var layout = new HorizontalLayout();
        layout.setAlignItems(FlexComponent.Alignment.CENTER);
        layout.setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER);

        switch (status) {
            case FAILED -> layout.add(IconFactory.newIcon(VaadinIcon.CLOSE::create, "red", "1.5rem", "Error"));
            case COMPLETED -> layout.add(IconFactory.newIcon(VaadinIcon.CHECK::create, "green", "1.5rem", "Success"));
            case CREATED -> {
            }
            case DOWNLOADING_PUBLIC, ENHANCING, TRANSCRIBING -> {
                var progress = new BallClipRotatePulseProgress();
                layout.add(progress);
            }
        }

        return layout;
    }

    private Button newActions(TranscriptionProjection transcription) {
        var button = new Button(LineAwesomeIcon.ELLIPSIS_V_SOLID.create());
        button.addThemeVariants(ButtonVariant.LUMO_SMALL);

        var contextMenu = new ContextMenu(button);
        contextMenu.setOpenOnClick(true);

        var viewTranscriptionHl = new HorizontalLayout(LineAwesomeIcon.FILE_ALT_SOLID.create(), new Text("Open"));
        contextMenu.addItem(
                viewTranscriptionHl,
                _ -> UI.getCurrent().navigate(TranscriptionView.class, transcription.getId())
        );

        var renameHl = new HorizontalLayout(LineAwesomeIcon.EDIT_SOLID.create(), new Text("Rename"));
        contextMenu.addItem(
                renameHl, _ ->
                        new RenameTranscriptionDialog(transcription, beanLoader)
                                .withSaveListener(grid::refreshItemById)
                                .open()
        );

        var downloadHl = new HorizontalLayout(LineAwesomeIcon.DOWNLOAD_SOLID.create(), new Text("Download"));
        contextMenu.addItem(
                downloadHl,
                _ -> new DownloadTranscriptDialog(transcription.getId(), helperDownloadAnchorFactory, beanLoader)
                        .open()
        );

        return button;
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
                        () -> grid.refreshItemById(e.getTranscriptionId())
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
