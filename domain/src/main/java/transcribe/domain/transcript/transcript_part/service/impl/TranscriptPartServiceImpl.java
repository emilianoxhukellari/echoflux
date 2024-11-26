package transcribe.domain.transcript.transcript_part.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import transcribe.domain.transcript.transcript_part.data.TranscriptPartEntity;
import transcribe.domain.transcript.transcript_part.data.TranscriptPartRepository;
import transcribe.domain.transcript.transcript_part.mapper.TranscriptPartMapper;
import transcribe.domain.transcript.transcript_part.service.CreateTranscriptPartCommand;
import transcribe.domain.transcript.transcript_part.service.TranscriptPartService;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TranscriptPartServiceImpl implements TranscriptPartService {

    private final TranscriptPartRepository repository;
    private final TranscriptPartMapper mapper;

    @Override
    public List<TranscriptPartEntity> getAllForTranscription(Long transcriptionId) {
        return repository.findAllByTranscriptionIdOrderBySequence(transcriptionId);
    }

    @Override
    @Transactional
    public TranscriptPartEntity create(CreateTranscriptPartCommand command) {
        var entity = mapper.toEntity(command);

        return repository.saveAndFlush(entity);
    }

}
