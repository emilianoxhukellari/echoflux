package transcribe.core.audio.transcoder.impl;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import net.bramp.ffmpeg.builder.FFmpegBuilder;
import net.bramp.ffmpeg.builder.FFmpegOutputBuilder;
import org.springframework.stereotype.Component;
import transcribe.core.audio.ffmpeg.FFmpegWrapper;
import transcribe.core.audio.transcoder.AudioTranscoder;
import transcribe.core.audio.transcoder.TranscodeParameters;
import transcribe.core.audio.transcoder.temp_file.TranscoderTempDirectory;
import transcribe.core.core.log.LoggedMethodExecution;

import java.nio.file.Path;

@Component
@RequiredArgsConstructor
public class AudioTranscoderImpl implements AudioTranscoder {

    private final FFmpegWrapper fFmpegWrapper;

    @SneakyThrows
    @LoggedMethodExecution
    public Path transcode(Path source, TranscodeParameters parameters) {
        var fileName = String.format("%s.%s", newFileName(), parameters.getAudioContainer().getContainer());
        var outputPath = TranscoderTempDirectory.INSTANCE.locationPath().resolve(fileName).toAbsolutePath();

        var outputBuilder = new FFmpegOutputBuilder()
                .setFilename(outputPath.toString())
                .setAudioCodec(parameters.getAudioContainer().getCodec())
                .setAudioChannels(parameters.getChannels())
                .setFormat(parameters.getAudioContainer().getContainer());

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
