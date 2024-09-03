package transcribe.domain.operation.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import transcribe.domain.operation.data.OperationEntity;
import transcribe.domain.operation.data.OperationRepository;
import transcribe.domain.operation.data.OperationStatus;
import transcribe.domain.operation.data.OperationType;
import transcribe.domain.operation.mapper.OperationMapper;
import transcribe.domain.operation.service.OperationService;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Transactional
public class OperationServiceImpl implements OperationService {

    private final OperationRepository repository;
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
    public void updateSuccess(Long id) {
        repository.findById(id)
                .map(e -> mapper.asEntity(e, LocalDateTime.now(), OperationStatus.SUCCESS))
                .map(repository::saveAndFlush)
                .orElseThrow();
    }

    @Override
    public void updateFailure(Long id, Throwable error) {
        repository.findById(id)
                .map(e -> mapper.asEntity(e, LocalDateTime.now(), OperationStatus.FAILURE, error.getMessage()))
                .map(repository::saveAndFlush)
                .orElseThrow();
    }


}
