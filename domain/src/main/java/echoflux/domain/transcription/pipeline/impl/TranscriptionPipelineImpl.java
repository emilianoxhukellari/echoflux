package echoflux.domain.transcription.pipeline.impl;

import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.ListUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import echoflux.core.audio.ffmpeg.FFprobeWrapper;
import echoflux.core.audio.splitter.AudioPartition;
import echoflux.core.audio.splitter.AudioSegment;
import echoflux.core.audio.splitter.AudioSplitter;
import echoflux.core.audio.splitter.SplitAudioCommand;
import echoflux.core.audio.transcoder.AudioTranscoder;
import echoflux.core.audio.transcoder.TranscodeParameters;
import echoflux.core.cloud_storage.CloudDeleteCommand;
import echoflux.core.cloud_storage.CloudStorage;
import echoflux.core.cloud_storage.CloudUploadCommand;
import echoflux.core.cloud_storage.GetSignedUrlOfUriCommand;
import echoflux.core.cloud_storage.ResourceInfo;
import echoflux.core.core.collector.ParallelCollectors;
import echoflux.core.core.iterable.DoublyLinkedIterable;
import echoflux.core.core.log.LoggedMethodExecution;
import echoflux.core.core.supplier.MoreSuppliers;
import echoflux.core.core.tuple.Tuple2;
import echoflux.core.core.utils.TsEnums;
import echoflux.core.core.utils.TsFiles;
import echoflux.core.core.utils.TsFunctions;
import echoflux.core.diarization.AudioDiarizer;
import echoflux.core.diarization.DiarizationEntry;
import echoflux.core.media.downloader.MediaDownloader;
import echoflux.core.settings.SettingsLoader;
import echoflux.core.transcribe.SpeechToText;
import echoflux.core.word.common.Word;
import echoflux.core.word.processor.SpeakerSegmentPartitioner;
import echoflux.core.word.processor.WordAssembler;
import echoflux.core.word.processor.WordPatcher;
import echoflux.domain.completion.data.CompletionProjection;
import echoflux.domain.completion.pipeline.CompleteCommand;
import echoflux.domain.completion.pipeline.CompletionsPipeline;
import echoflux.domain.core.broadcaster.Broadcaster;
import echoflux.domain.template.service.RenderTemplateCommand;
import echoflux.domain.template.service.TemplateService;
import echoflux.domain.transcription.data.TranscriptionProjection;
import echoflux.domain.transcription.data.TranscriptionStatus;
import echoflux.domain.transcription.event.TranscriptionCreateUserEvent;
import echoflux.domain.transcription.event.TranscriptionUpdateUserEvent;
import echoflux.domain.transcription.manager.TranscriptionManager;
import echoflux.domain.transcription.mapper.TranscriptionMapper;
import echoflux.domain.transcription.pipeline.TranscriptionPipeline;
import echoflux.domain.transcription.pipeline.TranscriptionPipelineCommand;
import echoflux.domain.transcription.pipeline.TranscriptionPipelineSettings;
import echoflux.domain.transcription.service.PatchTranscriptionCommand;
import echoflux.domain.transcription.service.TranscriptionService;
import echoflux.domain.transcription_word.data.WordDto;

