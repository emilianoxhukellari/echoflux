package transcribe.domain.transcription.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import transcribe.domain.transcription.data.TranscriptionEntity;
import transcribe.domain.transcription.data.TranscriptionRepository;
import transcribe.domain.transcription.mapper.TranscriptionMapper;
import transcribe.domain.transcription.service.CreateTranscriptionCommand;
import transcribe.domain.transcription.service.TranscriptionService;
import transcribe.domain.transcription.service.UpdateTranscriptionCommand;

@Service
@RequiredArgsConstructor
public class TranscriptionServiceImpl implements TranscriptionService {

    private final static int AVERAGE_REAL_TIME_FACTOR_WINDOW = 50_000;
    private final static double REAL_TIME_FACTOR_FALLBACK = 1.0;

    private final TranscriptionRepository repository;
    private final TranscriptionMapper mapper;

    @Override
    @Transactional(readOnly = true)
    public TranscriptionEntity get(Long id) {
        return repository.findById(id).orElseThrow();
    }

    @Override
    @Transactional
    public TranscriptionEntity create(CreateTranscriptionCommand command) {
        var entity = mapper.toEntity(command);

        return repository.saveAndFlush(entity);
    }

    @Override
    @Transactional
    public TranscriptionEntity update(UpdateTranscriptionCommand command) {
        var entity = repository.getReferenceById(command.getId());

        return repository.saveAndFlush(mapper.asEntity(entity, command));
    }

    @Override
    @Transactional(readOnly = true)
    public double getRealTimeFactor() {
        return repository.findAverageRealTimeFactor(AVERAGE_REAL_TIME_FACTOR_WINDOW)
                .orElse(REAL_TIME_FACTOR_FALLBACK);
    }

}
