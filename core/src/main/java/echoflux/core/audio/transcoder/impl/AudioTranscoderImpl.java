package echoflux.core.audio.transcoder.impl;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import net.bramp.ffmpeg.builder.FFmpegBuilder;
import net.bramp.ffmpeg.builder.FFmpegOutputBuilder;
import org.springframework.stereotype.Component;
import echoflux.core.audio.ffmpeg.FFmpegWrapper;
import echoflux.core.audio.transcoder.AudioTranscoder;
import echoflux.core.audio.transcoder.TranscodeParameters;
import echoflux.core.audio.transcoder.temp_file.AudioTranscoderTempDirectory;
import echoflux.core.core.log.LoggedMethodExecution;
import echoflux.core.core.temp_file.TempFileNameGenerator;

import java.nio.file.Path;

@Component
@RequiredArgsConstructor
public class AudioTranscoderImpl implements AudioTranscoder, TempFileNameGenerator {

    private final FFmpegWrapper fFmpegWrapper;

    @SneakyThrows
    @LoggedMethodExecution
    public Path transcode(Path source, TranscodeParameters transcodeParameters) {
        var fileName = String.format("%s.%s", newFileName(), transcodeParameters.getAudioContainer().getContainer());
        var outputPath = AudioTranscoderTempDirectory.INSTANCE.locationPath().resolve(fileName).toAbsolutePath();

        var outputBuilder = new FFmpegOutputBuilder()
                .setFilename(outputPath.toString())
                .setAudioCodec(transcodeParameters.getAudioContainer().getCodec())
                .setAudioChannels(transcodeParameters.getChannels())
                .setFormat(transcodeParameters.getAudioContainer().getContainer());

        var builder = new FFmpegBuilder()
                .setInput(source.toAbsolutePath().toString())
                .addOutput(outputBuilder);

        fFmpegWrapper.ffmpeg().run(builder.build());

        return outputPath;
    }

    @Override
    public String fileNamePrefix() {
        return "transcode";
    }

}
