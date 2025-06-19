package echoflux.domain.completion.service.impl;

import echoflux.domain.completion.data.ScalarCompletionProjection;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import echoflux.domain.completion.data.CompletionRepository;
import echoflux.domain.completion.mapper.CompletionMapper;
import echoflux.domain.completion.service.CompletionService;
import echoflux.domain.completion.service.CreateCompletionCommand;
import echoflux.domain.completion.service.PatchCompletionCommand;
import echoflux.domain.transcription.service.TranscriptionService;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CompletionServiceImpl implements CompletionService {

    private final CompletionRepository completionRepository;
    private final CompletionMapper completionMapper;
    private final TranscriptionService transcriptionService;

    @Override
    @Transactional
    public ScalarCompletionProjection create(CreateCompletionCommand command) {
        var transcription = transcriptionService.getById(command.getTranscriptionId());
        var completion = completionMapper.toEntity(command);
        completion.setTranscription(transcription);

        var saved = completionRepository.save(completion);

        return completionMapper.toProjection(saved);
    }

    @Override
    @Transactional
    public ScalarCompletionProjection patch(PatchCompletionCommand command) {
        var completion = completionRepository.getReferenceById(command.getId());
        var patchedCompletion = completionMapper.patch(completion, command);

        var saved = completionRepository.save(patchedCompletion);

        return completionMapper.toProjection(saved);
    }

}
