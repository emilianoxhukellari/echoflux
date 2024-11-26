package transcribe.domain.settings.synchronizer.impl;

import com.google.common.collect.Maps;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.function.Failable;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import transcribe.core.core.initialize.Initialize;
import transcribe.core.core.initialize.InitializeOrder;
import transcribe.core.function.FunctionUtils;
import transcribe.core.settings.Settings;
import transcribe.core.core.bean.BeanUtils;
import transcribe.domain.settings.data.SettingsEntity;
import transcribe.domain.settings.service.CreateSettingsCommand;
import transcribe.domain.settings.service.PatchSettingsCommand;
import transcribe.domain.settings.service.SettingsService;
import transcribe.domain.settings.synchronizer.SettingsSynchronizer;
import transcribe.domain.settings.schema_processor.SettingsSchemaProcessor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Slf4j
public class SettingsSynchronizerImpl implements SettingsSynchronizer, Initialize {

    private final SettingsSchemaProcessor schemaProcessor;
    private final SettingsService service;
    private final Map<String, Class<?>> keyBeanTypeMap = newKeyBeanTypeMap();

    @Override
    @Transactional
    public void synchronize() {
        FunctionUtils.runTimed(this::delete, d -> log.info("Deleted settings in [{}]ms", d.toMillis()));
        FunctionUtils.runTimed(this::update, d -> log.info("Updated settings in [{}]ms", d.toMillis()));
        FunctionUtils.runTimed(this::create, d -> log.info("Created settings in [{}]ms", d.toMillis()));
    }

    @Override
    @Transactional
    public void initialize() {
        synchronize();
    }

    @Override
    public SettingsEntity reset(String key) {
        var entity = service.get(key);

        var command = PatchSettingsCommand.builder()
                .id(entity.getId())
                .name(BeanUtils.getDisplayName(keyBeanTypeMap.get(key)))
                .value(schemaProcessor.create(keyBeanTypeMap.get(key)))
                .build();

        return service.patch(command);
    }

    @Override
    public InitializeOrder getOrder() {
        return InitializeOrder.SETTINGS;
    }

    private void create() {
        var existing = service.getAllByKeys(keyBeanTypeMap.keySet())
                .stream()
                .map(SettingsEntity::getKey)
                .collect(Collectors.toUnmodifiableSet());

        var toCreate = Maps.filterEntries(keyBeanTypeMap, e -> !existing.contains(e.getKey()))
                .entrySet()
                .stream()
                .map(e -> CreateSettingsCommand.builder()
                        .name(BeanUtils.getDisplayName(e.getValue()))
                        .key(e.getKey())
                        .value(schemaProcessor.create(e.getValue()))
                        .build())
                .toList();

        var entities = service.createAll(toCreate);
        logActionOnEntities("Created", entities);
    }

    private void update() {
        var existingEntities = service.getAllByKeys(keyBeanTypeMap.keySet());

        var commands = new ArrayList<PatchSettingsCommand>();

        for (var entity : existingEntities) {
            var dbName = entity.getName();
            var codeName = BeanUtils.getDisplayName(keyBeanTypeMap.get(entity.getKey()));

            var dbValue = entity.getValue();
            var mergedValue = schemaProcessor.adaptToSchema(keyBeanTypeMap.get(entity.getKey()), dbValue);
            if (!StringUtils.equals(dbName, codeName) || !Objects.equals(dbValue, mergedValue)) {
                commands.add(
                        PatchSettingsCommand.builder()
                                .id(entity.getId())
                                .name(codeName)
                                .value(mergedValue)
                                .build()
                );
            }
        }

        var entities = service.patchAll(commands);
        logActionOnEntities("Updated", entities);
    }

    private void delete() {
        var entitiesToDelete = service.getAllByKeysExcluding(keyBeanTypeMap.keySet());
        var idsToDelete = entitiesToDelete.stream()
                .map(SettingsEntity::getId)
                .toList();

        service.deleteAll(idsToDelete);
        logActionOnEntities("Deleted", entitiesToDelete);
    }

    private static Map<String, Class<?>> newKeyBeanTypeMap() {
        var scanner = new ClassPathScanningCandidateComponentProvider(false);
        scanner.addIncludeFilter(new AnnotationTypeFilter(Settings.class));
        var timedFind = FunctionUtils.getTimed(() -> scanner.findCandidateComponents(""));
        var candidateComponents = timedFind.getResult();
        log.info("Scanned [{}] settings in [{}] ms", candidateComponents.size(), timedFind.getDuration().toMillis());

        return candidateComponents.stream()
                .map(b -> Failable.get(() -> Class.forName(b.getBeanClassName())))
                .collect(Collectors.toUnmodifiableMap(
                        beanType -> beanType.getAnnotation(Settings.class).key(),
                        Function.identity()
                ));
    }

    private static void logActionOnEntities(String action, Collection<SettingsEntity> entities) {
        if (CollectionUtils.isNotEmpty(entities)) {
            log.info("{} settings: [{}]", action, entities.stream().map(SettingsEntity::getName).toList());
        }
    }

}