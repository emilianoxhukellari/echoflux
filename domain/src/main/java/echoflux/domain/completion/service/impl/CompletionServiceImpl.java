package echoflux.domain.completion.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import echoflux.domain.completion.data.CompletionEntity;
import echoflux.domain.completion.data.CompletionProjection;
import echoflux.domain.completion.data.CompletionRepository;
import echoflux.domain.completion.mapper.CompletionMapper;
import echoflux.domain.completion.service.CompletionService;
import echoflux.domain.completion.service.CreateCompletionCommand;
import echoflux.domain.completion.service.PatchCompletionCommand;
import echoflux.domain.transcription.service.TranscriptionService;

import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CompletionServiceImpl implements CompletionService {

    private final CompletionRepository completionRepository;
    private final CompletionMapper completionMapper;
    private final TranscriptionService transcriptionService;

    @Override
    @Transactional(readOnly = true)
    public CompletionEntity getById(Long id) {
        return completionRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Completion not found"));
    }

    @Override
    @Transactional
    public CompletionProjection create(CreateCompletionCommand command) {
        var transcription = transcriptionService.getById(command.getTranscriptionId());
        var completion = completionMapper.toEntity(command);
        completion.setTranscription(transcription);

        var saved = completionRepository.save(completion);

        return completionMapper.toProjection(saved);
    }

    @Override
    @Transactional
    public CompletionProjection patch(PatchCompletionCommand command) {
        var completion = completionRepository.getReferenceById(command.getId());
        var patchedCompletion = completionMapper.patch(completion, command);

        var saved = completionRepository.save(patchedCompletion);

        return completionMapper.toProjection(saved);
    }

}
