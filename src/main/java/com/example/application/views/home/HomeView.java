package com.example.application.views.home;

import com.example.application.core.audio.common.AudioContainer;
import com.example.application.core.audio.transcoder.AudioTranscoder;
import com.example.application.core.audio.transcoder.TranscodeParameters;
import com.example.application.core.cloud_storage.CloudStorage;
import com.example.application.core.common.utils.UriUtils;
import com.example.application.core.media.downloader.MediaDownloader;
import com.example.application.core.transcribe.common.Language;
import com.example.application.core.transcribe.SpeechToText;
import com.example.application.views.MainLayout;
import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.NativeLabel;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.progressbar.ProgressBar;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.component.upload.receivers.FileBuffer;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteAlias;
import com.vaadin.flow.theme.lumo.LumoUtility.Gap;
import jakarta.annotation.security.PermitAll;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@PageTitle("Home")
@Route(value = "", layout = MainLayout.class)
@RouteAlias(value = "", layout = MainLayout.class)
@PermitAll
@Slf4j
public class HomeView extends Composite<VerticalLayout> {

    public HomeView(List<MediaDownloader> mediaDownloaderList,
                    AudioTranscoder audioTranscoder,
                    SpeechToText speechToText,
                    CloudStorage cloudStorage) {
        HorizontalLayout layoutRow2 = new HorizontalLayout();

        var image = new Image();
        image.setWidth("400px");
        var title = new NativeLabel("Title");

        var findVideo = new TextField();
        findVideo.setPlaceholder("Enter a Facebook or YouTube video URL");
        var findVideoButton = new Button("Find Video");
        findVideoButton.addClickListener(e -> {
            mediaDownloaderList.stream().filter(d -> d.supports(UriUtils.newUri(findVideo.getValue())))
                    .findFirst()
                    .ifPresent(d -> {
                        var result = d.find(UriUtils.newUri(findVideo.getValue()));
                        result.ifPresent(r -> {
                            image.setSrc(r.getThumbnailUri().toString());
                            image.setAlt(r.getTitle());
                            title.setText(r.getTitle());
                        });
                    });
        });

        var textInput = new TextField();
        var button = new Button("Download");

        button.addClickListener(e -> {
            var downloadedFile = mediaDownloaderList.stream().filter(d -> d.supports(UriUtils.newUri(textInput.getValue())))
                    .findFirst()
                    .orElseThrow()
                    .download(UriUtils.newUri(textInput.getValue()));

            log.info("Downloaded file: {}", downloadedFile);
            var convertedFile = audioTranscoder.transcode(downloadedFile, TranscodeParameters.builder().audioContainer(AudioContainer.WAV).build());
            log.info("Converted file: {}", convertedFile);
            var resourceInfo = cloudStorage.upload(convertedFile);
            log.info("Uploaded file: {}", resourceInfo);
            var transcription = speechToText.transcribe(resourceInfo.getUri(), Language.ENGLISH_US);
            log.info("Transcription: {}", transcription);
        });

        var progressBar = new ProgressBar(0, 100);
        var progressBarLabelText = new NativeLabel(
                "Getting media from URL");
        progressBarLabelText.setId("pblabel");
        progressBar.getElement().setAttribute("aria-labelledby", "pblabel");

        var progressBarLabelValue = new Span("0%");
        var progressBarLabel = new HorizontalLayout(progressBarLabelText, progressBarLabelValue);
        progressBarLabel.setJustifyContentMode(FlexComponent.JustifyContentMode.BETWEEN);
        var progress = new VerticalLayout(progressBarLabel, progressBar);

        layoutRow2.add(textInput, button, progress);

        var upload = new Upload();
        var receiver = new FileBuffer();
        upload.setReceiver(receiver);

        upload.addFinishedListener(e -> {
            var file = receiver.getFileData().getFile().toPath();

            log.info("Downloaded file: {}", file);
            var convertedFile = audioTranscoder.transcode(file, TranscodeParameters.builder().build());
            log.info("Converted file: {}", convertedFile);
            var resourceInfo = cloudStorage.upload(convertedFile);
            log.info("Uploaded file: {}", resourceInfo);
            var transcription = speechToText.transcribe(resourceInfo.getUri(), Language.ENGLISH_US);
            log.info("Transcription: {}", transcription);
        });

        HorizontalLayout layoutRow = new HorizontalLayout();
        VerticalLayout layoutColumn2 = new VerticalLayout();

        layoutColumn2.add(upload);

        VerticalLayout layoutColumn3 = new VerticalLayout();

        layoutColumn3.add(image, title);

        HorizontalLayout layoutRow3 = new HorizontalLayout();
        getContent().setWidth("100%");
        getContent().getStyle().set("flex-grow", "1");
        layoutRow2.addClassName(Gap.MEDIUM);
        layoutRow2.setWidth("100%");
        layoutRow2.setHeight("min-content");
        layoutRow.addClassName(Gap.MEDIUM);
        layoutRow.setWidth("100%");
        layoutRow.getStyle().set("flex-grow", "1");
        layoutColumn2.getStyle().set("flex-grow", "1");
        layoutColumn3.setWidth("100%");
        layoutColumn3.getStyle().set("flex-grow", "1");
        layoutRow3.addClassName(Gap.MEDIUM);
        layoutRow3.setWidth("100%");
        layoutRow3.setHeight("min-content");
        getContent().add(layoutRow2);
        getContent().add(layoutRow);
        layoutRow.add(layoutColumn2);
        layoutRow.add(layoutColumn3);
        getContent().add(layoutRow3);
    }
}
