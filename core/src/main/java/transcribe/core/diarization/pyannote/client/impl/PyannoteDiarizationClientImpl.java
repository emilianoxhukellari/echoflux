package transcribe.core.diarization.pyannote.client.impl;

import org.springframework.http.MediaType;
import org.springframework.http.client.JdkClientHttpRequestFactory;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import transcribe.core.core.executor.MoreExecutors;
import transcribe.core.core.log.LoggedMethodExecution;
import transcribe.core.diarization.pyannote.PyannoteProperties;
import transcribe.core.diarization.pyannote.client.GetJobResponse;
import transcribe.core.diarization.pyannote.client.PyannoteDiarizationClient;
import transcribe.core.diarization.pyannote.client.SubmitJobRequest;
import transcribe.core.diarization.pyannote.client.SubmitJobResponse;

import java.net.http.HttpClient;

@Component
public class PyannoteDiarizationClientImpl implements PyannoteDiarizationClient {

    private final RestClient restClient;

    public PyannoteDiarizationClientImpl(PyannoteProperties pyannoteProperties) {
        var httpClient = HttpClient.newBuilder()
                .executor(MoreExecutors.virtualThreadExecutor())
                .build();

        this.restClient = RestClient.builder()
                .requestFactory(new JdkClientHttpRequestFactory(httpClient, MoreExecutors.virtualThreadExecutor()))
                .baseUrl(pyannoteProperties.getBaseUrl())
                .defaultHeader("Authorization", "Bearer %s".formatted(pyannoteProperties.getApiKey()))
                .build();
    }

    @Retryable(
            backoff = @Backoff(delay = 2000, multiplier = 2, maxDelay = 30000, random = true),
            maxAttempts = 10
    )
    @LoggedMethodExecution
    @Override
    public SubmitJobResponse submitJob(SubmitJobRequest request) {
        var body = new SubmitJobRequest(request.url());

        return restClient.post()
                .uri("/diarize")
                .contentType(MediaType.APPLICATION_JSON)
                .body(body)
                .retrieve()
                .body(SubmitJobResponse.class);
    }

    @Retryable(
            backoff = @Backoff(delay = 2000, multiplier = 2, maxDelay = 30000, random = true),
            maxAttempts = 10
    )
    @LoggedMethodExecution(logReturn = false)
    @Override
    public GetJobResponse getJob(String jobId) {
        return restClient.get()
                .uri("/jobs/{jobId}", jobId)
                .retrieve()
                .body(GetJobResponse.class);
    }

}
