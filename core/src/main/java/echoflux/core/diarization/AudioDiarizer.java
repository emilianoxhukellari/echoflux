package echoflux.core.diarization;

import jakarta.validation.constraints.NotNull;
import org.springframework.validation.annotation.Validated;

import java.net.URL;
import java.util.List;

@Validated
public interface AudioDiarizer {

    List<DiarizationEntry> diarize(@NotNull URL url);

}