import java.net.URISyntaxException;
import java.nio.file.Path;
import java.time.Duration;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
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
    private final TranscriptionMapper transcriptionMapper;
    private final TranscriptionService transcriptionService;
    private final TranscriptionManager transcriptionManager;
    private final CompletionsPipeline completionsPipeline;
    private final Broadcaster broadcaster;
    private final FFprobeWrapper ffprobeWrapper;
    private final SettingsLoader settingsLoader;
    private final TemplateService templateService;
    private final AudioDiarizer audioDiarizer;

    @Override
    @LoggedMethodExecution(logReturn = false)
    public TranscriptionProjection transcribe(TranscriptionPipelineCommand command) {
        var transcription = transcriptionService.create(transcriptionMapper.toCommand(command));
        log.debug("Transcription created: [{}]", transcription.id());

        broadcaster.publishQuietly(
                TranscriptionCreateUserEvent.builder()
                        .applicationUserId(command.getApplicationUserId())
                        .transcriptionId(transcription.id())
                        .build()
        );

        try {
            return transcribeCreated(transcription, command);
        } catch (Throwable e) {
            log.error("Transcription failed", e);

            var withError = transcriptionService.patch(
                    PatchTranscriptionCommand.builder()
                            .id(transcription.id())
                            .status(TranscriptionStatus.FAILED)
                            .error(e.getMessage())
                            .build()
            );
            broadcaster.publishQuietly(
                    TranscriptionUpdateUserEvent.builder()
                            .applicationUserId(command.getApplicationUserId())
                            .transcriptionId(withError.id())
                            .build()
            );

            throw e;
        }
    }

    @SneakyThrows({URISyntaxException.class})
    private CompletableFuture<List<DiarizationEntry>> diarizeAsync(TranscriptionProjection transcription) {
        var publicUri = cloudStorage.getSignedUrl(
                GetSignedUrlOfUriCommand.builder()
                        .cloudUri(transcription.cloudUri())
                        .duration(Duration.ofHours(1))
                        .build()
        );
        var uri = publicUri.toURI();

        return TsFunctions.getAsync(() -> audioDiarizer.diarize(uri));
    }

    private TranscriptionProjection transcribeCreated(TranscriptionProjection transcription, TranscriptionPipelineCommand command) {
        var settings = settingsLoader.load(TranscriptionPipelineSettings.class);

        var originalMedia = resolveOriginalMedia(transcription, command);

        var durationProbedTranscription = probeDuration(transcription, originalMedia, command.getApplicationUserId());

        var transcodeResult = transcode(durationProbedTranscription, originalMedia, settings, command.getApplicationUserId());

        var uploadedTranscription = upload(
                durationProbedTranscription,
                transcodeResult.audio(),
                transcodeResult.contentType(),
                command.getApplicationUserId()
        );

        var diarizeFuture = diarizeAsync(uploadedTranscription);

        var uploadedSplits = split(uploadedTranscription, transcodeResult.audio(), settings, command.getApplicationUserId());

        TsFiles.deleteIfExists(originalMedia, transcodeResult.audio());

        var transcribed = transcribe(uploadedTranscription, uploadedSplits, diarizeFuture, settings, command.getApplicationUserId());

        deleteUploadedAudioPartitions(uploadedSplits);

        if (command.getEnhanced()) {
            enhance(transcribed, settings, command.getApplicationUserId());
        }

        return updateStatusAndPublish(
                UpdateStatusAndPublishCommand.builder()
                        .transcriptionId(transcribed.id())
                        .applicationUserId(command.getApplicationUserId())
                        .status(TranscriptionStatus.COMPLETED)
                        .build()
        );
    }

    @LoggedMethodExecution
    private Path resolveOriginalMedia(TranscriptionProjection transcription, TranscriptionPipelineCommand command) {
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
                                .transcriptionId(transcription.id())
                                .applicationUserId(command.getApplicationUserId())
                                .status(TranscriptionStatus.DOWNLOADING_PUBLIC)
                                .build()
                );

                yield mediaDownloader.download(sourceUri);
            }
        };
    }

    @SneakyThrows
    private TranscriptionProjection probeDuration(TranscriptionProjection transcription,
                                                  Path originalMedia,
                                                  Long applicationUserId) {
        updateStatusAndPublish(
                UpdateStatusAndPublishCommand.builder()
                        .transcriptionId(transcription.id())
                        .applicationUserId(applicationUserId)
                        .status(TranscriptionStatus.FINDING_DURATION)
                        .build()
        );

        var length = ffprobeWrapper.getDuration(originalMedia);
        log.debug("Transcription duration found: [{}]", length);

        var withLength = transcriptionService.patch(
                PatchTranscriptionCommand.builder()
                        .id(transcription.id())
                        .length(length)
                        .build()
        );
        log.debug("Transcription [{}] patched with length: [{}]", transcription.id(), length);

        broadcaster.publishQuietly(
                TranscriptionUpdateUserEvent.builder()
                        .applicationUserId(applicationUserId)
                        .transcriptionId(withLength.id())
                        .build()
        );

        return withLength;
    }

    private TranscodeResult transcode(TranscriptionProjection transcription,
                                      Path originalMedia,
                                      TranscriptionPipelineSettings settings,
                                      Long applicationUserId) {
        updateStatusAndPublish(
                UpdateStatusAndPublishCommand.builder()
                        .transcriptionId(transcription.id())
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
                transcription.id(), settings.getTranscode().getContainer(), transcoded);

        return TranscodeResult.builder()
                .audio(transcoded)
                .contentType(settings.getTranscode().getContainer().getContentType())
                .build();
    }

    private TranscriptionProjection upload(TranscriptionProjection transcription,
                                           Path audio,
                                           String contentType,
                                           Long applicationUserId) {
        updateStatusAndPublish(
                UpdateStatusAndPublishCommand.builder()
                        .transcriptionId(transcription.id())
                        .applicationUserId(applicationUserId)
                        .status(TranscriptionStatus.UPLOADING)
                        .build()
        );

        var resourceInfo = cloudStorage.upload(
                CloudUploadCommand.builder()
                        .path(audio)
                        .contentType(contentType)
                        .build()
        );
        log.debug("Transcription [{}] uploaded to cloud: [{}]", transcription.id(), resourceInfo);

        return transcriptionService.patch(
                PatchTranscriptionCommand.builder()
                        .id(transcription.id())
                        .cloudUri(resourceInfo.getUri())
                        .build()
        );
    }

    private List<UploadedAudioPartition> split(TranscriptionProjection transcription,
                                               Path audio,
                                               TranscriptionPipelineSettings settings,
                                               Long applicationUserId) {
        updateStatusAndPublish(
                UpdateStatusAndPublishCommand.builder()
                        .transcriptionId(transcription.id())
                        .applicationUserId(applicationUserId)
                        .status(TranscriptionStatus.SPLITTING)
                        .build()
        );

        var length = Objects.requireNonNull(transcription.length(), "Length is required");
        var minSplitThreshold = Duration.ofMinutes(settings.getPartition().getDurationMinutes())
                .plus(Duration.ofMinutes(settings.getPartition().getToleranceDurationMinutes()));

        if (length.compareTo(minSplitThreshold) < 0) {
            log.debug("Transcription [{}] will not be split because length <= min split threshold", transcription.id());
            return List.of();
        }

        var partitions = audioSplitter.split(
                SplitAudioCommand.builder()
                        .audio(audio)
                        .partitionDuration(Duration.ofMinutes(settings.getPartition().getDurationMinutes()))
                        .toleranceDuration(Duration.ofMinutes(settings.getPartition().getToleranceDurationMinutes()))
                        .minSilenceDuration(Duration.ofSeconds(settings.getPartition().getMinSilenceDurationSeconds()))
                        .concurrency(settings.getPartition().getSplitConcurrency())
                        .build()
        );

        log.debug("Transcription [{}] split into [{}] partitions", transcription.id(), partitions.size());

        log.debug("Performing parallel upload for split partitions with concurrency [{}] for transcription [{}]",
                settings.getPartition().getSplitConcurrency(), transcription.id());

        var uploadedAudioPartitions = partitions.stream()
                .map(p -> MoreSuppliers.of(() -> UploadedAudioPartition.builder()
                        .audioSegment(p.getAudioSegment())
                        .resourceInfo(
                                cloudStorage.upload(
                                        CloudUploadCommand.builder()
                                                .path(p.getAudio())
                                                .temp(true)
                                                .build()
                                )
                        )
                        .build())
                )
                .collect(ParallelCollectors.toList());

        TsFiles.deleteIfExists(
                partitions.stream()
                        .map(AudioPartition::getAudio)
                        .toList()
        );

        return uploadedAudioPartitions;
    }

    private TranscriptionProjection transcribe(TranscriptionProjection transcription,
                                               List<UploadedAudioPartition> uploadedSplits,
                                               CompletableFuture<List<DiarizationEntry>> diarizeFuture,
                                               TranscriptionPipelineSettings settings,
                                               Long applicationUserId) {
        updateStatusAndPublish(
                UpdateStatusAndPublishCommand.builder()
                        .transcriptionId(transcription.id())
                        .applicationUserId(applicationUserId)
                        .status(TranscriptionStatus.TRANSCRIBING)
                        .build()
        );

        var partitions = CollectionUtils.isNotEmpty(uploadedSplits)
                ? uploadedSplits
                : List.of(UploadedAudioPartition.of(transcription));

        log.debug("Performing parallel transcription with concurrency [{}] for transcription [{}]",
                settings.getPartition().getTranscribeConcurrency(), transcription.id());

        var transcribeResults = DoublyLinkedIterable.of(partitions)
                .nodeStream()
                .map(node -> {
                    long offsetShift = node.getAllPrev()
                            .stream()
                            .map(p -> p.getValue().audioSegment().getDurationMillis())
                            .reduce(0L, Long::sum);

                    return Tuple2.of(node.getValue(), offsetShift);
                })
                .map(valueOffset -> MoreSuppliers.of(() -> {
                    var partition = valueOffset.getT1();
                    long offsetShift = valueOffset.getT2();

                    var words = speechToText.transcribe(partition.resourceInfo().getUri(), transcription.language());

                    return words.stream()
                            .map(w -> w.shiftOffsets(offsetShift))
                            .toList();
                }))
                .collect(ParallelCollectors.toList(settings.getPartition().getTranscribeConcurrency()));

        var speechToTextWords = transcribeResults.stream()
                .flatMap(Collection::stream)
                .toList();

        var diarizationEntries = diarizeFuture.join();
        var words = WordAssembler.assembleAll(speechToTextWords, diarizationEntries, Word::new);

        log.debug("Saving original words for transcription [{}]", transcription.id());
        var duration = TsFunctions.runTimed(
                () -> transcriptionManager.saveWords(transcription.id(), words)
        );
        log.debug("Original words saved for transcription [{}] in [{}]ms", transcription.id(), duration.toMillis());

        return transcription;
    }

    private void enhance(TranscriptionProjection transcription, TranscriptionPipelineSettings settings, Long applicationUserId) {
        updateStatusAndPublish(
                UpdateStatusAndPublishCommand.builder()
                        .transcriptionId(transcription.id())
                        .applicationUserId(applicationUserId)
                        .status(TranscriptionStatus.ENHANCING)
                        .build()
        );

        var segments = transcriptionManager.getTranscriptionSpeakerSegments(transcription.id());
        int partitionWordLimit = settings.getPartition().getEnhanceWordLimit();
        var partitions = SpeakerSegmentPartitioner.partitionAll(segments, partitionWordLimit);

        var completeCommands = partitions.stream()
                .map(partition -> Map.<String, Object>of(
                                settings.getEnhanceCompletionContentDataModelKey(),
                                partition.content(),
                                settings.getEnhanceCompletionLanguageDataModelKey(),
                                TsEnums.toDisplayName(transcription.language())
                        )
                ).map(dataModel -> templateService.render(
                                RenderTemplateCommand.builder()
                                        .name(settings.getEnhanceCompletionDynamicTemplateName())
                                        .dataModel(dataModel)
                                        .build()
                        )
                ).map(input -> CompleteCommand.builder()
                        .input(input)
                        .transcriptionId(transcription.id())
                        .aiProvider(settings.getCompletionsAiProvider())
                        .build()
                )
                .toList();

        log.debug("Performing parallel completion with concurrency [{}] for transcription [{}]",
                settings.getPartition().getEnhanceConcurrency(), transcription.id());

        var completionResults = completeCommands.stream()
                .map(command -> MoreSuppliers.of(() -> completionsPipeline.complete(command)))
                .collect(ParallelCollectors.toList(settings.getPartition().getEnhanceConcurrency()));

        var combinedOutput = completionResults.stream()
                .map(CompletionProjection::output)
                .map(StringUtils::strip)
                .collect(Collectors.joining(StringUtils.SPACE));

        log.debug("Saving enhanced words for transcription [{}]", transcription.id());
        var words = transcriptionManager.getTranscriptionSpeakerWords(transcription.id());
        var patchedWords = WordPatcher.patchAllFromText(words, combinedOutput, WordDto::new);
        var duration = TsFunctions.runTimed(
                () -> transcriptionManager.saveWords(transcription.id(), patchedWords)
        );
        log.debug("Enhanced words saved for transcription [{}] in [{}]ms", transcription.id(), duration.toMillis());
    }

    private TranscriptionProjection updateStatusAndPublish(UpdateStatusAndPublishCommand command) {
        var updatedTranscription = transcriptionService.patch(
                PatchTranscriptionCommand.builder()
                        .id(command.transcriptionId())
                        .status(command.status())
                        .build()
        );
        broadcaster.publishQuietly(
                TranscriptionUpdateUserEvent.builder()
                        .applicationUserId(command.applicationUserId())
                        .transcriptionId(updatedTranscription.id())
                        .build()
        );

        return updatedTranscription;
    }

    private void deleteUploadedAudioPartitions(List<UploadedAudioPartition> partitions) {
        var resourcesToDelete = ListUtils.emptyIfNull(partitions)
                .stream()
                .map(UploadedAudioPartition::resourceInfo)
                .toList();

        Function<ResourceInfo, Boolean> deleteFunc = r -> cloudStorage.delete(
                CloudDeleteCommand.builder()
                        .resourceName(r.getResourceName())
                        .temp(true)
                        .build()
        );

        TsFunctions.executeAllParallel(resourcesToDelete, deleteFunc);
    }

    @Builder
    private record UploadedAudioPartition(AudioSegment audioSegment, ResourceInfo resourceInfo) {

        public static UploadedAudioPartition of(TranscriptionProjection transcription) {
            Objects.requireNonNull(transcription, "transcription");

            var audioSegment = AudioSegment.builder()
                    .startMillis(0L)
                    .endMillis(transcription.length().toMillis())
                    .build();

            return UploadedAudioPartition.builder()
                    .audioSegment(audioSegment)
                    .resourceInfo(
                            ResourceInfo.builder()
                                    .uri(transcription.cloudUri())
                                    .build()
                    )
                    .build();
        }

    }

    @Builder
    private record UpdateStatusAndPublishCommand(Long transcriptionId,
                                                 Long applicationUserId,
                                                 TranscriptionStatus status) {
    }

    @Builder
    private record TranscodeResult(Path audio, String contentType) {
    }

}
