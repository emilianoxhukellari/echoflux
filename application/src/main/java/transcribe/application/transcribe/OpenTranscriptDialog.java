package transcribe.application.transcribe;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import org.apache.commons.lang3.Validate;
import org.vaadin.lineawesome.LineAwesomeIcon;
import transcribe.application.core.component.AudioTextConnector;
import transcribe.application.core.component.HelperDownloadAnchor;
import transcribe.application.core.dialog.EnhancedDialog;
import transcribe.domain.transcript.transcript_part.part.PartModel;

import java.net.URL;
import java.util.List;
import java.util.Objects;

public class OpenTranscriptDialog extends EnhancedDialog {

    public OpenTranscriptDialog(Long transcriptionId,
                                String transcriptionName,
                                List<PartModel> partModels,
                                URL signedUrl) {
        Objects.requireNonNull(transcriptionId);
        Validate.notBlank(transcriptionName);
        Objects.requireNonNull(partModels);
        Objects.requireNonNull(signedUrl);

        var helperDownloadAnchorFactory = HelperDownloadAnchor.newFactory(this);
        var audioConnector = new AudioTextConnector(transcriptionId, signedUrl.toString(), partModels);
        var downloadButton = new Button(
                LineAwesomeIcon.DOWNLOAD_SOLID.create(),
                _ -> new DownloadTranscriptDialog(
                        transcriptionId,
                        transcriptionName,
                        helperDownloadAnchorFactory
                ).open()
        );
        downloadButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        withTitle(transcriptionName);
        withContent(audioConnector);
        addFooterComponentMiddle(downloadButton);
        setModal(true);
        setWidth("700px");
        setMaxWidth("95vw");
        addClassName("padding-v-s");
    }

}
