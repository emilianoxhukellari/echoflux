package transcribe.domain.transcription.pipeline.impl;

import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import transcribe.core.audio.common.AudioContainer;
import transcribe.core.audio.ffmpeg.FFprobeWrapper;
import transcribe.core.audio.transcoder.AudioTranscoder;
import transcribe.core.audio.transcoder.TranscodeParameters;
import transcribe.core.media.downloader.MediaDownloader;
import transcribe.core.settings.SettingsLoader;
import transcribe.domain.completion.pipeline.CompletionsPipeline;
import transcribe.domain.core.broadcaster.Broadcaster;
import transcribe.core.cloud_storage.CloudStorage;
import transcribe.core.core.log.LoggedMethodExecution;
import transcribe.core.core.progress.ProgressTrigger;
import transcribe.core.core.utils.FileUtils;
import transcribe.core.function.FunctionUtils;
import transcribe.core.transcribe.SpeechToText;
import transcribe.domain.template.service.RenderTemplateCommand;
import transcribe.domain.template.service.TemplateService;
import transcribe.domain.transcript.transcript_manager.SaveAsPartsCommand;
import transcribe.domain.transcript.transcript_manager.TranscriptManager;
import transcribe.domain.transcription.data.MediaOrigin;
import transcribe.domain.transcription.data.TranscriptionEntity;
import transcribe.domain.transcription.data.TranscriptionStatus;
import transcribe.domain.transcription.event.TranscriptionCreateUserEvent;
import transcribe.domain.transcription.event.TranscriptionUpdateUserEvent;
import transcribe.domain.transcription.mapper.TranscriptionMapper;
import transcribe.domain.transcription.pipeline.TranscriptionPipeline;
import transcribe.domain.transcription.pipeline.TranscriptionPipelineCommand;
import transcribe.domain.transcription.pipeline.TranscriptionPipelineSettings;
import transcribe.domain.transcription.service.PatchTranscriptionCommand;
import transcribe.domain.transcription.service.TranscriptionService;

import java.net.URI;
import java.nio.file.Path;
import java.util.Map;
import java.util.Optional;

@Component
@RequiredArgsConstructor
@Slf4j
public class TranscriptionPipelineImpl implements TranscriptionPipeline {

    private final MediaDownloader mediaDownloader;
    private final AudioTranscoder audioTranscoder;
    private final CloudStorage cloudStorage;
    private final SpeechToText speechToText;
    private final TranscriptionMapper mapper;
    private final TranscriptionService transcriptionService;
    private final TranscriptManager transcriptManager;
    private final CompletionsPipeline completionsPipeline;
    private final Broadcaster broadcaster;
    private final FFprobeWrapper ffprobeWrapper;
    private final SettingsLoader settingsLoader;
    private final TemplateService templateService;

    @Override
    @LoggedMethodExecution(logReturn = false)
    public Optional<TranscriptionEntity> transcribe(TranscriptionPipelineCommand command) {
        var entity = transcriptionService.create(mapper.toCommand(command));

        broadcaster.publishQuietly(
                TranscriptionCreateUserEvent.builder()
                        .applicationUserId(command.getApplicationUserId())
                        .entity(entity)
                        .build()
        );

        try {
            return Optional.of(transcribeCreated(entity, command));
        } catch (Throwable e) {
            log.error("Transcription failed", e);

            var withError = transcriptionService.patch(
                    PatchTranscriptionCommand.builder()
                            .id(entity.getId())
                            .status(TranscriptionStatus.FAILED)
                            .error(e.getMessage())
                            .build()
            );
            broadcaster.publishQuietly(
                    TranscriptionUpdateUserEvent.builder()
                            .applicationUserId( command.getApplicationUserId())
                            .entity(withError)
                            .build()
            );

            return Optional.empty();
        }
    }

    @SneakyThrows
    private TranscriptionEntity transcribeCreated(TranscriptionEntity entity, TranscriptionPipelineCommand command) {
        var originalMedia = resolveOriginalMedia(entity, command.getMediaOrigin(), command.getSourceUri());

        var withDuration = probeDuration(entity, originalMedia);

        var processed = process(withDuration, originalMedia);

        FileUtils.deleteIfExists(originalMedia);

        var transcribed = transcribe(processed);

        if (command.getEnhanced()) {
            enhance(transcribed);
        }

        return updateStatusAndPublish(entity.getId(), entity.getApplicationUserId(), TranscriptionStatus.COMPLETED);
    }

    private Path resolveOriginalMedia(TranscriptionEntity entity, MediaOrigin mediaOrigin, URI sourceUri) {
        return switch (mediaOrigin) {
            case LOCAL -> Path.of(sourceUri);
            case PUBLIC -> {
                updateStatusAndPublish(entity.getId(), entity.getApplicationUserId(), TranscriptionStatus.DOWNLOADING_PUBLIC);
                var downloadedTimed = FunctionUtils.getTimed(() -> mediaDownloader.download(sourceUri));

                transcriptionService.patch(
                        PatchTranscriptionCommand.builder()
                                .id(entity.getId())
                                .downloadDurationMillis(downloadedTimed.getDuration().toMillis())
                                .build()
                );

                yield downloadedTimed.getResult();
            }
        };
    }

