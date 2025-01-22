package transcribe.domain.completion.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import transcribe.domain.completion.data.CompletionEntity;
import transcribe.domain.completion.data.CompletionRepository;
import transcribe.domain.completion.mapper.CompletionMapper;
import transcribe.domain.completion.service.CompletionService;
import transcribe.domain.completion.service.CreateCompletionCommand;
import transcribe.domain.completion.service.PatchCompletionCommand;

import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class CompletionServiceImpl implements CompletionService {

    private final CompletionRepository repository;
    private final CompletionMapper mapper;

    @Override
    @Transactional(readOnly = true)
    public CompletionEntity get(Long completionId) {
        return repository.findById(completionId)
                .orElseThrow(() -> new NoSuchElementException("Completion not found"));
    }

    @Override
    @Transactional
    public CompletionEntity create(CreateCompletionCommand command) {
        var entity = mapper.toEntity(command);

        return repository.save(entity);
    }

    @Override
    @Transactional
    public CompletionEntity patch(PatchCompletionCommand command) {
        var entity = repository.getReferenceById(command.getId());
        var patched = mapper.patch(entity, command);

        return repository.save(patched);
    }

}
