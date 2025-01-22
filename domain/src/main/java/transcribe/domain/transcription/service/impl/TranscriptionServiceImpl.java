package transcribe.domain.transcription.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import transcribe.domain.transcription.data.TranscriptionEntity;
import transcribe.domain.transcription.data.TranscriptionRepository;
import transcribe.domain.transcription.service.*;
import transcribe.domain.transcription.mapper.TranscriptionMapper;

import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class TranscriptionServiceImpl implements TranscriptionService {

    private final TranscriptionRepository repository;
    private final TranscriptionMapper mapper;

    @Override
    @Transactional(readOnly = true)
    public TranscriptionEntity get(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Transcription not found"));
    }

    @Override
    @Transactional
    public TranscriptionEntity create(CreateTranscriptionCommand command) {
        var entity = mapper.toEntity(command);

        return repository.save(entity);
    }

    @Override
    @Transactional
    public TranscriptionEntity patch(PatchTranscriptionCommand command) {
        var entity = repository.getReferenceById(command.getId());
        var patched = mapper.patch(entity, command);

        return repository.save(patched);
    }

    @Override
    @Transactional
    public TranscriptionEntity rename(RenameTranscriptionCommand command) {
        return patch(
                PatchTranscriptionCommand.builder()
                        .id(command.getId())
                        .name(command.getName())
                        .build()
        );
    }

}
