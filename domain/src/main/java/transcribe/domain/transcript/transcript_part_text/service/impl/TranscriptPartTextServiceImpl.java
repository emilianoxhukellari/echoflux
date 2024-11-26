package transcribe.domain.transcript.transcript_part_text.service.impl;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import transcribe.domain.transcript.transcript_part_text.data.TranscriptPartTextEntity;
import transcribe.domain.transcript.transcript_part_text.data.TranscriptPartTextRepository;
import transcribe.domain.transcript.transcript_part_text.mapper.TranscriptPartTextMapper;
import transcribe.domain.transcript.transcript_part_text.service.AddTranscriptPartTextCommand;
import transcribe.domain.transcript.transcript_part_text.service.TranscriptPartTextService;

@Service
@RequiredArgsConstructor
public class TranscriptPartTextServiceImpl implements TranscriptPartTextService {

    private final TranscriptPartTextRepository repository;
    private final TranscriptPartTextMapper mapper;

    @Transactional
    @Override
    public TranscriptPartTextEntity add(AddTranscriptPartTextCommand command) {
        var lastVersion = repository.findFirstByTranscriptPartIdOrderByVersionDesc(command.getTranscriptPartId())
                .map(TranscriptPartTextEntity::getVersion)
                .orElse(-1);

        var entity = mapper.toEntity(command, lastVersion + 1);

        return repository.saveAndFlush(entity);
    }

}
