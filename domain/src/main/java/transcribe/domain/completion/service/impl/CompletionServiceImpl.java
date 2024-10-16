package transcribe.domain.completion.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import transcribe.domain.completion.data.CompletionEntity;
import transcribe.domain.completion.data.CompletionRepository;
import transcribe.domain.completion.mapper.CompletionMapper;
import transcribe.domain.completion.service.CompletionService;
import transcribe.domain.completion.service.CreateCompletionCommand;
import transcribe.domain.completion.service.UpdateCompletionCommand;

@Service
@RequiredArgsConstructor
public class CompletionServiceImpl implements CompletionService {

    private final CompletionRepository repository;
    private final CompletionMapper mapper;

    @Override
    @Transactional
    public CompletionEntity create(CreateCompletionCommand command) {
        var entity = mapper.toEntity(command);

        return repository.saveAndFlush(entity);
    }

    @Override
    @Transactional
    public CompletionEntity update(UpdateCompletionCommand command) {
        var entity = repository.getReferenceById(command.getId());

        return repository.saveAndFlush(mapper.asEntity(entity, command));
    }

}
