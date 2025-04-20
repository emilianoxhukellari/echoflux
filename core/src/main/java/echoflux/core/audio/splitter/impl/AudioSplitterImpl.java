package echoflux.core.audio.splitter.impl;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import net.bramp.ffmpeg.FFmpegUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.file.PathUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;
import org.springframework.stereotype.Component;
import echoflux.core.audio.ffmpeg.FFmpegWrapper;
import echoflux.core.audio.ffmpeg.FFprobeWrapper;
import echoflux.core.audio.splitter.AudioPartition;
import echoflux.core.audio.splitter.AudioSplitter;
import echoflux.core.audio.splitter.CopyAudioCommand;
import echoflux.core.audio.splitter.AudioSegment;
import echoflux.core.audio.splitter.SplitAudioCommand;
import echoflux.core.audio.splitter.temp_file.AudioSplitterTempDirectory;
import echoflux.core.core.collector.ParallelCollectors;
import echoflux.core.core.log.LoggedMethodExecution;
import echoflux.core.core.supplier.MoreSuppliers;
import echoflux.core.core.temp_file.TempFileNameGenerator;
import echoflux.core.core.utils.EfFiles;

import java.nio.charset.Charset;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

@Slf4j
@Component
@RequiredArgsConstructor
public class AudioSplitterImpl implements AudioSplitter, TempFileNameGenerator {

    private static final Pattern START_SILENCE_PATTERN = Pattern.compile("^lavfi\\.silence_start=([\\d.]+)$");
    private static final Pattern END_SILENCE_PATTERN = Pattern.compile("^lavfi\\.silence_end=([\\d.]+)$");

    private final FFmpegWrapper fFmpegWrapper;
    private final FFprobeWrapper fFprobeWrapper;

    @LoggedMethodExecution
    @Override
    public List<AudioPartition> split(SplitAudioCommand command) {
        var silences = findSilences(command.getAudio(), command.getMinSilenceDuration().toMillis());
        log.debug("Silences: [{}]", silences);
        //todo: this is not finding enough silences

        var audioDuration = fFprobeWrapper.getDuration(command.getAudio());
        log.debug("Audio duration: [{}]", audioDuration);

        var partitions = findOptimalSplits(
                0,
                audioDuration.toMillis(),
                command.getPartitionDuration().toMillis(),
                command.getToleranceDuration().toMillis(),
                command.getMinSilenceDuration().toMillis(),
                silences
        );

        return partitions.stream()
                .map(p -> CopyAudioCommand.builder()
                        .source(command.getAudio())
                        .audioSegment(p)
                        .build()
                )
                .map(c -> MoreSuppliers.of(() -> copy(c)))
                .collect(ParallelCollectors.toList(command.getConcurrency()));
    }

    @SneakyThrows
    @LoggedMethodExecution
    @Override
    public AudioPartition copy(CopyAudioCommand command) {
        var extension = Validate.notBlank(
                PathUtils.getExtension(command.getSource()),
                "Extension not found for source file"
        );
        var fileName = String.format("%s.%s", newFileName(), extension);
        var outputPath = AudioSplitterTempDirectory.INSTANCE.locationPath().resolve(fileName).toAbsolutePath();

        var args = List.of(
                "-v", "error",
                "-i", command.getSource().toAbsolutePath().toString(),
                "-ss", FFmpegUtils.toTimecode(command.getAudioSegment().getStartMillis(), TimeUnit.MILLISECONDS),
                "-to", FFmpegUtils.toTimecode(command.getAudioSegment().getEndMillis(), TimeUnit.MILLISECONDS),
                "-c", "copy",
                outputPath.toString()
        );

        fFmpegWrapper.ffmpeg().run(args);

        return AudioPartition.builder()
                .audio(outputPath)
                .audioSegment(command.getAudioSegment())
                .build();
    }

    @Override
    public String fileNamePrefix() {
        return "split";
    }

