package transcribe.core.diarization;

import jakarta.validation.constraints.NotNull;
import org.springframework.validation.annotation.Validated;

import java.net.URI;
import java.util.List;

@Validated
public interface AudioDiarizer {

    /**
     * @param audioUri Publicly accessible URI of an audio file
     * */
    List<DiarizationEntry> diarize(@NotNull URI audioUri);

}
