package echoflux.application.transcribe.media_provider.impl;

import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.shared.Tooltip;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.value.ValueChangeMode;
import lombok.Data;
import org.vaadin.lineawesome.LineAwesomeIcon;
import echoflux.application.core.dialog.TsDialogs;
import echoflux.application.core.operation.Operation;
import echoflux.application.core.operation.OperationRunner;
import echoflux.application.transcribe.media_provider.MediaProvider;
import echoflux.application.transcribe.media_provider.MediaValue;
import echoflux.core.core.bean.loader.BeanLoader;
import echoflux.core.core.utils.EfUris;
import echoflux.core.media.downloader.MediaDownloader;
import echoflux.core.media.downloader.MediaFindResult;
import echoflux.core.core.utils.EfFunctions;
import echoflux.domain.transcription.data.MediaOrigin;

import java.time.Duration;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;

public class PublicMediaProvider extends HorizontalLayout implements MediaProvider {

    private final MediaDownloader mediaDownloader;
    private final OperationRunner operationRunner;
    private final TextField searchUri;
    private final HorizontalLayout searchContainer;
    private Consumer<MediaValue> onReady;
    private Runnable onClientCleared;

    public PublicMediaProvider(BeanLoader beanLoader) {
        Objects.requireNonNull(beanLoader, "beanLoader");

        this.mediaDownloader = beanLoader.load(MediaDownloader.class);
        this.operationRunner = beanLoader.load(OperationRunner.class);

        var binder = new Binder<SearchUri>();
        binder.setBean(new SearchUri());

        var searchButton = new Button(LineAwesomeIcon.SEARCH_SOLID.create());
        Tooltip.forComponent(searchButton)
                .setText("Search media");
        searchButton.addClickListener(_ -> {
            if (binder.writeBeanIfValid(binder.getBean())) {
                findAndSetMedia();
            }
        });

        this.searchUri = new TextField("Public URL");
        searchUri.setWidthFull();
        binder.forField(searchUri)
                .asRequired()
                .bind(SearchUri::getUri, SearchUri::setUri);
        searchUri.addValueChangeListener(_ -> {
            if (searchUri.isEmpty()) {
                searchButton.removeThemeVariants(ButtonVariant.LUMO_PRIMARY);
            } else {
                searchButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
            }
        });
        searchUri.setValueChangeMode(ValueChangeMode.LAZY);

        this.searchContainer = new HorizontalLayout();
        searchContainer.setPadding(false);
        searchContainer.setSpacing(false);
        searchContainer.getThemeList().set("spacing-s", true);
        searchContainer.getStyle().set("padding", "0 5px");
        searchContainer.addAndExpand(searchUri, searchButton);
        searchContainer.setAlignItems(Alignment.BASELINE);

        add(searchContainer);
        setSizeFull();
        setAlignItems(Alignment.CENTER);
    }

    private void findAndSetMedia() {
        var operation = Operation.<Optional<MediaFindResult>>builder()
                .name("Finding public media")
                .callable(() -> mediaDownloader.find(EfUris.newUri(searchUri.getValue())))
                .onSuccess(r -> {
                    if (r.isEmpty()) {
                        TsDialogs.info("Media not found", "Please make sure the URL is correct.");
                    } else {
                        setMediaResult(r.get());
                        EfFunctions.consumeIfPresent(
                                onReady,
                                new MediaValue(r.get().getUri(), r.get().getTitle(), MediaOrigin.PUBLIC)
                        );
                    }
                })
                .onError(_ -> TsDialogs.info("Media not found", "Please make sure the URL is correct."))
                .onErrorNotify(false)
                .onSuccessNotify(false)
                .timeout(Duration.ofMinutes(1))
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
            clearAndCleanup();
            EfFunctions.runIfPresent(onClientCleared);
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
    public void clearAndCleanup() {
        searchUri.clear();
        removeAll();
        add(searchContainer);
    }

    @Data
    private static class SearchUri {

        private String uri;

    }

}
