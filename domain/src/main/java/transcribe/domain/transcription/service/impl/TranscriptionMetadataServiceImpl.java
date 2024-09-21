package transcribe.domain.transcription.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import transcribe.domain.transcription.data.TranscriptionMetadataEntity;
import transcribe.domain.transcription.data.TranscriptionMetadataRepository;
import transcribe.domain.transcription.mapper.TranscriptionMetadataMapper;
import transcribe.domain.transcription.service.TranscriptionMetadataService;
import transcribe.domain.transcription.service.UpdateTranscriptionMetadataCommand;

@Service
@RequiredArgsConstructor
public class TranscriptionMetadataServiceImpl implements TranscriptionMetadataService {

    private final static int AVERAGE_REAL_TIME_FACTOR_WINDOW = 50_000;
    private final static double REAL_TIME_FACTOR_FALLBACK = 1.0;

    private final TranscriptionMetadataRepository repository;
    private final TranscriptionMetadataMapper mapper;

    @Transactional
    public TranscriptionMetadataEntity update(UpdateTranscriptionMetadataCommand command) {
        var entity = repository.getReferenceById(command.getId());

        return repository.saveAndFlush(mapper.asEntity(entity, command));
    }

    @Transactional(readOnly = true)
    public double getRealTimeFactor() {
        return repository.findAverageRealTimeFactor(AVERAGE_REAL_TIME_FACTOR_WINDOW)
                .orElse(REAL_TIME_FACTOR_FALLBACK);
    }

}
