package transcribe.domain.operation.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import transcribe.domain.operation.data.OperationEntity;
import transcribe.domain.operation.data.OperationProjection;
import transcribe.domain.operation.data.OperationRepository;
import transcribe.domain.operation.data.OperationStatus;
import transcribe.domain.operation.data.OperationType;
import transcribe.domain.operation.mapper.OperationMapper;
import transcribe.domain.operation.service.OperationService;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class OperationServiceImpl implements OperationService {

    private final OperationRepository operationRepository;
    private final OperationMapper operationMapper;

    @Override
    @Transactional
    public OperationProjection createRunning(String name, String description, OperationType type) {
        var entity = OperationEntity.builder()
                .name(name)
                .description(description)
                .type(type)
                .status(OperationStatus.RUNNING)
                .startedAt(LocalDateTime.now())
                .build();
        var saved = operationRepository.save(entity);

        return operationMapper.toProjection(saved);
    }

    @Override
    @Transactional
    public OperationProjection updateSuccess(Long id) {
        var operation = operationRepository.getReferenceById(id);
        operation.setStatus(OperationStatus.SUCCESS);
        operation.setEndedAt(LocalDateTime.now());
        var saved = operationRepository.save(operation);

        return operationMapper.toProjection(saved);
    }

    @Override
    @Transactional
    public OperationProjection updateFailure(Long id, Throwable error) {
        var operation = operationRepository.getReferenceById(id);
        operation.setStatus(OperationStatus.FAILURE);
        operation.setEndedAt(LocalDateTime.now());
        var saved = operationRepository.save(operation);

        return operationMapper.toProjection(saved);
    }

}
