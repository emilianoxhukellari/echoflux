package transcribe.core.diarization.pyannote.audio_diarizer;

import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.Validate;
import org.springframework.stereotype.Component;
import transcribe.core.core.log.LoggedMethodExecution;
import transcribe.core.core.utils.MoreFunctions;
import transcribe.core.diarization.AudioDiarizer;
import transcribe.core.diarization.DiarizationEntry;
import transcribe.core.diarization.SpeakerNameGenerator;
import transcribe.core.diarization.pyannote.client.DiarizationJobStatus;
import transcribe.core.diarization.pyannote.client.PyannoteDiarizationClient;
import transcribe.core.diarization.pyannote.client.SubmitJobRequest;

import java.net.URI;
import java.time.Duration;
import java.util.List;

@Component
@RequiredArgsConstructor
public class PyannoteAudioDiarizer implements AudioDiarizer {

    private final PyannoteDiarizationClient client;

    @LoggedMethodExecution(logReturn = false)
    @Override
    public List<DiarizationEntry> diarize(URI audioUri) {
        var request = new SubmitJobRequest(audioUri.toString());
        var job = client.submitJob(request);

        var response = MoreFunctions.pollUntil(
                () -> client.getJob(job.jobId()),
                r -> r.status().isCompleted(),
                Duration.ofSeconds(15),
                Duration.ofMinutes(30)
        );

        Validate.isTrue(
                DiarizationJobStatus.SUCCEEDED.equals(response.status()),
                "Diarization did not succeed. Status: %s", response.status()
        );

        var speakerNameGenerator = new SpeakerNameGenerator();

        return response.output()
                .diarization()
                .stream()
                .map(e ->
                        DiarizationEntry.builder()
                                .speakerName(speakerNameGenerator.generate(e.speaker()))
                                .startOffsetMillis((long) (e.start() * 1000))
                                .endOffsetMillis((long) (e.end() * 1000))
                                .build()
                )
                .toList();
    }

}
