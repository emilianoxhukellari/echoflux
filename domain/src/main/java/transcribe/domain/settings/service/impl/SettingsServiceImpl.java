package transcribe.domain.settings.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import transcribe.domain.settings.data.SettingsEntity;
import transcribe.domain.settings.data.SettingsRepository;
import transcribe.domain.settings.mapper.SettingsMapper;
import transcribe.domain.settings.service.CreateSettingsCommand;
import transcribe.domain.settings.service.SettingsService;
import transcribe.domain.settings.service.UpdateSettingsCommand;

import java.util.Collection;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SettingsServiceImpl implements SettingsService {

    private final SettingsRepository repository;
    private final SettingsMapper mapper;

    @Override
    @Transactional
    public List<SettingsEntity> createAll(Collection<CreateSettingsCommand> commandCollection) {
        var entities = commandCollection.stream()
                .map(mapper::toEntity)
                .toList();

        return repository.saveAllAndFlush(entities);
    }

    @Override
    @Transactional
    public List<SettingsEntity> updateAll(Collection<UpdateSettingsCommand> commandCollection) {
        var commandMap = commandCollection.stream()
                .collect(Collectors.toMap(UpdateSettingsCommand::getId, Function.identity()));

        var current = repository.findAllById(commandMap.keySet());

        var updated = current.stream()
                .map(e -> mapper.asEntity(e, commandMap.get(e.getId())))
                .toList();

        return repository.saveAllAndFlush(updated);
    }

    @Override
    @Transactional
    public SettingsEntity update(UpdateSettingsCommand command) {
        var entity = repository.getReferenceById(command.getId());

        return repository.saveAndFlush(mapper.asEntity(entity, command));
    }

    @Override
    public void deleteAll(Collection<Long> idCollection) {
        repository.deleteAllByIdInBatch(idCollection);
    }

    @Override
    public SettingsEntity get(String key) {
        return repository.findByKey(key)
                .orElseThrow();
    }

    @Override
    public List<SettingsEntity> getAllByKeys(Collection<String> keys) {
        return repository.findAllByKeyIn(keys);
    }

    @Override
    public List<SettingsEntity> getAllByKeysExcluding(Collection<String> keys) {
        return keys.isEmpty()
                ? repository.findAll()
                : repository.findAllByKeyNotIn(keys);
    }

}
