package transcribe.domain.transcription.pipeline.impl;

import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import transcribe.core.audio.ffmpeg.FFprobeWrapper;
import transcribe.core.audio.splitter.AudioPartition;
import transcribe.core.audio.splitter.AudioSegment;
import transcribe.core.audio.splitter.AudioSplitter;
import transcribe.core.audio.splitter.SplitAudioCommand;
import transcribe.core.audio.transcoder.AudioTranscoder;
import transcribe.core.audio.transcoder.TranscodeParameters;
import transcribe.core.cloud_storage.ResourceInfo;
import transcribe.core.core.collector.ParallelCollectors;
import transcribe.core.core.iterable.DoublyLinkedIterable;
import transcribe.core.core.supplier.MoreSuppliers;
import transcribe.core.core.tuple.Tuple2;
import transcribe.core.core.utils.MoreEnums;
import transcribe.core.media.downloader.MediaDownloader;
import transcribe.core.settings.SettingsLoader;
import transcribe.domain.completion.data.CompletionEntity;
import transcribe.domain.completion.pipeline.CompleteCommand;
import transcribe.domain.completion.pipeline.CompletionsPipeline;
import transcribe.domain.core.broadcaster.Broadcaster;
import transcribe.core.cloud_storage.CloudStorage;
import transcribe.core.core.log.LoggedMethodExecution;
import transcribe.core.core.utils.MoreFiles;
import transcribe.core.core.utils.MoreFunctions;
import transcribe.core.transcribe.SpeechToText;
import transcribe.domain.template.service.RenderTemplateCommand;
import transcribe.domain.template.service.TemplateService;
import transcribe.domain.transcript.transcript_manager.SaveEnhancedParts;
import transcribe.domain.transcript.transcript_manager.SaveOriginalParts;
import transcribe.domain.transcript.transcript_manager.TranscriptManager;
import transcribe.domain.transcript.transcript_manager.TranscriptPartition;
import transcribe.domain.transcript.transcript_part.part.PartModelUtils;
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
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Slf4j
public class TranscriptionPipelineImpl implements TranscriptionPipeline {

