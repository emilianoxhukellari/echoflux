package echoflux.domain.transcription.pipeline.impl;

import echoflux.core.audio.common.AudioInfo;
import echoflux.core.core.bean.loader.BeanLoader;
import echoflux.core.storage.SaveOptions;
import echoflux.core.storage.StorageProvider;
import echoflux.domain.completion.data.ScalarCompletionProjection;
import echoflux.domain.transcription.data.ScalarTranscriptionProjection;
import echoflux.domain.transcription_word.data.SequencedWord;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import echoflux.core.audio.ffmpeg.FFprobeWrapper;
import echoflux.core.audio.transcoder.AudioTranscoder;
import echoflux.core.audio.transcoder.TranscodeParameters;
import echoflux.core.core.collector.ParallelCollectors;
import echoflux.core.core.log.LoggedMethodExecution;
import echoflux.core.core.utils.MoreEnums;
import echoflux.core.core.utils.MoreFiles;
import echoflux.core.core.utils.MoreFunctions;
import echoflux.core.media.downloader.MediaDownloader;
import echoflux.core.settings.SettingsLoader;
import echoflux.core.transcribe.SpeechToText;
import echoflux.core.word.processor.SpeakerSegmentPartitioner;
import echoflux.core.word.processor.WordPatcher;
import echoflux.domain.completion.pipeline.CompleteCommand;
import echoflux.domain.completion.pipeline.CompletionsPipeline;
import echoflux.domain.core.broadcaster.Broadcaster;
import echoflux.domain.template.service.RenderTemplateCommand;
import echoflux.domain.template.service.TemplateService;
import echoflux.domain.transcription.data.TranscriptionStatus;
import echoflux.domain.transcription.event.TranscriptionCreateUserEvent;
import echoflux.domain.transcription.event.TranscriptionUpdateUserEvent;
import echoflux.domain.transcription.mapper.TranscriptionMapper;
import echoflux.domain.transcription.pipeline.TranscriptionPipeline;
import echoflux.domain.transcription.pipeline.TranscriptionPipelineCommand;
import echoflux.domain.transcription.pipeline.TranscriptionPipelineSettings;
import echoflux.domain.transcription.service.PatchTranscriptionCommand;
import echoflux.domain.transcription.service.TranscriptionService;

import java.nio.file.Path;
import java.util.Map;
import java.util.function.Supplier;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Slf4j
public class TranscriptionPipelineImpl implements TranscriptionPipeline {

    private final MediaDownloader mediaDownloader;
    private final AudioTranscoder audioTranscoder;
    private final SpeechToText speechToText;
    private final TranscriptionMapper transcriptionMapper;
    private final TranscriptionService transcriptionService;
    private final CompletionsPipeline completionsPipeline;
    private final Broadcaster broadcaster;
    private final FFprobeWrapper ffprobeWrapper;
    private final SettingsLoader settingsLoader;
    private final TemplateService templateService;
    private final BeanLoader beanLoader;

    @Override
    @LoggedMethodExecution(logReturn = false)
    public ScalarTranscriptionProjection transcribe(TranscriptionPipelineCommand command) {
        var transcription = transcriptionService.create(transcriptionMapper.toCommand(command));
        log.debug("Transcription created: [{}]", transcription.getId());

        broadcaster.publish(
                TranscriptionCreateUserEvent.builder()
                        .applicationUserId(command.getApplicationUserId())
                        .transcriptionId(transcription.getId())
                        .build()
        );

        try {
            return transcribeCreated(transcription, command);
        } catch (Throwable e) {
            log.error("Transcription failed", e);

            var withError = transcriptionService.patch(
                    PatchTranscriptionCommand.builder()
                            .id(transcription.getId())
                            .status(TranscriptionStatus.FAILED)
                            .error(e.getMessage())
                            .build()
            );
            broadcaster.publish(
                    TranscriptionUpdateUserEvent.builder()
                            .applicationUserId(command.getApplicationUserId())
                            .transcriptionId(withError.getId())
                            .build()
            );

            throw e;
        }
    }

