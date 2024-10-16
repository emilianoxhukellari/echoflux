package transcribe.domain.transcription.pipeline.impl;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Component;
import transcribe.core.audio.common.AudioContainer;
import transcribe.core.audio.ffmpeg.FFprobeWrapper;
import transcribe.core.audio.transcoder.AudioTranscoder;
import transcribe.core.audio.transcoder.TranscodeParameters;
import transcribe.core.media.downloader.MediaDownloader;
import transcribe.domain.completion.pipeline.CompletionPipeline;
import transcribe.domain.core.broadcaster.Broadcaster;
import transcribe.core.cloud_storage.CloudStorage;
import transcribe.core.core.log.LoggedMethodExecution;
import transcribe.core.core.progress.ProgressTrigger;
import transcribe.core.core.utils.FileUtils;
import transcribe.core.function.FunctionUtils;
import transcribe.core.transcribe.SpeechToText;
import transcribe.core.transcribe.common.TranscribeResult;
import transcribe.domain.transcription.data.TranscriptionEntity;
import transcribe.domain.transcription.data.TranscriptionStatus;
import transcribe.domain.transcription.event.TranscriptionCreateUserEvent;
import transcribe.domain.transcription.event.TranscriptionUpdateUserEvent;
import transcribe.domain.transcription.mapper.TranscriptionMapper;
import transcribe.domain.transcription.pipeline.TranscriptionPipeline;
import transcribe.domain.transcription.pipeline.TranscriptionPipelineCommand;
import transcribe.domain.transcription.service.TranscriptionService;
import transcribe.domain.transcription.service.UpdateTranscriptionCommand;

import java.nio.file.Path;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class TranscriptionPipelineImpl implements TranscriptionPipeline {

    private final MediaDownloader mediaDownloader;
    private final AudioTranscoder audioTranscoder;
    private final CloudStorage cloudStorage;
    private final SpeechToText speechToText;
    private final TranscriptionMapper mapper;
    private final TranscriptionService transcriptionService;
    private final CompletionPipeline completionPipeline;
    private final Broadcaster broadcaster;
    private final FFprobeWrapper ffprobeWrapper;

    @Override
    @LoggedMethodExecution(logReturn = false)
    public Optional<TranscribeResult> transcribe(TranscriptionPipelineCommand command) {
        var entity = transcriptionService.create(mapper.toCreateCommand(command));

        broadcaster.publishQuietly(
                TranscriptionCreateUserEvent.builder()
                        .applicationUserId(command.getApplicationUserId())
                        .entity(entity)
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
                var downloadedTimed = FunctionUtils.getTimed(() -> mediaDownloader.download(command.getMediaUri()));

                transcriptionService.update(
                        UpdateTranscriptionCommand.builder()
                                .id(entity.getId())
                                .downloadDurationMillis(downloadedTimed.getDuration().toMillis())
                                .build()
                );

                yield downloadedTimed.getResult();
            }
        };
        updateStatusAndPublish(entity.getId(), command.getApplicationUserId(), TranscriptionStatus.PROCESSING);

        var processStart = System.nanoTime();
        var result = ffprobeWrapper.ffprobe().probe(original.toAbsolutePath().toString());
        var lengthMillis = (long) (result.getFormat().duration * 1000);
        var withLengthEntity = transcriptionService.update(
                UpdateTranscriptionCommand.builder()
                        .id(entity.getId())
                        .lengthMillis(lengthMillis)
                        .build()
        );
        broadcaster.publishQuietly(
                TranscriptionUpdateUserEvent.builder()
                        .applicationUserId(command.getApplicationUserId())
                        .entity(withLengthEntity)
                        .build()
        );

        var transcodeParameters = TranscodeParameters.builder()
                .audioContainer(AudioContainer.OGG)
                .channels(1)
                .build();
        var ogg = audioTranscoder.transcode(original, transcodeParameters);
        var resourceInfo = cloudStorage.upload(ogg);
        transcriptionService.update(
                UpdateTranscriptionCommand.builder()
                        .id(entity.getId())
                        .cloudUri(resourceInfo.getUri())
                        .processDurationMillis((System.nanoTime() - processStart) / 1_000_000)
                        .build()
        );

        updateStatusAndPublish(entity.getId(), command.getApplicationUserId(), TranscriptionStatus.TRANSCRIBING);
        var progressTrigger = new ProgressTrigger(
                Math.round(lengthMillis * transcriptionService.getRealTimeFactor()),
                2000,
                90,
                p -> updateTranscribeProgressAndPublish(
                        entity.getId(),
                        command.getApplicationUserId(),
                        p
                )
        );
        FunctionUtils.runQuietly(progressTrigger::start);

        var timedTranscribeResult = FunctionUtils.getTimed(
                () -> speechToText.transcribe(resourceInfo.getUri(), command.getLanguage())
        );
        var transcribeResult = timedTranscribeResult.getResult();
        transcriptionService.update(
                UpdateTranscriptionCommand.builder()
                        .id(entity.getId())
                        .transcribeDurationMillis(timedTranscribeResult.getDuration().toMillis())
                        .build()
        );
        FunctionUtils.runQuietly(progressTrigger::stop);

        transcriptionService.update(
                UpdateTranscriptionCommand.builder()
                        .id(entity.getId())
                        .transcript(transcribeResult.getTranscript())
                        .build()
        );

        updateStatusAndPublish(entity.getId(), command.getApplicationUserId(), TranscriptionStatus.ENHANCING);
        completionPipeline.complete(transcribeResult.getTranscript()).ifPresent(r -> transcriptionService.update(
                UpdateTranscriptionCommand.builder()
                        .id(entity.getId())
                        .completionId(r.getCompletionId())
                        .build()
        ));

        updateStatusAndPublish(entity.getId(), command.getApplicationUserId(), TranscriptionStatus.COMPLETED);
        FunctionUtils.runQuietly(() -> FileUtils.deleteIfExists(original, ogg));

        return transcribeResult;
    }

    private void updateStatusAndPublish(Long transcriptionId, Long applicationUserId, TranscriptionStatus status) {
        var updatedEntity = transcriptionService.update(
                UpdateTranscriptionCommand.builder()
                        .id(transcriptionId)
                        .status(status)
                        .build()
        );
        broadcaster.publishQuietly(
                TranscriptionUpdateUserEvent.builder()
                        .applicationUserId(applicationUserId)
                        .entity(updatedEntity)
                        .build()
        );
    }

    private void updateTranscribeProgressAndPublish(Long transcriptionId,
                                                    Long applicationUserId,
                                                    int progress) {
        var updatedEntity = transcriptionService.update(
                UpdateTranscriptionCommand.builder()
                        .id(transcriptionId)
                        .transcribeProgress(progress)
                        .build()
        );
        broadcaster.publishQuietly(
                TranscriptionUpdateUserEvent.builder()
                        .applicationUserId(applicationUserId)
                        .entity(updatedEntity)
                        .build()
        );
    }

}
