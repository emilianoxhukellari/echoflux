package transcribe.domain.transcription.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import transcribe.core.settings.SettingsLoader;
import transcribe.domain.transcription.data.TranscriptionEntity;
import transcribe.domain.transcription.data.TranscriptionRepository;
import transcribe.domain.transcription.service.TranscriptionSettings;
import transcribe.domain.transcription.mapper.TranscriptionMapper;
import transcribe.domain.transcription.service.CreateTranscriptionCommand;
import transcribe.domain.transcription.service.TranscriptionService;
import transcribe.domain.transcription.service.UpdateTranscriptionCommand;

@Service
@RequiredArgsConstructor
public class TranscriptionServiceImpl implements TranscriptionService {

    private final TranscriptionRepository repository;
    private final TranscriptionMapper mapper;
    private final SettingsLoader settingsLoader;

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
    public double getRealTimeFactor() {
        var settings = settingsLoader.load(TranscriptionSettings.class);

        return repository.findAverageRealTimeFactor(settings.getAverageRealTimeFactorWindow())
                .orElse(settings.getRealTimeFactorFallback());
    }

}
