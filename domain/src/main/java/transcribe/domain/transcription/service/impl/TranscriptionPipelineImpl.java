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
import transcribe.domain.transcription.event.TranscriptionCreateUserEvent;
import transcribe.domain.transcription.event.TranscriptionUpdateUserEvent;
import transcribe.domain.transcription.mapper.TranscriptionMapper;
import transcribe.domain.transcription.service.TranscriptionMetadataService;
import transcribe.domain.transcription.service.TranscriptionPipeline;
import transcribe.domain.transcription.service.TranscriptionPipelineCommand;
import transcribe.domain.transcription.service.TranscriptionService;
import transcribe.domain.transcription.service.UpdateTranscriptionCommand;
import transcribe.domain.transcription.service.UpdateTranscriptionMetadataCommand;

import java.nio.file.Path;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class TranscriptionPipelineImpl implements TranscriptionPipeline {

    private final MediaDownloaderFactory mediaDownloaderFactory;
    private final AudioTranscoder audioTranscoder;
    private final CloudStorage cloudStorage;
    private final SpeechToText speechToText;
    private final TranscriptionMapper mapper;
    private final TranscriptionService transcriptionService;
    private final TranscriptionMetadataService transcriptionMetadataService;
    private final Broadcaster broadcaster;
    private final FFprobeWrapper ffprobeWrapper;

    @Override
    @LoggedMethodExecution(logReturn = false)
    public Optional<TranscribeResult> transcribe(TranscriptionPipelineCommand command) {
        var entity = transcriptionService.create(mapper.toCreateCommand(command));

        broadcaster.publishQuietly(
                TranscriptionCreateUserEvent.builder()
                        .applicationUserId(command.getApplicationUserId())
                        .transcription(entity)
                        .build()
        );

        try {
            return Optional.of(transcribeCreated(entity, command));
        } catch (Throwable e) {
            updateStatusAndPublish(entity.getId(), command.getApplicationUserId(), TranscriptionStatus.FAILED);

            return Optional.empty();
        }
    }

    @SneakyThrows
    private TranscribeResult transcribeCreated(TranscriptionEntity entity, TranscriptionPipelineCommand command) {
        var original = switch (command.getMediaOrigin()) {
            case LOCAL -> Path.of(command.getMediaUri());
            case PUBLIC -> {
                updateStatusAndPublish(entity.getId(), command.getApplicationUserId(), TranscriptionStatus.DOWNLOADING_PUBLIC);

                var downloadStart = System.nanoTime();
                var downloaded = mediaDownloaderFactory.getRequired(command.getMediaUri())
                        .download(command.getMediaUri());
                transcriptionMetadataService.update(
                        UpdateTranscriptionMetadataCommand.builder()
                                .id(entity.getMetadata().getId())
                                .downloadDurationMillis((System.nanoTime() - downloadStart) / 1_000_000)
                                .build()
                );

                yield downloaded;
            }
        };
        updateStatusAndPublish(entity.getId(), command.getApplicationUserId(), TranscriptionStatus.PROCESSING);

        var processStart = System.nanoTime();
        var result = ffprobeWrapper.ffprobe().probe(original.toAbsolutePath().toString());
        var lengthMillis = (long) (result.getFormat().duration * 1000);
        transcriptionMetadataService.update(
                UpdateTranscriptionMetadataCommand.builder()
                        .id(entity.getMetadata().getId())
                        .lengthMillis(lengthMillis)
                        .build()
        );
        broadcaster.publishQuietly(
                TranscriptionUpdateUserEvent.builder()
                        .applicationUserId(command.getApplicationUserId())
                        .transcription(transcriptionService.get(entity.getId()))
                        .build()
        );

        var transcodeParameters = TranscodeParameters.builder()
                .audioContainer(AudioContainer.OGG)
                .channels(1)
                .build();
        var ogg = audioTranscoder.transcode(original, transcodeParameters);
        var resourceInfo = cloudStorage.upload(ogg);
        transcriptionMetadataService.update(
                UpdateTranscriptionMetadataCommand.builder()
                        .id(entity.getMetadata().getId())
                        .processDurationMillis((System.nanoTime() - processStart) / 1_000_000)
                        .build()
        );
        transcriptionService.update(
                UpdateTranscriptionCommand.builder()
                        .id(entity.getId())
                        .cloudUri(resourceInfo.getUri())
                        .build()
        );

        updateStatusAndPublish(entity.getId(), command.getApplicationUserId(), TranscriptionStatus.TRANSCRIBING);
        var progressTrigger = new ProgressTrigger(
                Math.round(lengthMillis * transcriptionMetadataService.getRealTimeFactor()),
                2000,
                98,
                p -> updateTranscribeProgressAndPublish(
                        entity.getId(),
                        entity.getMetadata().getId(),
                        command.getApplicationUserId(),
                        p
                )
        );
        RunnableUtils.runQuietly(progressTrigger::start);

        var transcribeStart = System.nanoTime();
        var transcribeResult = speechToText.transcribe(resourceInfo.getUri(), command.getLanguage());
        transcriptionMetadataService.update(
                UpdateTranscriptionMetadataCommand.builder()
                        .id(entity.getMetadata().getId())
                        .transcribeDurationMillis((System.nanoTime() - transcribeStart) / 1_000_000)
                        .build()
        );

        RunnableUtils.runQuietly(progressTrigger::stop);

        transcriptionService.update(
                UpdateTranscriptionCommand.builder()
                        .id(entity.getId())
                        .transcript(transcribeResult.getTranscript())
                        .build()
        );

        updateStatusAndPublish(entity.getId(), command.getApplicationUserId(), TranscriptionStatus.FINISHED);
        RunnableUtils.runQuietly(() -> FileUtils.deleteIfExists(original, ogg));

        return transcribeResult;
    }

    private void updateStatusAndPublish(Long transcriptionId, Long applicationUserId, TranscriptionStatus status) {
        var updated = transcriptionService.update(
                UpdateTranscriptionCommand.builder()
                        .id(transcriptionId)
                        .status(status)
                        .build()
        );
        broadcaster.publishQuietly(
                TranscriptionUpdateUserEvent.builder()
                        .applicationUserId(applicationUserId)
                        .transcription(updated)
                        .build()
        );
    }

    private void updateTranscribeProgressAndPublish(Long transcriptionId,
                                                    Long transcriptionMetadataId,
                                                    Long applicationUserId,
                                                    int progress) {
        transcriptionMetadataService.update(
                UpdateTranscriptionMetadataCommand.builder()
                        .id(transcriptionMetadataId)
                        .transcribeProgress(progress)
                        .build()
        );
        broadcaster.publishQuietly(
                TranscriptionUpdateUserEvent.builder()
                        .applicationUserId(applicationUserId)
                        .transcription(transcriptionService.get(transcriptionId))
                        .build()
        );
    }

}