    @SneakyThrows
    private List<AudioSegment> findSilences(Path audio, long minSilenceDuration) {
        var minSilenceDurationSeconds = minSilenceDuration / 1000.0;
        var silencesFile = AudioSplitterTempDirectory.INSTANCE
                .locationPath()
                .resolve("%s.txt".formatted(newFileName()))
                .toAbsolutePath();

        var formattedSilencesFileLocation = StringUtils.replaceEach(
                silencesFile.toAbsolutePath().toString(),
                ArrayUtils.toArray(":", "\\"),
                ArrayUtils.toArray("\\\\:", "/")
        );

        var argValue = "silencedetect=noise=-40dB:d=%f,ametadata=print:file=%s"
                .formatted(minSilenceDurationSeconds, formattedSilencesFileLocation);

        var args = List.of(
                "-v", "error",
                "-i", audio.toAbsolutePath().toString(),
                "-af", argValue,
                "-f", "null", "-"
        );

        fFmpegWrapper.ffmpeg().run(args);

        var silences = parseSilences(silencesFile);
        EfFiles.deleteIfExists(silencesFile);

        return silences;
    }

    @SneakyThrows
    private static List<AudioSegment> parseSilences(Path silencesFile) {
        Objects.requireNonNull(silencesFile, "Segments file cannot be null");

        var lines = FileUtils.readLines(silencesFile.toFile(), Charset.defaultCharset());
        var segments = new ArrayList<AudioSegment>();

        Double currentStart = null;

        for (var line : lines) {
            line = StringUtils.trim(line);
            var startMatcher = START_SILENCE_PATTERN.matcher(line);
            var endMatcher = END_SILENCE_PATTERN.matcher(line);

            if (startMatcher.matches()) {
                currentStart = Double.parseDouble(startMatcher.group(1));
            } else if (endMatcher.matches()) {
                if (currentStart == null) {
                    continue;
                }

                var end = Double.parseDouble(endMatcher.group(1));
                var startMillis = (long) (currentStart * 1000);
                var endMillis = (long) (end * 1000);
                segments.add(
                        AudioSegment.builder()
                                .startMillis(startMillis)
                                .endMillis(endMillis)
                                .build()
                );

                currentStart = null;
            }
        }

        return segments;
    }

    private static List<AudioSegment> findOptimalSplits(long startMillis,
                                                        long endMillis,
                                                        long partitionDuration,
                                                        long toleranceDuration,
                                                        long minSilenceDuration,
                                                        List<AudioSegment> silences) {
        var splitSegments = new ArrayList<AudioSegment>();
        long audioSegmentDuration = endMillis - startMillis;

        if (audioSegmentDuration <= partitionDuration + toleranceDuration) {
            log.debug("Audio segment duration [{}] <= partition duration [{}]", audioSegmentDuration, partitionDuration);

            splitSegments.add(
                    AudioSegment.builder()
                            .startMillis(startMillis)
                            .endMillis(endMillis)
                            .build()
            );

            return splitSegments;
        }

        long middleOfAudioSegment = startMillis + audioSegmentDuration / 2;
        long windowStart = Math.max(startMillis, middleOfAudioSegment - toleranceDuration);
        long windowEnd = Math.min(endMillis, middleOfAudioSegment + toleranceDuration);

        var silence = findLongestSilenceInRange(silences, windowStart, windowEnd, minSilenceDuration);

        long splitPoint;

        if (silence.isEmpty()) {
            log.debug("No silence found in range [{} - {}]; splitting at middle of audio segment [{}]",
                    windowStart, windowEnd, middleOfAudioSegment);
            splitPoint = middleOfAudioSegment;
        } else {
            long middleOfSilence = (silence.get().getStartMillis() + silence.get().getEndMillis()) / 2;
            log.debug("Silence found in range [{} - {}]; splitting at middle of silence [{}]",
                    windowStart, windowEnd, middleOfSilence);
            splitPoint = middleOfSilence;
        }

        splitSegments.addAll(
                findOptimalSplits(startMillis, splitPoint, partitionDuration, toleranceDuration, minSilenceDuration, silences)
        );
        splitSegments.addAll(
                findOptimalSplits(splitPoint, endMillis, partitionDuration, toleranceDuration, minSilenceDuration, silences)
        );

        return splitSegments;
    }

    private static Optional<AudioSegment> findLongestSilenceInRange(List<AudioSegment> audioSegments,
                                                                    long rangeStartMillis,
                                                                    long rangeEndMillis,
                                                                    long minSilenceDuration) {
        return audioSegments.stream()
                .filter(s -> s.getStartMillis() >= rangeStartMillis && s.getEndMillis() <= rangeEndMillis)
                .filter(s -> s.getDurationMillis() >= minSilenceDuration)
                .max(Comparator.comparing(AudioSegment::getDurationMillis));
    }

}
