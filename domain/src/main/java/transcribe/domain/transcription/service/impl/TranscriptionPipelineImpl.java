package transcribe.domain.transcription.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import transcribe.core.audio.common.AudioContainer;
import transcribe.core.audio.transcoder.AudioTranscoder;
import transcribe.core.audio.transcoder.TranscodeParameters;
import transcribe.core.cloud_storage.CloudStorage;
import transcribe.core.media.downloader.factory.MediaDownloaderFactory;
import transcribe.core.transcribe.SpeechToText;
import transcribe.domain.transcription.data.DetailedTranscriptionStatus;
import transcribe.domain.transcription.service.TranscriptionFeedback;
import transcribe.domain.transcription.service.TranscriptionPipeline;
import transcribe.domain.transcription.service.TranscriptionPipelineCommand;

import java.nio.file.Files;
import java.nio.file.Path;

@Component
@RequiredArgsConstructor
@Slf4j
public class TranscriptionPipelineImpl implements TranscriptionPipeline {

    private final MediaDownloaderFactory mediaDownloaderFactory;
    private final AudioTranscoder audioTranscoder;
    private final CloudStorage cloudStorage;
    private final SpeechToText speechToText;

    @SneakyThrows
    @Override
    public void transcribeWithFeedback(TranscriptionPipelineCommand command, TranscriptionFeedback feedback) {
        var statusChangeFeedback = feedback.getOnDetailedStatusChanged();
        statusChangeFeedback.accept(DetailedTranscriptionStatus.CREATED);

        Path original;
        if (command.isLocal()) {
            original = Path.of(command.getMediaUri());
        } else {
            statusChangeFeedback.accept(DetailedTranscriptionStatus.DOWNLOADING_PUBLIC);
            original = mediaDownloaderFactory.getRequired(command.getMediaUri())
                    .download(command.getMediaUri(), feedback.getDownloadPublicCallback());
        }

        statusChangeFeedback.accept(DetailedTranscriptionStatus.TRANSCODING);
        var transcodeParameters = TranscodeParameters.builder()
                .audioContainer(AudioContainer.OGG)
                .channels(1)
                .build();

        var ogg = audioTranscoder.transcode(original, transcodeParameters);

        statusChangeFeedback.accept(DetailedTranscriptionStatus.UPLOADING_TO_CLOUD);
        var resourceInfo = cloudStorage.upload(ogg);

        statusChangeFeedback.accept(DetailedTranscriptionStatus.TRANSCRIBING);
        var transcribeResult = speechToText.transcribe(resourceInfo.getUri(), command.getLanguage());

        statusChangeFeedback.accept(DetailedTranscriptionStatus.FINISHED);
        Files.deleteIfExists(original);
        Files.deleteIfExists(ogg);

        log.info("Transcription result: {}", transcribeResult.getTranscript());
    }

}
