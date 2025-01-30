package transcribe.domain.settings.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import transcribe.domain.settings.data.SettingsEntity;
import transcribe.domain.settings.data.SettingsRepository;
import transcribe.domain.settings.mapper.SettingsMapper;
import transcribe.domain.settings.service.CreateSettingsCommand;
import transcribe.domain.settings.service.PatchSettingsCommand;
import transcribe.domain.settings.service.SettingsService;

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

        return repository.saveAll(entities);
    }

    @Override
    @Transactional
    public List<SettingsEntity> patchAll(Collection<PatchSettingsCommand> commandCollection) {
        var commandMap = commandCollection.stream()
                .collect(Collectors.toMap(PatchSettingsCommand::getId, Function.identity()));

        var current = repository.findAllById(commandMap.keySet());

        var updated = current.stream()
                .map(e -> mapper.patch(e, commandMap.get(e.getId())))
                .toList();

        return repository.saveAll(updated);
    }

    @Override
    @Transactional
    public SettingsEntity patch(PatchSettingsCommand command) {
        var entity = repository.getReferenceById(command.getId());

        return repository.save(mapper.patch(entity, command));
    }

    @Override
    public void deleteAll(Collection<Long> idCollection) {
        repository.deleteAllByIdInBatch(idCollection);
    }

    @Override
    public SettingsEntity getByKey(String key) {
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
