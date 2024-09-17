package transcribe.domain.transcription.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Component;
import transcribe.core.audio.common.AudioContainer;
import transcribe.core.audio.ffmpeg.FFprobeWrapper;
import transcribe.core.audio.transcoder.AudioTranscoder;
import transcribe.core.audio.transcoder.TranscodeParameters;
import transcribe.domain.core.broadcaster.Broadcaster;
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
import transcribe.domain.transcription.event.TranscriptionLengthEvent;
import transcribe.domain.transcription.event.TranscriptionProgressChangeEvent;
import transcribe.domain.transcription.event.TranscriptionStatusChangeUserEvent;
import transcribe.domain.transcription.mapper.TranscriptionMapper;
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
    private final Broadcaster broadcaster;
    private final FFprobeWrapper ffprobeWrapper;

    @Override
    @LoggedMethodExecution(logReturn = false)
    public Optional<TranscribeResult> transcribe(TranscriptionPipelineCommand command) {
        var entity = service.create(mapper.toCreateCommand(command));

        broadcaster.publishQuietly(
                TranscriptionStatusChangeUserEvent.builder()
                        .applicationUserId(command.getApplicationUserId())
                        .transcriptionId(entity.getId())
                        .status(TranscriptionStatus.CREATED)
                        .build()
        );

        try {
            return Optional.of(transcribeCreated(entity, command));
        } catch (Throwable e) {
            service.update(
                    UpdateTranscriptionCommand.builder()
                            .id(entity.getId())
                            .status(TranscriptionStatus.FAILED)
                            .build()
            );
            broadcaster.publishQuietly(
                    TranscriptionStatusChangeUserEvent.builder()
                            .applicationUserId(command.getApplicationUserId())
                            .transcriptionId(entity.getId())
                            .status(TranscriptionStatus.FAILED)
                            .build()
            );

            return Optional.empty();
        }
    }

    @SneakyThrows
    private TranscribeResult transcribeCreated(TranscriptionEntity entity, TranscriptionPipelineCommand command) {
        var original = switch (command.getMediaOrigin()) {
            case LOCAL -> Path.of(command.getMediaUri());
            case PUBLIC -> {
                saveStatusAndPublish(entity.getId(), command.getApplicationUserId(), TranscriptionStatus.DOWNLOADING_PUBLIC);

                yield mediaDownloaderFactory.getRequired(command.getMediaUri())
                        .download(
                                command.getMediaUri(),
                                p -> broadcaster.publishQuietly(
                                        TranscriptionProgressChangeEvent.builder()
                                                .applicationUserId(command.getApplicationUserId())
                                                .transcriptionId(entity.getId())
                                                .progress(p)
                                                .build()
                                )
                        );
            }
        };
        saveStatusAndPublish(entity.getId(), command.getApplicationUserId(), TranscriptionStatus.PROCESSING);

        var result = ffprobeWrapper.ffprobe().probe(original.toAbsolutePath().toString());
        var lengthMillis = (long) (result.getFormat().duration * 1000);
        service.update(
                UpdateTranscriptionCommand.builder()
                        .id(entity.getId())
                        .lengthMillis(lengthMillis)
                        .build()
        );
        broadcaster.publishQuietly(
                TranscriptionLengthEvent.builder()
                        .applicationUserId(command.getApplicationUserId())
                        .transcriptionId(entity.getId())
                        .lengthMillis(lengthMillis)
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

        saveStatusAndPublish(entity.getId(), command.getApplicationUserId(), TranscriptionStatus.TRANSCRIBING);
        // todo: duration must be calculated using some statistics service
        var progressTrigger = new ProgressTrigger(
                Duration.ofSeconds(7),
                p -> broadcaster.publishQuietly(
                        TranscriptionProgressChangeEvent.builder()
                                .applicationUserId(command.getApplicationUserId())
                                .transcriptionId(entity.getId())
                                .progress(p)
                                .build()
                ),
                95);
        RunnableUtils.runQuietly(progressTrigger::start);

        var transcribeResult = speechToText.transcribe(resourceInfo.getUri(), command.getLanguage());
        RunnableUtils.runQuietly(progressTrigger::stop);

        service.update(
                UpdateTranscriptionCommand.builder()
                        .id(entity.getId())
                        .transcript(transcribeResult.getTranscript())
                        .build()
        );

        saveStatusAndPublish(entity.getId(), command.getApplicationUserId(), TranscriptionStatus.FINISHED);
        RunnableUtils.runQuietly(() -> FileUtils.deleteIfExists(original, ogg));

        return transcribeResult;
    }

    /**
     * Throws on status save, but silently runs feedback callback.
     */
    private void saveStatusAndPublish(Long transcriptionId, Long applicationUserId, TranscriptionStatus status) {
        service.update(
                UpdateTranscriptionCommand.builder()
                        .id(transcriptionId)
                        .status(status)
                        .build()
        );
        broadcaster.publishQuietly(
                TranscriptionStatusChangeUserEvent.builder()
                        .applicationUserId(applicationUserId)
                        .transcriptionId(transcriptionId)
                        .status(status)
                        .build()
        );
    }

}
