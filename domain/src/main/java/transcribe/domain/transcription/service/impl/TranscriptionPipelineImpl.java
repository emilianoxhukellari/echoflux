package transcribe.domain.transcription.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Component;
import transcribe.core.audio.common.AudioContainer;
import transcribe.core.audio.ffmpeg.FFprobeWrapper;
import transcribe.core.audio.transcoder.AudioTranscoder;
import transcribe.core.audio.transcoder.TranscodeParameters;
import transcribe.core.cloud_storage.CloudStorage;
import transcribe.core.core.log.LoggedMethodExecution;
import transcribe.core.core.progress.ProgressTrigger;
import transcribe.core.core.utils.FileUtils;
import transcribe.core.media.downloader.factory.MediaDownloaderFactory;
import transcribe.core.run.RunnableUtils;
import transcribe.core.transcribe.SpeechToText;
import transcribe.core.transcribe.common.TranscribeResult;
import transcribe.domain.transcription.data.TranscriptionEntity;
import transcribe.domain.transcription.data.TranscriptionStatus;
import transcribe.domain.transcription.mapper.TranscriptionMapper;
import transcribe.domain.transcription.service.TranscriptionFeedback;
import transcribe.domain.transcription.service.TranscriptionPipeline;
import transcribe.domain.transcription.service.TranscriptionPipelineCommand;
import transcribe.domain.transcription.service.TranscriptionService;
import transcribe.domain.transcription.service.UpdateTranscriptionCommand;

import java.nio.file.Path;
import java.time.Duration;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class TranscriptionPipelineImpl implements TranscriptionPipeline {

    private final MediaDownloaderFactory mediaDownloaderFactory;
    private final AudioTranscoder audioTranscoder;
    private final CloudStorage cloudStorage;
    private final SpeechToText speechToText;
    private final TranscriptionMapper mapper;
    private final TranscriptionService service;
    private final FFprobeWrapper ffprobeWrapper;

    @Override
    @LoggedMethodExecution(logReturn = false)
    public Optional<TranscribeResult> transcribeWithFeedback(TranscriptionPipelineCommand command, TranscriptionFeedback feedback) {
        var entity = service.create(mapper.toCreateCommand(command));
        RunnableUtils.runSilent(() -> feedback.getOnStatusChanged().accept(TranscriptionStatus.CREATED));

        try {
            return Optional.of(transcribeCreatedWithFeedback(entity, command, feedback));
        } catch (Throwable e) {
            service.update(
                    UpdateTranscriptionCommand.builder()
                            .id(entity.getId())
                            .status(TranscriptionStatus.FAILED)
                            .build()
            );
            feedback.getOnStatusChanged().accept(TranscriptionStatus.FAILED);

            return Optional.empty();
        }
    }

    @SneakyThrows
    private TranscribeResult transcribeCreatedWithFeedback(TranscriptionEntity entity, TranscriptionPipelineCommand command, TranscriptionFeedback feedback) {
        var original = switch (command.getMediaOrigin()) {
            case LOCAL -> Path.of(command.getMediaUri());
            case PUBLIC -> {
                saveStatusAndSendFeedback(entity.getId(), TranscriptionStatus.DOWNLOADING_PUBLIC, feedback);

                yield mediaDownloaderFactory.getRequired(command.getMediaUri())
                        .download(command.getMediaUri(), feedback.getDownloadPublicCallback());
            }
        };
        saveStatusAndSendFeedback(entity.getId(), TranscriptionStatus.PROCESSING, feedback);

        var result = ffprobeWrapper.ffprobe().probe(original.toAbsolutePath().toString());
        service.update(
                UpdateTranscriptionCommand.builder()
                        .id(entity.getId())
                        .lengthMillis((long) (result.getFormat().duration * 1000))
                        .build()
        );

        var transcodeParameters = TranscodeParameters.builder()
                .audioContainer(AudioContainer.OGG)
                .channels(1)
                .build();
        var ogg = audioTranscoder.transcode(original, transcodeParameters);
        var resourceInfo = cloudStorage.upload(ogg);

        service.update(
                UpdateTranscriptionCommand.builder()
                        .id(entity.getId())
                        .cloudUri(resourceInfo.getUri())
                        .build()
        );

        saveStatusAndSendFeedback(entity.getId(), TranscriptionStatus.TRANSCRIBING, feedback);
        // todo: duration must be calculated using some statistics service
        var progressTrigger = new ProgressTrigger(Duration.ofSeconds(7), feedback.getTranscribeProgressCallback(), 95);
        RunnableUtils.runSilent(progressTrigger::start);

        var transcribeResult = speechToText.transcribe(resourceInfo.getUri(), command.getLanguage());
        RunnableUtils.runSilent(progressTrigger::stop);

        service.update(
                UpdateTranscriptionCommand.builder()
                        .id(entity.getId())
                        .transcript(transcribeResult.getTranscript())
                        .build()
        );

        saveStatusAndSendFeedback(entity.getId(), TranscriptionStatus.FINISHED, feedback);
        RunnableUtils.runSilent(() -> FileUtils.deleteIfExists(original, ogg));

        return transcribeResult;
    }

    /**
     * Throws on status save, but silently runs feedback callback.
     */
    private void saveStatusAndSendFeedback(Long transcriptionId,
                                           TranscriptionStatus status,
                                           TranscriptionFeedback feedback) {
        service.update(
                UpdateTranscriptionCommand.builder()
                        .id(transcriptionId)
                        .status(status)
                        .build()
        );
        RunnableUtils.runSilent(() -> feedback.getOnStatusChanged().accept(status));
    }

}
