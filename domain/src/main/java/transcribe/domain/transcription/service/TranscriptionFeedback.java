package transcribe.domain.transcription.service;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import transcribe.core.core.no_op.NoOp;
import transcribe.core.core.progress.ProgressCallback;
import transcribe.core.media.downloader.MediaDownloadProgressCallback;
import transcribe.domain.transcription.data.TranscriptionStatus;

import java.util.function.Consumer;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TranscriptionFeedback {

    @NotNull
    @Builder.Default
    private Consumer<TranscriptionStatus> onStatusChanged = NoOp.consumer();

    @NotNull
    @Builder.Default
    private MediaDownloadProgressCallback downloadPublicCallback = NoOp.mediaDownloadProgressCallback();

    @NotNull
    @Builder.Default
    private ProgressCallback transcribeProgressCallback = NoOp.progressCallback();

}