    private ScalarTranscriptionProjection transcribeCreated(ScalarTranscriptionProjection transcription, TranscriptionPipelineCommand command) {
        var settings = settingsLoader.load(TranscriptionPipelineSettings.class);

        var originalMedia = resolveOriginalMedia(transcription, command);

        var durationProbedTranscription = probeDuration(transcription, originalMedia, command.getApplicationUserId());

        var audioInfo = transcode(durationProbedTranscription, originalMedia, settings, command.getApplicationUserId());

        MoreFunctions.runAsync(() -> MoreFiles.deleteIfExists(originalMedia));
        MoreFunctions.runAllParallelAbortOnFailure(
                () -> storageSave(transcription, audioInfo, settings.getStorageProvider()),
                () -> {
                    var transcribed = transcribe(transcription, audioInfo, command.getApplicationUserId());
                    enhance(transcribed, settings, command.getApplicationUserId());
                }
        );
        MoreFunctions.runAsync(() -> MoreFiles.deleteIfExists(audioInfo.audio()));

        return updateStatusAndPublish(
                UpdateStatusAndPublishCommand.builder()
                        .transcriptionId(transcription.getId())
                        .applicationUserId(command.getApplicationUserId())
                        .status(TranscriptionStatus.COMPLETED)
                        .build()
        );
    }

    private void storageSave(ScalarTranscriptionProjection transcription, AudioInfo audioInfo, StorageProvider storageProvider) {
        var storage = beanLoader.loadStorage(storageProvider);
        var saveOptions = SaveOptions.builder()
                .contentType(audioInfo.container().getContentType())
                .temp(false)
                .build();
        var uri = storage.save(audioInfo.audio(), saveOptions);

        transcriptionService.patch(
                PatchTranscriptionCommand.builder()
                        .id(transcription.getId())
                        .uri(uri)
                        .storageProvider(storageProvider)
                        .build()
        );
    }

    private Path resolveOriginalMedia(ScalarTranscriptionProjection transcription, TranscriptionPipelineCommand command) {
        var sourceUri = command.getSourceUri();

        return switch (command.getMediaOrigin()) {
            case LOCAL -> {
                log.debug("Transcription source is local: [{}]", sourceUri);

                yield Path.of(sourceUri);
            }
            case PUBLIC -> {
                log.debug("Transcription source is public: [{}]", sourceUri);

                updateStatusAndPublish(
                        UpdateStatusAndPublishCommand.builder()
                                .transcriptionId(transcription.getId())
                                .applicationUserId(command.getApplicationUserId())
                                .status(TranscriptionStatus.DOWNLOADING_PUBLIC)
                                .build()
                );

                yield mediaDownloader.download(sourceUri);
            }
        };
    }

    private ScalarTranscriptionProjection probeDuration(ScalarTranscriptionProjection transcription,
                                                        Path originalMedia,
                                                        Long applicationUserId) {
        updateStatusAndPublish(
                UpdateStatusAndPublishCommand.builder()
                        .transcriptionId(transcription.getId())
                        .applicationUserId(applicationUserId)
                        .status(TranscriptionStatus.FINDING_DURATION)
                        .build()
        );

        var length = ffprobeWrapper.getDuration(originalMedia);
        log.debug("Transcription duration found: [{}]", length);

        var withLength = transcriptionService.patch(
                PatchTranscriptionCommand.builder()
                        .id(transcription.getId())
                        .length(length)
                        .build()
        );
        log.debug("Transcription [{}] patched with length: [{}]", transcription.getId(), length);

        broadcaster.publish(
                TranscriptionUpdateUserEvent.builder()
                        .applicationUserId(applicationUserId)
                        .transcriptionId(withLength.getId())
                        .build()
        );

        return withLength;
    }

    private AudioInfo transcode(ScalarTranscriptionProjection transcription,
                                Path originalMedia,
                                TranscriptionPipelineSettings settings,
                                Long applicationUserId) {
        updateStatusAndPublish(
                UpdateStatusAndPublishCommand.builder()
                        .transcriptionId(transcription.getId())
                        .applicationUserId(applicationUserId)
                        .status(TranscriptionStatus.TRANSCODING)
                        .build()
        );

        var transcodeParameters = TranscodeParameters.builder()
                .audioContainer(settings.getTranscode().getContainer())
                .channels(settings.getTranscode().getChannels())
                .build();
        var transcoded = audioTranscoder.transcode(originalMedia, transcodeParameters);
        log.debug("Transcription [{}] transcoded to [{}]: [{}]",
                transcription.getId(), settings.getTranscode().getContainer(), transcoded);

        return AudioInfo.builder()
                .audio(transcoded)
                .container(settings.getTranscode().getContainer())
                .build();
    }