    private final MediaDownloader mediaDownloader;
    private final AudioTranscoder audioTranscoder;
    private final AudioSplitter audioSplitter;
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
    public TranscriptionEntity transcribe(TranscriptionPipelineCommand command) {
        var entity = transcriptionService.create(mapper.toCommand(command));
        log.debug("Transcription created: [{}]", entity);

        broadcaster.publishQuietly(
                TranscriptionCreateUserEvent.builder()
                        .applicationUserId(command.getApplicationUserId())
                        .transcriptionId(entity.getId())
                        .build()
        );

        try {
            return transcribeCreated(entity, command);
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
                            .applicationUserId(command.getApplicationUserId())
                            .transcriptionId(withError.getId())
                            .build()
            );

            throw e;
        }
    }

    @SneakyThrows
    private TranscriptionEntity transcribeCreated(TranscriptionEntity entity, TranscriptionPipelineCommand command) {
        var settings = settingsLoader.load(TranscriptionPipelineSettings.class);

        var originalMedia = resolveOriginalMedia(entity, command.getMediaOrigin(), command.getSourceUri());

        var withDuration = probeDuration(entity, originalMedia);

        var processResult = process(withDuration, originalMedia, settings);

        //todo: add diarization
        MoreFiles.deleteIfExists(originalMedia);

        var partitionsToTranscribe = new ArrayList<UploadedAudioPartition>();
        if (CollectionUtils.isEmpty(processResult.splitPartitions())) {
            log.debug("No split partitions available for transcription [{}], using single original audio", entity.getId());
            var audioSegment = AudioSegment.builder()
                    .startMillis(0L)
                    .endMillis(withDuration.getLengthMillis())
                    .build();

            partitionsToTranscribe.add(
                    UploadedAudioPartition.builder()
                            .audioSegment(audioSegment)
                            .resourceInfo(
                                    ResourceInfo.builder()
                                            .uri(processResult.entity().getCloudUri())
                                            .build()
                            )
                            .build()
            );
        } else {
            log.debug("Split partitions available for transcription [{}]", entity.getId());
            partitionsToTranscribe.addAll(processResult.splitPartitions());
        }

        var transcribed = transcribe(processResult.entity(), partitionsToTranscribe, settings);

        var resourcesToDelete = processResult.splitPartitions()
                .stream()
                .map(UploadedAudioPartition::resourceInfo)
                .toList();

        MoreFunctions.executeAllParallel(
                resourcesToDelete,
                r -> cloudStorage.delete(r.getResourceName(), true)
        );

        if (command.getEnhanced()) {
            try {
                enhance(transcribed);
            } catch (Throwable e) {
                var failedEnhancing = transcriptionService.patch(
                        PatchTranscriptionCommand.builder()
                                .id(entity.getId())
                                .status(TranscriptionStatus.ENHANCING_FAILED)
                                .error(e.getMessage())
                                .build()
                );
                broadcaster.publishQuietly(
                        TranscriptionUpdateUserEvent.builder()
                                .applicationUserId(command.getApplicationUserId())
                                .transcriptionId(failedEnhancing.getId())
                                .build()
                );

                return failedEnhancing;
            }
        }

        return updateStatusAndPublish(
                UpdateStatusAndPublishCommand.builder()
                        .transcriptionId(transcribed.getId())
                        .applicationUserId(transcribed.getApplicationUserId())
                        .status(TranscriptionStatus.COMPLETED)
                        .build()
        );
    }

    private Path resolveOriginalMedia(TranscriptionEntity entity, MediaOrigin mediaOrigin, URI sourceUri) {
        return switch (mediaOrigin) {
            case LOCAL -> {
                log.debug("Transcription source is local: [{}]", sourceUri);

                yield Path.of(sourceUri);
            }
            case PUBLIC -> {
                log.debug("Transcription source is public: [{}]", sourceUri);

                updateStatusAndPublish(
                        UpdateStatusAndPublishCommand.builder()
                                .transcriptionId(entity.getId())
                                .applicationUserId(entity.getApplicationUserId())
                                .status(TranscriptionStatus.DOWNLOADING_PUBLIC)
                                .build()
                );
                var downloadedTimed = MoreFunctions.getTimed(() -> mediaDownloader.download(sourceUri));
                log.debug("Transcription downloaded at [{}] in [{}]ms",
                        downloadedTimed.getResult(), downloadedTimed.getDuration().toMillis());

                transcriptionService.patch(
                        PatchTranscriptionCommand.builder()
                                .id(entity.getId())
                                .downloadDurationMillis(downloadedTimed.getDuration().toMillis())
                                .build()
                );
                log.debug("Transcription patched with download duration: [{}]", downloadedTimed.getDuration().toMillis());

                yield downloadedTimed.getResult();
            }
        };
    }

    @SneakyThrows
    private TranscriptionEntity probeDuration(TranscriptionEntity entity, Path originalMedia) {
        updateStatusAndPublish(
                UpdateStatusAndPublishCommand.builder()
                        .transcriptionId(entity.getId())
                        .applicationUserId(entity.getApplicationUserId())
                        .status(TranscriptionStatus.FINDING_DURATION)
                        .build()
        );

        var lengthMillis = ffprobeWrapper.getDuration(originalMedia).toMillis();
        log.debug("Transcription duration found: [{}]", lengthMillis);

        var withLengthEntity = transcriptionService.patch(
                PatchTranscriptionCommand.builder()
                        .id(entity.getId())
                        .lengthMillis(lengthMillis)
                        .build()
        );
        log.debug("Transcription [{}] patched with duration: [{}]ms", entity.getId(), lengthMillis);

        broadcaster.publishQuietly(
                TranscriptionUpdateUserEvent.builder()
                        .applicationUserId(entity.getApplicationUserId())
                        .transcriptionId(withLengthEntity.getId())
                        .build()
        );

        return withLengthEntity;
    }

    @SneakyThrows
    private ProcessResult process(TranscriptionEntity entity,
                                  Path originalMedia,
                                  TranscriptionPipelineSettings settings) {
        updateStatusAndPublish(
                UpdateStatusAndPublishCommand.builder()
                        .transcriptionId(entity.getId())
                        .applicationUserId(entity.getApplicationUserId())
                        .status(TranscriptionStatus.PROCESSING)
                        .build()
        );

        var processStart = System.nanoTime();

        var transcodeParameters = TranscodeParameters.builder()
                .audioContainer(settings.getTranscode().getContainer())
                .channels(settings.getTranscode().getChannels())
                .build();
        var transcoded = audioTranscoder.transcode(originalMedia, transcodeParameters);
        log.debug("Transcription [{}] transcoded to [{}]: [{}]",
                entity.getId(), settings.getTranscode().getContainer(), transcoded);

        var lengthMillis = Objects.requireNonNull(entity.getLengthMillis(), "Length is required");
        var splitPartitions = new ArrayList<UploadedAudioPartition>();
        var minSplitThresholdMillis = Duration.ofMinutes(settings.getPartition().getPartitionDurationMinutes())
                .plus(Duration.ofMinutes(settings.getPartition().getToleranceDurationMinutes()))
                .toMillis();
        log.debug("Min split threshold: [{}]ms", minSplitThresholdMillis);

        if (lengthMillis > minSplitThresholdMillis) {
            log.debug("Transcription [{}] will be split because length > min split threshold", entity.getId());

            var partitions = audioSplitter.split(
                    SplitAudioCommand.builder()
                            .audio(transcoded)
                            .partitionDuration(Duration.ofMinutes(settings.getPartition().getPartitionDurationMinutes()))
                            .toleranceDuration(Duration.ofMinutes(settings.getPartition().getToleranceDurationMinutes()))
                            .minSilenceDuration(Duration.ofSeconds(settings.getPartition().getMinSilenceDurationSeconds()))
                            .concurrency(settings.getPartition().getSplitConcurrency())
                            .build()
            );
            log.debug("Transcription [{}] split into [{}] partitions", entity.getId(), partitions.size());

            log.debug("Performing parallel upload for split partitions with concurrency [{}] for transcription [{}]",
                    settings.getPartition().getSplitConcurrency(), entity.getId());
            var uploadedAudioPartitions = partitions.stream()
                    .map(
                            p -> MoreSuppliers.of(
                                    () -> UploadedAudioPartition.builder()
                                            .audioSegment(p.getAudioSegment())
                                            .resourceInfo(cloudStorage.uploadTemp(p.getAudio()))
                                            .build()
                            )
                    )
                    .collect(ParallelCollectors.toList());
            log.debug("Transcription [{}] split partitions uploaded", entity.getId());

            splitPartitions.addAll(uploadedAudioPartitions);

            MoreFiles.deleteIfExists(
                    partitions.stream()
                            .map(AudioPartition::getAudio)
                            .toList()
            );
        }

        var resourceInfo = cloudStorage.upload(transcoded);
        log.debug("Transcription [{}] uploaded to cloud: [{}]", entity.getId(), resourceInfo);

        MoreFiles.deleteIfExists(transcoded);

        var processedEntity = transcriptionService.patch(
                PatchTranscriptionCommand.builder()
                        .id(entity.getId())
                        .cloudUri(resourceInfo.getUri())
                        .processDurationMillis((System.nanoTime() - processStart) / 1_000_000)
                        .build()
        );

        return ProcessResult.builder()
                .entity(processedEntity)
                .splitPartitions(splitPartitions)
                .build();
    }

    private TranscriptionEntity transcribe(TranscriptionEntity entity,
                                           List<UploadedAudioPartition> partitions,
                                           TranscriptionPipelineSettings settings) {
        updateStatusAndPublish(
                UpdateStatusAndPublishCommand.builder()
                        .transcriptionId(entity.getId())
                        .applicationUserId(entity.getApplicationUserId())
                        .status(TranscriptionStatus.TRANSCRIBING)
                        .build()
        );

        log.debug("Performing parallel transcription with concurrency [{}] for transcription [{}]",
                settings.getPartition().getTranscribeConcurrency(), entity.getId());

        var timedTranscribeResult = DoublyLinkedIterable.of(partitions)
                .nodeStream()
                .map(node -> {
                    var offset = node.getAllPrev().stream()
                            .map(p -> p.getValue().audioSegment().getDurationMillis())
                            .reduce(0L, Long::sum);

                    return Tuple2.of(node.getValue(), offset);
                })
                .map(valueOffset -> MoreSuppliers.of(() -> {
                    var partition = valueOffset.getT1();
                    var offset = valueOffset.getT2();

                    var words = speechToText.transcribe(partition.resourceInfo().getUri(), entity.getLanguage());
                    var offsetAdjustedWords = words.stream()
                            .map(w ->
                                    w.setStartOffsetMillis(w.getStartOffsetMillis() + offset)
                                            .setEndOffsetMillis(w.getEndOffsetMillis() + offset)
                            )
                            .toList();

                    return TranscriptPartition.builder()
                            .speechToTextWords(offsetAdjustedWords)
                            .build();
                }))
                .collect(ParallelCollectors.toListTimed(settings.getPartition().getTranscribeConcurrency()));

        var allWords = timedTranscribeResult.getResult()
                .stream()
                .flatMap(p -> p.getSpeechToTextWords().stream())
                .toList();

       // var t = MoreFunctions.runTimed(() -> transcriptionManager.saveOriginalWords(
       //         SaveOriginalWordsCommand.builder()
       //                 .transcriptionId(entity.getId())
       //                 .words(allWords)
       //                 .build()
       // ));
       // log.info("Transcription [{}] words saved in [{}]ms", entity.getId(), t.toMillis());

        transcriptManager.saveOriginalParts(
                SaveOriginalParts.builder()
                        .partitions(timedTranscribeResult.getResult())
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

    private void enhance(TranscriptionEntity entity) {
        updateStatusAndPublish(
                UpdateStatusAndPublishCommand.builder()
                        .transcriptionId(entity.getId())
                        .applicationUserId(entity.getApplicationUserId())
                        .status(TranscriptionStatus.ENHANCING)
                        .build()
        );

        var transcriptPartitionsWithMetadata = transcriptManager.getTranscriptPartitionsWithMetadata(entity.getId());

        var settings = settingsLoader.load(TranscriptionPipelineSettings.class);

        var completionInputs = transcriptPartitionsWithMetadata.stream()
                .map(partition -> Map.<String, Object>of(
                                settings.getEnhanceCompletionTextDataModelKey(),
                                partition,
                                settings.getEnhanceCompletionLanguageDataModelKey(),
                                MoreEnums.toDisplayName(entity.getLanguage())
                        )
                )
                .map(dataModel -> templateService.render(
                        RenderTemplateCommand.builder()
                                .name(settings.getEnhanceCompletionDynamicTemplateName())
                                .dataModel(dataModel)
                                .build()
                ))
                .toList();

        log.debug("Performing parallel completion with concurrency [{}] for transcription [{}]",
                settings.getPartition().getEnhanceConcurrency(), entity.getId());
        var completionEntities = completionInputs.stream()
                .map(input -> MoreSuppliers.of(() -> completionsPipeline.complete(
                        CompleteCommand.builder()
                                .input(input)
                                .transcriptionId(entity.getId())
                                .aiProvider(settings.getCompletionsAiProvider())
                                .build()
                )))
                .collect(ParallelCollectors.toList(settings.getPartition().getEnhanceConcurrency()));

        var combinedOutput = completionEntities.stream()
                .map(CompletionEntity::getOutput)
                .map(StringUtils::strip)
                .collect(Collectors.joining(StringUtils.LF + StringUtils.LF));

        var partModels = PartModelUtils.parse(combinedOutput, transcriptManager.getTranscriptPartModels(entity.getId()));

        transcriptManager.saveEnhancedParts(
                SaveEnhancedParts.builder()
                        .transcriptionId(entity.getId())
                        .partModels(partModels)
                        .build()
        );
    }

    private TranscriptionEntity updateStatusAndPublish(UpdateStatusAndPublishCommand command) {
        var updatedEntity = transcriptionService.patch(
                PatchTranscriptionCommand.builder()
                        .id(command.transcriptionId())
                        .status(command.status())
                        .build()
        );
        broadcaster.publishQuietly(
                TranscriptionUpdateUserEvent.builder()
                        .applicationUserId(command.applicationUserId())
                        .transcriptionId(updatedEntity.getId())
                        .build()
        );

        return updatedEntity;
    }

    @Builder
    private record ProcessResult(TranscriptionEntity entity, List<UploadedAudioPartition> splitPartitions) {
    }

    @Builder
    private record UploadedAudioPartition(AudioSegment audioSegment, ResourceInfo resourceInfo) {
    }

    @Builder
    private record UpdateStatusAndPublishCommand(Long transcriptionId,
                                                 Long applicationUserId,
                                                 TranscriptionStatus status) {
    }

}
