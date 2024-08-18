package com.example.application.core.audio.transcoder.impl;

import com.example.application.core.audio.ffmpeg.FFmpegManager;
import com.example.application.core.audio.transcoder.AudioTranscoder;
import com.example.application.core.audio.transcoder.TranscodeCommand;
import com.example.application.core.audio.transcoder.temp_file.TranscoderTempDirectory;
import lombok.SneakyThrows;
import net.bramp.ffmpeg.builder.FFmpegBuilder;
import net.bramp.ffmpeg.builder.FFmpegOutputBuilder;
import org.springframework.stereotype.Component;

import java.nio.file.Path;

@Component
public class AudioTranscoderImpl implements AudioTranscoder {

    @SneakyThrows
    public Path transcode(TranscodeCommand command) {
        var fileName = String.format("%s.%s", newFileName(), command.getAudioContainer().getContainer());
        var outputPath = TranscoderTempDirectory.INSTANCE.locationPath().resolve(fileName).toAbsolutePath();

        var outputBuilder = new FFmpegOutputBuilder()
                .setFilename(outputPath.toString())
                .setAudioCodec(command.getAudioContainer().getCodec())
                .setAudioChannels(command.getChannels())
                .setFormat(command.getAudioContainer().getContainer());

        var builder = new FFmpegBuilder()
                .setInput(command.getSource().toAbsolutePath().toString())
                .addOutput(outputBuilder);

        FFmpegManager.ffmpeg().run(builder.build());

        return outputPath;
    }

    @Override
    public String fileNamePrefix() {
        return "transcode";
    }

}
