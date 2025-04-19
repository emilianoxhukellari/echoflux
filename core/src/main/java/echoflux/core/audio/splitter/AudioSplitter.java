package echoflux.core.audio.splitter;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.validation.annotation.Validated;

import java.util.List;

@Validated
public interface AudioSplitter {

    List<AudioPartition> split(@Valid @NotNull SplitAudioCommand command);

    AudioPartition copy(@Valid @NotNull CopyAudioCommand command);

}