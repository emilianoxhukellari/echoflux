package com.example.application.core.audio.transcoder.impl;

import com.example.application.core.audio.ffmpeg.FFmpegManager;
import com.example.application.core.audio.transcoder.AudioTranscoder;
import com.example.application.core.audio.transcoder.TranscodeParameters;
import com.example.application.core.audio.transcoder.temp_file.TranscoderTempDirectory;
import lombok.SneakyThrows;
import net.bramp.ffmpeg.builder.FFmpegBuilder;
import net.bramp.ffmpeg.builder.FFmpegOutputBuilder;
import org.springframework.stereotype.Component;

import java.nio.file.Path;

@Component
public class AudioTranscoderImpl implements AudioTranscoder {

    @SneakyThrows
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

        FFmpegManager.ffmpeg().run(builder.build());

        return outputPath;
    }

    @Override
    public String fileNamePrefix() {
        return "transcode";
    }

}
