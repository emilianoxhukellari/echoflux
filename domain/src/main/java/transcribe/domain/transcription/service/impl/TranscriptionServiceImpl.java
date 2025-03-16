package transcribe.domain.transcription.service.impl;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import transcribe.domain.application_user.data.ApplicationUserRepository;
import transcribe.domain.transcription.data.TranscriptionEntity;
import transcribe.domain.transcription.data.TranscriptionProjection;
import transcribe.domain.transcription.data.TranscriptionRepository;
import transcribe.domain.transcription.mapper.TranscriptionMapper;
import transcribe.domain.transcription.service.CreateTranscriptionCommand;
import transcribe.domain.transcription.service.PatchTranscriptionCommand;
import transcribe.domain.transcription.service.RenameTranscriptionCommand;
import transcribe.domain.transcription.service.TranscriptionService;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TranscriptionServiceImpl implements TranscriptionService {

    private final ApplicationUserRepository applicationUserRepository;
    private final TranscriptionRepository transcriptionRepository;
    private final TranscriptionMapper transcriptionMapper;

    @Override
    public TranscriptionEntity getById(Long id) {
        return transcriptionRepository.getReferenceById(id);
    }

    @Override
    public TranscriptionEntity getByIdFetchWords(Long id) {
        return transcriptionRepository.findByIdFetchWords(id)
                .orElseThrow(() -> new EntityNotFoundException("Transcription with id [%d] not found".formatted(id)));
    }

    @Override
    public TranscriptionProjection projectById(Long id) {
        return transcriptionRepository.findById(id, TranscriptionProjection.class)
                .orElseThrow(() -> new EntityNotFoundException("Transcription with id [%d] not found".formatted(id)));
    }

    @Override
    @Transactional
    public TranscriptionProjection create(CreateTranscriptionCommand command) {
        var applicationUser = applicationUserRepository.getReferenceById(command.getApplicationUserId());
        var transcription = transcriptionMapper.toEntity(command);
        transcription.setApplicationUser(applicationUser);

        var saved = transcriptionRepository.save(transcription);

        return transcriptionMapper.toProjection(saved);
    }

    @Override
    @Transactional
    public TranscriptionProjection patch(PatchTranscriptionCommand command) {
        var transcription = transcriptionRepository.getReferenceById(command.getId());
        var patchedTranscription = transcriptionMapper.patch(transcription, command);

        var saved = transcriptionRepository.save(patchedTranscription);

        return transcriptionMapper.toProjection(saved);
    }

    @Override
    @Transactional
    public TranscriptionProjection rename(RenameTranscriptionCommand command) {
        return patch(
                PatchTranscriptionCommand.builder()
                        .id(command.getId())
                        .name(command.getName())
                        .build()
        );
    }

}
