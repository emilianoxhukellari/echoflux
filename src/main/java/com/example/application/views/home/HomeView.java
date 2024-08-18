package com.example.application.views.home;

import com.example.application.core.audio.transcoder.AudioTranscoder;
import com.example.application.core.common.utils.UriUtils;
import com.example.application.core.media.downloader.MediaDownloader;
import com.example.application.views.MainLayout;
import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.NativeLabel;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.progressbar.ProgressBar;
import com.vaadin.flow.component.textfield.TextField;
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
                    AudioTranscoder audioTranscoder) {
        HorizontalLayout layoutRow2 = new HorizontalLayout();

        var textInput = new TextField();
        var button = new Button("Download");

        var progressBar = new ProgressBar(0, 100);
        var progressBarLabelText = new NativeLabel(
                "Getting media from URL");
        progressBarLabelText.setId("pblabel");
        progressBar.getElement().setAttribute("aria-labelledby", "pblabel");

        var progressBarLabelValue = new Span("0%");
        var progressBarLabel = new HorizontalLayout(progressBarLabelText, progressBarLabelValue);
        progressBarLabel.setJustifyContentMode(FlexComponent.JustifyContentMode.BETWEEN);
        var progress = new VerticalLayout(progressBarLabel, progressBar);

        var currentUI = UI.getCurrent();

        button.addClickListener(event -> {
            var url = textInput.getValue();
            log.info("Downloading media from URL: {}", url);
            Thread.ofVirtual().start(() -> {
                 var result = mediaDownloaderList.stream()
                        .filter(downloader -> downloader.supports(UriUtils.newUri(url)))
                        .findFirst()
                        .orElseThrow()
                        .find(UriUtils.newUri(url));
                        //.download(UriUtils.newUri(url), p -> currentUI.access(() -> {
                        //    progressBar.setValue(p);
                        //    progressBarLabelValue.setText(p + "%");
                        //}));

                if (result.isPresent()) {
                    log.info("Found media title: {}", result.get().getTitle());
                    log.info("Found media thumbnail: {}", result.get().getThumbnailUri());
                } else {
                    log.info("Media not found");
                }

               //var transcodeCommand = TranscodeCommand.builder()
               //        .source(file)
               //        .audioContainer(AudioContainer.MP3)
               //        .build();
               //var t = audioTranscoder.transcode(transcodeCommand);

               //log.info("Downloaded file: {}", file);
               //log.info("Transcoded file: {}", t);
            });
        });

        layoutRow2.add(textInput, button, progress);

        HorizontalLayout layoutRow = new HorizontalLayout();
        VerticalLayout layoutColumn2 = new VerticalLayout();
        VerticalLayout layoutColumn3 = new VerticalLayout();
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
