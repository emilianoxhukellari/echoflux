package echoflux.domain.transcription.endpoint.impl;

import echoflux.domain.core.security.Endpoint;
import echoflux.domain.jooq.tables.pojos.Transcription;
import echoflux.domain.transcription.endpoint.TranscriptionEndpoint;
import echoflux.domain.transcription.service.RenameTranscriptionCommand;
import echoflux.domain.transcription.service.TranscriptionService;
import lombok.RequiredArgsConstructor;

@Endpoint
@RequiredArgsConstructor
public class TranscriptionEndpointImpl implements TranscriptionEndpoint {

    private final TranscriptionService transcriptionService;

    @Override
    public Transcription getTranscriptionById(Long id) {
        return transcriptionService.getById(id);
    }

    @Override
    public Long renameTranscription(RenameTranscriptionCommand command) {
        return transcriptionService.rename(command);
    }

}
