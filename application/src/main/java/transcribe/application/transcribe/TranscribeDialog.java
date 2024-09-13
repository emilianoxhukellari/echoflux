package transcribe.application.transcribe;

import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.DetachEvent;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.NativeLabel;
import com.vaadin.flow.component.progressbar.ProgressBar;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.component.upload.receivers.FileBuffer;
import lombok.extern.slf4j.Slf4j;
import org.vaadin.lineawesome.LineAwesomeIcon;
import transcribe.application.core.broadcaster.Broadcaster;
import transcribe.application.core.dialog.EnhancedDialog;
import transcribe.application.core.operation.Operation;
import transcribe.application.core.operation.OperationCallable;
import transcribe.application.core.operation.OperationRunner;
import transcribe.application.core.spring.SpringContext;
import transcribe.application.core.upload.UploadFileFactory;
import transcribe.application.security.AuthenticatedUser;
import transcribe.core.common.utils.UriUtils;
import transcribe.core.transcribe.common.Language;
import transcribe.domain.application_user.data.ApplicationUserEntity;
import transcribe.domain.operation.data.OperationType;
import transcribe.domain.transcription.data.DetailedTranscriptionStatus;
import transcribe.domain.transcription.service.TranscriptionFeedback;
import transcribe.domain.transcription.service.TranscriptionPipeline;
import transcribe.domain.transcription.service.TranscriptionPipelineCommand;

@Slf4j
public class TranscribeDialog extends EnhancedDialog {

    private final TranscriptionPipeline transcriptionPipeline = SpringContext.getBean(TranscriptionPipeline.class);
    private final AuthenticatedUser authenticatedUser = SpringContext.getBean(AuthenticatedUser.class);
    private final OperationRunner operationRunner = SpringContext.getBean(OperationRunner.class);
    private final Broadcaster broadcaster = SpringContext.getBean(Broadcaster.class);

    public TranscribeDialog() {
        var fileBuffer = new FileBuffer(UploadFileFactory.INSTANCE);
        var upload = new Upload();
        upload.setReceiver(fileBuffer);
        upload.setDropLabelIcon(LineAwesomeIcon.UPLOAD_SOLID.create());
        upload.setDropLabel(new NativeLabel("Upload media file"));

        var status = new NativeLabel("Status");
        var downloadProgress = new ProgressBar();
        downloadProgress.setMin(0);
        downloadProgress.setMax(100);

        var uriField = new TextField("Media URI");
        var startTranscribe = new Button("Start Transcribe", LineAwesomeIcon.PODCAST_SOLID.create());

        var ui = UI.getCurrent();
        var userId = authenticatedUser.find().orElseThrow().getId();

        var feedback = TranscriptionFeedback.builder()
                .onDetailedStatusChanged(s -> broadcaster.publish(new DetailedStatusEvent(s, userId)))
                .downloadPublicCallback(p -> broadcaster.publish(new DownloadProgressEvent(p, userId)))
                .build();

        startTranscribe.addClickListener(_ -> {
            var uri = UriUtils.newUri(uriField.getValue());
            var command = TranscriptionPipelineCommand.builder()
                    .local(false)
                    .name("Transcription")
                    .applicationUserId(authenticatedUser.find().map(ApplicationUserEntity::getId).orElseThrow())
                    .mediaUri(uri)
                    .language(Language.ENGLISH_US)
                    .build();

            var operation = Operation.builder()
                    .name("Transcribing")
                    .type(OperationType.NON_BLOCKING)
                    .beforeCall(this::close)
                    .callable(OperationCallable.ofRunnable(() -> transcriptionPipeline.transcribeWithFeedback(command, feedback)))
                    .build();

            operationRunner.run(operation, ui);
        });

        setHeaderTitle("Transcribe");

        setMinWidth("500px");
        add(upload, status, downloadProgress, uriField, startTranscribe);

        setModal(true);
    }

    @Override
    protected void onAttach(AttachEvent attachEvent) {
        super.onAttach(attachEvent);
    }

    @Override
    protected void onDetach(DetachEvent attachEvent) {
        super.onDetach(attachEvent);
    }

    public record DetailedStatusEvent(DetailedTranscriptionStatus status, Long userId) {
    }

    public record DownloadProgressEvent(int progress, Long userId) {
    }

}
