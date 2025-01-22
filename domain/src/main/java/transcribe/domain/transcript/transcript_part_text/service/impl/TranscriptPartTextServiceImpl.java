package transcribe.domain.transcript.transcript_part_text.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import transcribe.domain.transcript.transcript_part_text.data.TranscriptPartTextEntity;
import transcribe.domain.transcript.transcript_part_text.data.TranscriptPartTextRepository;
import transcribe.domain.transcript.transcript_part_text.mapper.TranscriptPartTextMapper;
import transcribe.domain.transcript.transcript_part_text.service.AddTranscriptPartTextCommand;
import transcribe.domain.transcript.transcript_part_text.service.TranscriptPartTextService;

import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class TranscriptPartTextServiceImpl implements TranscriptPartTextService {

    private final TranscriptPartTextRepository repository;
    private final TranscriptPartTextMapper mapper;

    @Transactional
    @Override
    public TranscriptPartTextEntity add(AddTranscriptPartTextCommand command) {
        var latestVersion = repository.findFirstByTranscriptPartIdOrderByVersionDesc(command.getTranscriptPartId())
                .map(TranscriptPartTextEntity::getVersion)
                .orElse(-1);

        var entity = mapper.toEntity(command, latestVersion + 1);

        return repository.save(entity);
    }

    @Override
    public List<TranscriptPartTextEntity> getAllRollbacksForTranscriptPart(Long transcriptPartId) {
        var latest = repository.findFirstByTranscriptPartIdOrderByVersionDesc(transcriptPartId);

        if (latest.isEmpty()) {
            return List.of();
        }

        var all = repository.findAllByTranscriptPartId(transcriptPartId);

        return all.stream()
                .filter(e -> !Objects.equals(e.getVersion(), latest.get().getVersion()))
                .toList();
    }

}
