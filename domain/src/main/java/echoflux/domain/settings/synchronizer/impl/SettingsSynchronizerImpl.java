package echoflux.domain.settings.synchronizer.impl;

import com.google.common.collect.Maps;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.function.Failable;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import echoflux.core.core.bean.MoreBeans;
import echoflux.core.core.initialize.Initialize;
import echoflux.core.core.initialize.InitializeOrder;
import echoflux.core.core.utils.EfFunctions;
import echoflux.core.settings.Settings;
import echoflux.domain.settings.data.SettingsEntity;
import echoflux.domain.settings.data.SettingsProjection;
import echoflux.domain.settings.mapper.SettingsMapper;
import echoflux.domain.settings.schema_processor.SettingsSchemaProcessor;
import echoflux.domain.settings.service.CreateSettingsCommand;
import echoflux.domain.settings.service.SettingsService;
import echoflux.domain.settings.synchronizer.SettingsSynchronizer;

import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class SettingsSynchronizerImpl implements SettingsSynchronizer, Initialize {

    private final SettingsSchemaProcessor schemaProcessor;
    private final SettingsService settingsService;
    private final SettingsMapper settingsMapper;
    private final Map<String, Class<?>> keyBeanTypeMap = newKeyBeanTypeMap();

    @Override
    @Transactional
    public void synchronize() {
        int deleted = delete();
        int updated = update();
        int created = create();

        log.info("Synchronized settings: [{}] created, [{}] updated, [{}] deleted", created, updated, deleted);
    }

    @Override
    @Transactional
    public void initialize() {
        synchronize();
    }

    @Override
    @Transactional
    public SettingsProjection reset(String key) {
        var settings = settingsService.getByKey(key);
        settings.setName(MoreBeans.getDisplayName(keyBeanTypeMap.get(key)));
        settings.setValue(schemaProcessor.create(keyBeanTypeMap.get(key)));

        return settingsMapper.toProjection(settings);
    }

    @Override
    public InitializeOrder getOrder() {
        return InitializeOrder.SETTINGS;
    }

    private int create() {
        var existingKeys = settingsService.getAllByKeys(keyBeanTypeMap.keySet())
                .stream()
                .map(SettingsEntity::getKey)
                .collect(Collectors.toUnmodifiableSet());

        var toCreate = Maps.filterEntries(keyBeanTypeMap, e -> !existingKeys.contains(e.getKey()))
                .entrySet()
                .stream()
                .map(e -> CreateSettingsCommand.builder()
                        .name(MoreBeans.getDisplayName(e.getValue()))
                        .key(e.getKey())
                        .value(schemaProcessor.create(e.getValue()))
                        .build())
                .toList();

        var createdSettings = settingsService.createAll(toCreate);

        return createdSettings.size();
    }

    private int update() {
        var existingEntities = settingsService.getAllByKeys(keyBeanTypeMap.keySet());

        int updated = 0;
        for (var entity : existingEntities) {
            var dbName = entity.getName();
            var codeName = MoreBeans.getDisplayName(keyBeanTypeMap.get(entity.getKey()));

            var dbValue = entity.getValue();
            var mergedValue = schemaProcessor.adaptToSchema(keyBeanTypeMap.get(entity.getKey()), dbValue);

            if (!StringUtils.equals(dbName, codeName) || !Objects.equals(dbValue, mergedValue)) {
                entity.setName(codeName);
                entity.setValue(mergedValue);
                updated++;
            }
        }

        return updated;
    }

    private int delete() {
        var entitiesToDelete = settingsService.getAllByKeysExcluding(keyBeanTypeMap.keySet());
        settingsService.deleteAll(entitiesToDelete);

        return entitiesToDelete.size();
    }

    private static Map<String, Class<?>> newKeyBeanTypeMap() {
        var scanner = new ClassPathScanningCandidateComponentProvider(false);
        scanner.addIncludeFilter(new AnnotationTypeFilter(Settings.class));

        var timedFind = EfFunctions.getTimed(() -> scanner.findCandidateComponents("echoflux"));
        var beanDefinitions = timedFind.getResult();
        log.info("Scanned [{}] settings in [{}] ms", beanDefinitions.size(), timedFind.getDuration().toMillis());

        return beanDefinitions.stream()
                .map(b -> Failable.get(() -> Class.forName(b.getBeanClassName())))
                .collect(Collectors.toUnmodifiableMap(
                        beanType -> beanType.getAnnotation(Settings.class).key(),
                        Function.identity()
                ));
    }

}