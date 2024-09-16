package transcribe.application.transcribe.media_provider.impl;

import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import lombok.Data;
import org.vaadin.lineawesome.LineAwesomeIcon;
import transcribe.application.core.dialog.Dialogs;
import transcribe.application.core.operation.Operation;
import transcribe.application.core.operation.OperationRunner;
import transcribe.application.core.spring.SpringContext;
import transcribe.application.transcribe.media_provider.MediaProvider;
import transcribe.application.transcribe.media_provider.MediaValue;
import transcribe.core.core.utils.UriUtils;
import transcribe.core.media.downloader.MediaFindResult;
import transcribe.core.media.downloader.factory.MediaDownloaderFactory;
import transcribe.core.run.RunnableUtils;
import transcribe.domain.transcription.data.MediaOrigin;

import java.util.Optional;
import java.util.function.Consumer;

public class PublicMediaProvider extends HorizontalLayout implements MediaProvider {

    private final MediaDownloaderFactory mediaDownloaderFactory;
    private final OperationRunner operationRunner;
    private final TextField searchUri;
    private final HorizontalLayout searchContainer;
    private Consumer<MediaValue> onReady;
    private Runnable onClientCleared;

    public PublicMediaProvider() {
        this.mediaDownloaderFactory = SpringContext.getBean(MediaDownloaderFactory.class);
        this.operationRunner = SpringContext.getBean(OperationRunner.class);

        var binder = new Binder<SearchUri>();
        binder.setBean(new SearchUri());

        this.searchUri = new TextField("Public URL");
        searchUri.setWidthFull();
        binder.forField(searchUri)
                .asRequired()
                .bind(SearchUri::getUri, SearchUri::setUri);
        var searchButton = new Button(LineAwesomeIcon.SEARCH_SOLID.create());
        searchButton.addClickListener(_ -> {
            if (binder.validate().isOk()) {
                findAndSetMedia();
            }
        });

        this.searchContainer = new HorizontalLayout();
        searchContainer.setPadding(false);
        searchContainer.addAndExpand(searchUri, searchButton);
        searchContainer.setAlignItems(Alignment.BASELINE);

        add(searchContainer);
        setSizeFull();
        setAlignItems(Alignment.CENTER);
    }

    private void findAndSetMedia() {
        var operation = Operation.<Optional<MediaFindResult>>builder()
                .name("Finding public media")
                .callable(() -> {
                    var uri = UriUtils.newUri(searchUri.getValue());

                    return mediaDownloaderFactory.findDownloader(uri)
                            .flatMap(d -> d.find(uri));
                })
                .onSuccess(r -> {
                    if (r.isEmpty()) {
                        Dialogs.info("Media not found", "Make sure the URL is correct.");
                    } else {
                        setMediaResult(r.get());
                        RunnableUtils.consumeIfPresent(
                                onReady,
                                new MediaValue(r.get().getUri(), r.get().getTitle(), MediaOrigin.PUBLIC)
                        );
                    }
                })
                .onError(_ -> Dialogs.warn("Media not found", "Make sure the URL is correct."))
                .onErrorNotify(false)
                .onSuccessNotify(false)
                .build();

        operationRunner.run(operation, UI.getCurrent());
    }

    private void setMediaResult(MediaFindResult result) {
        removeAll();
        add(newMediaContainer(result));
    }

    private HorizontalLayout newMediaContainer(MediaFindResult result) {
        var container = new HorizontalLayout();
        container.setSizeFull();
        container.setPadding(false);

        if (result.getThumbnailUri() != null) {
            var image = new Image(result.getThumbnailUri().toString(), "Thumbnail");
            image.setMaxHeight("100%");
            image.setMaxWidth("50%");
            image.getStyle().set("border-radius", "10px");
            container.add(image);
        }

        container.add(new Text(result.getTitle()));
        container.setAlignItems(Alignment.CENTER);

        var closeButton = new Button(VaadinIcon.CLOSE_SMALL.create());
        closeButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY, ButtonVariant.LUMO_SMALL);
        closeButton.addClickListener(_ -> {
            clear();
            RunnableUtils.runIfPresent(onClientCleared);
        });

        container.add(closeButton);

        return container;
    }

    @Override
    public void onReady(Consumer<MediaValue> onReady) {
        this.onReady = onReady;
    }

    @Override
    public void onClientCleared(Runnable onClientCleared) {
        this.onClientCleared = onClientCleared;
    }

    @Override
    public void clear() {
        searchUri.clear();
        removeAll();
        add(searchContainer);
    }

    @Data
    private static class SearchUri {

        private String uri;

    }

}
