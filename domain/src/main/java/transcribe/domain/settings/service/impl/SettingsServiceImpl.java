package transcribe.domain.settings.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import transcribe.domain.settings.data.SettingsEntity;
import transcribe.domain.settings.data.SettingsProjection;
import transcribe.domain.settings.data.SettingsRepository;
import transcribe.domain.settings.mapper.SettingsMapper;
import transcribe.domain.settings.service.CreateSettingsCommand;
import transcribe.domain.settings.service.SettingsService;

import java.util.Collection;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SettingsServiceImpl implements SettingsService {

    private final SettingsRepository settingsRepository;
    private final SettingsMapper settingsMapper;

    @Transactional
    @Override
    public List<SettingsProjection> createAll(Collection<CreateSettingsCommand> commandCollection) {
        var entities = commandCollection.stream()
                .map(settingsMapper::toEntity)
                .toList();
        var savedEntities = settingsRepository.saveAll(entities);

        return settingsMapper.toProjections(savedEntities);
    }

    @Transactional
    @Override
    public void deleteAll(Collection<SettingsEntity> settings) {
        settingsRepository.deleteAll(settings);
    }

    @Override
    public SettingsEntity getByKey(String key) {
        return settingsRepository.findByKey(key)
                .orElseThrow();
    }

    @Override
    public List<SettingsEntity> getAllByKeys(Collection<String> keys) {
        return settingsRepository.findAllByKeyIn(keys);
    }

    @Override
    public List<SettingsEntity> getAllByKeysExcluding(Collection<String> keys) {
        return keys.isEmpty()
                ? settingsRepository.findAll()
                : settingsRepository.findAllByKeyNotIn(keys);
    }

}