    private ScalarTranscriptionProjection transcribe(ScalarTranscriptionProjection transcription,
                                                     AudioInfo audioInfo,
                                                     Long applicationUserId) {
        updateStatusAndPublish(
                UpdateStatusAndPublishCommand.builder()
                        .transcriptionId(transcription.getId())
                        .applicationUserId(applicationUserId)
                        .status(TranscriptionStatus.TRANSCRIBING)
                        .build()
        );

        var words = speechToText.transcribe(audioInfo, transcription.getLanguage());

        log.debug("Saving original words for transcription [{}]", transcription.getId());
        var duration = MoreFunctions.runTimed(
                () -> transcriptionService.saveWords(transcription.getId(), words)
        );
        log.debug("Original words saved for transcription [{}] in [{}]ms", transcription.getId(), duration.toMillis());

        return transcription;
    }

    private void enhance(ScalarTranscriptionProjection transcription, TranscriptionPipelineSettings settings, Long applicationUserId) {
        updateStatusAndPublish(
                UpdateStatusAndPublishCommand.builder()
                        .transcriptionId(transcription.getId())
                        .applicationUserId(applicationUserId)
                        .status(TranscriptionStatus.ENHANCING)
                        .build()
        );

        var segments = transcriptionService.getTranscriptionSpeakerSegments(transcription.getId());
        int partitionWordLimit = settings.getPartition().getEnhanceWordLimit();
        var partitions = SpeakerSegmentPartitioner.partitionAll(segments, partitionWordLimit);

        var completeCommands = partitions.stream()
                .map(partition -> Map.<String, Object>of(
                                settings.getEnhanceCompletionContentDataModelKey(),
                                partition.content(),
                                settings.getEnhanceCompletionLanguageDataModelKey(),
                                MoreEnums.toDisplayName(transcription.getLanguage())
                        )
                ).map(dataModel -> templateService.render(
                                RenderTemplateCommand.builder()
                                        .name(settings.getEnhanceCompletionDynamicTemplateName())
                                        .dataModel(dataModel)
                                        .build()
                        )
                ).map(input -> CompleteCommand.builder()
                        .input(input)
                        .transcriptionId(transcription.getId())
                        .aiProvider(settings.getCompletionsAiProvider())
                        .build()
                )
                .toList();

        log.debug("Performing parallel completion with concurrency [{}] for transcription [{}]",
                settings.getPartition().getEnhanceConcurrency(), transcription.getId());

        var completionResults = completeCommands.stream()
                .<Supplier<ScalarCompletionProjection>>map(command -> () -> completionsPipeline.complete(command))
                .collect(ParallelCollectors.toList(settings.getPartition().getEnhanceConcurrency()));

        var combinedOutput = completionResults.stream()
                .map(ScalarCompletionProjection::getOutput)
                .map(StringUtils::strip)
                .collect(Collectors.joining(StringUtils.SPACE));

        log.debug("Saving enhanced words for transcription [{}]", transcription.getId());

        var words = transcriptionService.getTranscriptionWords(transcription.getId());
        var patchedWords = WordPatcher.patchAllFromText(words, combinedOutput, SequencedWord::new);
        var duration = MoreFunctions.runTimed(
                () -> transcriptionService.saveWords(transcription.getId(), patchedWords)
        );
        log.debug("Enhanced words saved for transcription [{}] in [{}]ms", transcription.getId(), duration.toMillis());
    }

    private ScalarTranscriptionProjection updateStatusAndPublish(UpdateStatusAndPublishCommand command) {
        var updatedTranscription = transcriptionService.patch(
                PatchTranscriptionCommand.builder()
                        .id(command.transcriptionId())
                        .status(command.status())
                        .build()
        );
        broadcaster.publish(
                TranscriptionUpdateUserEvent.builder()
                        .applicationUserId(command.applicationUserId())
                        .transcriptionId(updatedTranscription.getId())
                        .build()
        );

        return updatedTranscription;
    }

    @Builder
    private record UpdateStatusAndPublishCommand(Long transcriptionId,
                                                 Long applicationUserId,
                                                 TranscriptionStatus status) {
    }

}