    @SneakyThrows
    private TranscriptionEntity probeDuration(TranscriptionEntity entity, Path originalMedia) {
        updateStatusAndPublish(entity.getId(), entity.getApplicationUserId(), TranscriptionStatus.FINDING_DURATION);

        var result = ffprobeWrapper.ffprobe().probe(originalMedia.toAbsolutePath().toString());
        var lengthMillis = (long) (result.getFormat().duration * 1000);
        var withLengthEntity = transcriptionService.patch(
                PatchTranscriptionCommand.builder()
                        .id(entity.getId())
                        .lengthMillis(lengthMillis)
                        .build()
        );
        broadcaster.publishQuietly(
                TranscriptionUpdateUserEvent.builder()
                        .applicationUserId(entity.getApplicationUserId())
                        .entity(withLengthEntity)
                        .build()
        );

        return withLengthEntity;
    }

    @SneakyThrows
    private TranscriptionEntity process(TranscriptionEntity entity, Path originalMedia) {
        updateStatusAndPublish(entity.getId(), entity.getApplicationUserId(), TranscriptionStatus.PROCESSING);

        var processStart = System.nanoTime();

        var transcodeParameters = TranscodeParameters.builder()
                .audioContainer(AudioContainer.OGG)
                .channels(1)
                .build();
        var ogg = audioTranscoder.transcode(originalMedia, transcodeParameters);

        var resourceInfo = cloudStorage.upload(ogg);
        FileUtils.deleteIfExists(ogg);

        return transcriptionService.patch(
                PatchTranscriptionCommand.builder()
                        .id(entity.getId())
                        .cloudUri(resourceInfo.getUri())
                        .processDurationMillis((System.nanoTime() - processStart) / 1_000_000)
                        .build()
        );
    }

    private TranscriptionEntity transcribe(TranscriptionEntity entity) {
        updateStatusAndPublish(entity.getId(), entity.getApplicationUserId(), TranscriptionStatus.TRANSCRIBING);

        var progressTrigger = new ProgressTrigger(
                Math.round(entity.getLengthMillis() * transcriptionService.getRealTimeFactor()),
                2000,
                95,
                p -> updateTranscribeProgressAndPublish(
                        entity.getId(),
                        entity.getApplicationUserId(),
                        p
                )
        );
        FunctionUtils.runQuietly(progressTrigger::start);
        var timedTranscribeResult = FunctionUtils.getTimed(
                () -> speechToText.transcribe(entity.getCloudUri(), entity.getLanguage())
        );
        FunctionUtils.runQuietly(progressTrigger::stop);

        var words = timedTranscribeResult.getResult();
        transcriptManager.saveAsParts(
                SaveAsPartsCommand.builder()
                        .words(words)
                        .transcriptionId(entity.getId())
                        .build()
        );

        return transcriptionService.patch(
                PatchTranscriptionCommand.builder()
                        .id(entity.getId())
                        .transcribeDurationMillis(timedTranscribeResult.getDuration().toMillis())
                        .build()
        );
    }

    private TranscriptionEntity enhance(TranscriptionEntity entity) {
        updateStatusAndPublish(entity.getId(), entity.getApplicationUserId(), TranscriptionStatus.ENHANCING);

        var transcriptWithTimestamps = transcriptManager.getTranscriptWithTimestamps(entity.getId());

        var settings = settingsLoader.load(TranscriptionPipelineSettings.class);
        var dataModel = Map.<String, Object>of(
                settings.getEnhanceCompletionTextDataModelKey(),
                transcriptWithTimestamps
        );
        var completionInput = templateService.render(
                RenderTemplateCommand.builder()
                        .name(settings.getEnhanceCompletionDynamicTemplateName())
                        .dataModel(dataModel)
                        .build()
        );
        var completionResult = completionsPipeline.complete(completionInput);

        if (completionResult.isPresent()) {
            return transcriptionService.patch(
                    PatchTranscriptionCommand.builder()
                            .id(entity.getId())
                            .completionId(completionResult.get().getCompletionId())
                            .build()
            );
        }

        return entity;
    }

    private TranscriptionEntity updateStatusAndPublish(Long transcriptionId, Long applicationUserId, TranscriptionStatus status) {
        var updatedEntity = transcriptionService.patch(
                PatchTranscriptionCommand.builder()
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

        return updatedEntity;
    }

    private void updateTranscribeProgressAndPublish(Long transcriptionId,
                                                    Long applicationUserId,
                                                    int progress) {
        var updatedEntity = transcriptionService.patch(
                PatchTranscriptionCommand.builder()
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
