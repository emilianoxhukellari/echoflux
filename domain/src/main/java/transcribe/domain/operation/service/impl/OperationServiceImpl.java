package transcribe.domain.operation.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import transcribe.domain.operation.data.OperationEntity;
import transcribe.domain.operation.data.OperationRepositoryJpa;
import transcribe.domain.operation.data.OperationStatus;
import transcribe.domain.operation.data.OperationType;
import transcribe.domain.operation.mapper.OperationMapper;
import transcribe.domain.operation.service.OperationService;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Transactional
public class OperationServiceImpl implements OperationService {

    private final OperationRepositoryJpa repository;
    private final OperationMapper mapper;

    @Override
    public OperationEntity createRunning(String name, String description, OperationType type) {
        var entity = OperationEntity.builder()
                .name(name)
                .description(description)
                .type(type)
                .status(OperationStatus.RUNNING)
                .startedAt(LocalDateTime.now())
                .build();

        return repository.saveAndFlush(entity);
    }

    @Override
    public OperationEntity updateSuccess(Long id) {
        var operation = repository.getReferenceById(id);
        var updated = mapper.asEntity(operation, LocalDateTime.now(), OperationStatus.SUCCESS);

        return repository.saveAndFlush(updated);
    }

    @Override
    public OperationEntity updateFailure(Long id, Throwable error) {
        var operation = repository.getReferenceById(id);
        var updated = mapper.asEntity(operation, LocalDateTime.now(), OperationStatus.FAILURE, error.getMessage());

        return repository.saveAndFlush(updated);
    }

}
