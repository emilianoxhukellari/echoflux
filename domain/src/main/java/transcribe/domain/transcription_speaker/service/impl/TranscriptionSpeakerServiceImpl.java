package transcribe.domain.transcription_speaker.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import transcribe.domain.transcription_speaker.data.TranscriptionSpeakerEntity;
import transcribe.domain.transcription_speaker.data.TranscriptionSpeakerRepository;
import transcribe.domain.transcription_speaker.mapper.TranscriptionSpeakerMapper;
import transcribe.domain.transcription_speaker.service.CreateTranscriptionSpeakerCommand;
import transcribe.domain.transcription_speaker.service.RenameTranscriptionSpeakerCommand;
import transcribe.domain.transcription_speaker.service.TranscriptionSpeakerService;

import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
@Slf4j
public class TranscriptionSpeakerServiceImpl implements TranscriptionSpeakerService {

    private final TranscriptionSpeakerRepository repository;
    private final TranscriptionSpeakerMapper mapper;

    @Override
    @Transactional(readOnly = true)
    public TranscriptionSpeakerEntity getById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Transcription speaker not found"));
    }

    @Override
    @Transactional
    public TranscriptionSpeakerEntity create(CreateTranscriptionSpeakerCommand command) {
        var entity = mapper.toEntity(command);

        return repository.save(entity);
    }

    @Override
    @Transactional
    public TranscriptionSpeakerEntity rename(RenameTranscriptionSpeakerCommand command) {
        var entity = getById(command.getId());
        entity.setName(command.getName());

        return repository.save(entity);
    }


    @Override
    @Transactional
    public void delete(Long id) {
        repository.deleteById(id);
    }

}
