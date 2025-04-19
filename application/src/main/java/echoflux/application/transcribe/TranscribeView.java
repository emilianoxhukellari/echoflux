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
import jakarta.annotation.security.PermitAll;
import org.springframework.data.jpa.domain.Specification;
import org.vaadin.lineawesome.LineAwesomeIcon;
import echoflux.application.core.component.HelperDownloadAnchor;
import echoflux.application.core.icon.IconFactory;
import echoflux.application.core.jpa.dto.impl.SimpleJpaDtoService;
import echoflux.application.core.jpa.grid.JpaGrid;
import echoflux.application.core.jpa.grid.JpaGridConfiguration;
import echoflux.application.core.jpa.grid.JpaGridControls;
import echoflux.application.core.progress.BallClipRotatePulseProgress;
import echoflux.application.core.ui.UiUtils;
import echoflux.application.layout.MainLayout;
import echoflux.application.security.AuthenticatedUser;
import echoflux.application.transcription.TranscriptionJpaDto;
import echoflux.application.transcription.TranscriptionJpaDto_;
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
    private final JpaGrid<TranscriptionJpaDto, TranscriptionEntity, Long> grid;
    private final Long applicationUserId;
    private final HelperDownloadAnchor.Factory helperDownloadAnchorFactory;

    public TranscribeView(Broadcaster broadcaster, AuthenticatedUser authenticatedUser, BeanLoader beanLoader) {
        this.broadcaster = broadcaster;
        this.beanLoader = beanLoader;
        this.applicationUserId = authenticatedUser.get().getId();
        this.helperDownloadAnchorFactory = HelperDownloadAnchor.newFactory(getContent());

        Specification<TranscriptionEntity> defaultSpecification = (root, _, criteriaBuilder) -> criteriaBuilder.equal(
                root.get(TranscriptionEntity_.APPLICATION_USER).get(ApplicationUserEntity_.ID), applicationUserId
        );

        this.grid = new JpaGrid<>(
                JpaGridConfiguration.<TranscriptionJpaDto, TranscriptionEntity, Long>builder()
                        .beanType(TranscriptionJpaDto.class)
                        .service(new SimpleJpaDtoService<>(TranscriptionJpaDto.class, beanLoader))
                        .beanLoader(beanLoader)
                        .defaultSpecification(defaultSpecification)
                        .build()
        );
        grid.removeThemeVariants(GridVariant.LUMO_COLUMN_BORDERS);
        grid.addClassName("body-cell-cursor-pointer");
        grid.addColumn(TranscriptionJpaDto_.NAME)
                .setWidth("25rem");
        grid.addColumn(TranscriptionJpaDto_.LANGUAGE);
        grid.addColumn(TranscriptionJpaDto_.CREATED_AT)
                .setHeader("Date");
        grid.addColumn(TranscriptionJpaDto_.LENGTH)
                .setHeader("Length");
        grid.addColumn(TranscriptionJpaDto_.STATUS)
                .setSortable(false);
        grid.addComponentColumn(TranscribeView::newProgress)
                .setWidth("6rem");
        grid.addComponentColumn(this::newActions)
                .setWidth("6rem");

        grid.setAllColumnsResizable();
        grid.addFilters(
                TranscriptionJpaDto_.NAME,
                TranscriptionJpaDto_.LANGUAGE,
                TranscriptionJpaDto_.CREATED_AT
        );
        grid.addItemClickListener(
                e -> UI.getCurrent().navigate(TranscriptionView.class, e.getItem().getId())
        );

        var transcribeButton = new Button("Transcribe", LineAwesomeIcon.PODCAST_SOLID.create());
        transcribeButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        transcribeButton.addClickListener(
                _ -> new TranscribeDialog(beanLoader).open()
        );

        var controls = new JpaGridControls<>(grid);
        controls.addTopLeft(transcribeButton);

        var content = getContent();
        content.setAlignItems(FlexComponent.Alignment.CENTER);
        content.setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER);
        content.addAndExpand(controls);
    }

    private static Component newProgress(TranscriptionJpaDto transcription) {
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

    private Button newActions(TranscriptionJpaDto transcription) {
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
                                .setSaveListener(grid::refreshItem)
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
