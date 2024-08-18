package com.example.application.views.home;

import com.example.application.core.audio.transcoder.AudioTranscoder;
import com.example.application.core.common.utils.UriUtils;
import com.example.application.core.media.downloader.MediaDownloader;
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

        var progressBar = new ProgressBar(0, 100);
        var progressBarLabelText = new NativeLabel(
                "Getting media from URL");
        progressBarLabelText.setId("pblabel");
        progressBar.getElement().setAttribute("aria-labelledby", "pblabel");

        var progressBarLabelValue = new Span("0%");
        var progressBarLabel = new HorizontalLayout(progressBarLabelText, progressBarLabelValue);
        progressBarLabel.setJustifyContentMode(FlexComponent.JustifyContentMode.BETWEEN);
        var progress = new VerticalLayout(progressBarLabel, progressBar);

        layoutRow2.add(findVideo, findVideoButton);

        HorizontalLayout layoutRow = new HorizontalLayout();
        VerticalLayout layoutColumn2 = new VerticalLayout();
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
