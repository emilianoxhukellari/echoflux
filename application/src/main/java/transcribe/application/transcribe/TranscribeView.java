package transcribe.application.transcribe;

import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.PermitAll;
import lombok.extern.slf4j.Slf4j;
import transcribe.application.core.error.PropagatedApplicationException;
import transcribe.application.core.operation.Operation;
import transcribe.application.core.operation.OperationRunner;
import transcribe.application.main.MainLayout;
import transcribe.core.audio.transcoder.AudioTranscoder;
import transcribe.core.audio.transcoder.TranscodeParameters;
import transcribe.core.cloud_storage.CloudStorage;
import transcribe.core.common.utils.UriUtils;
import transcribe.core.media.downloader.MediaDownloader;
import transcribe.domain.operation.data.OperationType;

import java.net.URI;
import java.util.List;


@PageTitle("Transcribe")
@Route(value = "transcribe", layout = MainLayout.class)
@PermitAll
@Slf4j
public class TranscribeView extends Composite<VerticalLayout> {

    public TranscribeView(CloudStorage cloudStorage,
                          List<MediaDownloader> mediaDownloaderList,
                          AudioTranscoder audioTranscoder,
                          OperationRunner operationRunner) {

        var videoUrl = new TextField();
        videoUrl.setPlaceholder("Video URL");

        var operation = Operation.<URI>builder()
                .name("Transcribe")
                .callable(
                        () -> {
                            var downloader = mediaDownloaderList.stream()
                                    .filter(d -> d.supports(UriUtils.newUri(videoUrl.getValue())))
                                    .findFirst()
                                    .orElseThrow(() -> new PropagatedApplicationException("No downloader found for video URL"));

                            var downloadResult = downloader.download(UriUtils.newUri(videoUrl.getValue()));

                            var transcodeResult = audioTranscoder.transcode(downloadResult, TranscodeParameters.builder().build());
                            var uploadResult = cloudStorage.upload(transcodeResult);

                            return uploadResult.getUri();
                        }
                )
                .onSuccess(uri -> log.info("TT audio uploaded to {}", uri))
                .onError(e -> log.error("Error", e))
                .onFinally(() -> log.info("Finally"))
                .type(OperationType.NON_BLOCKING)
                .build();

        var button = new Button("Transcribe");
        button.addClickListener(_ -> operationRunner.run(operation));

        var verticalLayout = new VerticalLayout();
        verticalLayout.add(videoUrl, button);

        getContent().add(verticalLayout);

    }
}
