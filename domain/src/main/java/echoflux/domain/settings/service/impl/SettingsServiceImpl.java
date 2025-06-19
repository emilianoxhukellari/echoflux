package echoflux.domain.settings.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import echoflux.domain.settings.data.SettingsEntity;
import echoflux.domain.settings.data.SettingsRepository;
import echoflux.domain.settings.mapper.SettingsMapper;
import echoflux.domain.settings.service.CreateSettingsCommand;
import echoflux.domain.settings.service.SettingsService;

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
    public List<SettingsEntity> createAll(Collection<CreateSettingsCommand> commandCollection) {
        var entities = commandCollection.stream()
                .map(settingsMapper::toEntity)
                .toList();

        return settingsRepository.saveAll(entities);
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
