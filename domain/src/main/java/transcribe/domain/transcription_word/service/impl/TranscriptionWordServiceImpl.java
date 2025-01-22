package transcribe.domain.transcription_word.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import transcribe.domain.transcription_word.data.TranscriptionWordEntity;
import transcribe.domain.transcription_word.data.TranscriptionWordRepository;
import transcribe.domain.transcription_word.mapper.TranscriptionWordMapper;
import transcribe.domain.transcription_word.service.CreateTranscriptionWordCommand;
import transcribe.domain.transcription_word.service.PatchTranscriptionWordCommand;
import transcribe.domain.transcription_word.service.TranscriptionWordService;

import java.util.List;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
@Slf4j
public class TranscriptionWordServiceImpl implements TranscriptionWordService {

    private final TranscriptionWordRepository repository;
    private final TranscriptionWordMapper mapper;

    @Override
    @Transactional(readOnly = true)
    public TranscriptionWordEntity get(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Transcription word not found"));
    }

    @Override
    @Transactional
    public TranscriptionWordEntity create(CreateTranscriptionWordCommand command) {
        var entity = mapper.toEntity(command);

        return repository.save(entity);
    }

    @Override
    @Transactional
    public List<TranscriptionWordEntity> createAll(List<CreateTranscriptionWordCommand> commands) {
        var entities = mapper.toEntities(commands);

        return repository.saveAll(entities);
    }

    @Override
    @Transactional
    public TranscriptionWordEntity patch(PatchTranscriptionWordCommand command) {
        var entity = get(command.getId());
        var patched = mapper.patch(entity, command);

        return repository.save(patched);
    }

}
