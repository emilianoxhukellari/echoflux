package echoflux.domain.settings.synchronizer.impl;

import com.google.common.collect.Maps;
import echoflux.domain.jooq.tables.records.SettingsRecord;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.function.Failable;
import org.jooq.DSLContext;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import echoflux.core.core.bean.MoreBeans;
import echoflux.core.core.initialize.Initialize;
import echoflux.core.core.initialize.InitializeOrder;
import echoflux.core.core.utils.MoreFunctions;
import echoflux.core.settings.Settings;
import echoflux.domain.settings.schema_processor.SettingsSchemaProcessor;
import echoflux.domain.settings.synchronizer.SettingsSynchronizer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

import static echoflux.domain.jooq.Tables.SETTINGS;

@Component
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class SettingsSynchronizerImpl implements SettingsSynchronizer, Initialize {

    private final DSLContext ctx;
    private final SettingsSchemaProcessor schemaProcessor;
    private final Map<String, Class<?>> keyBeanTypeMap = newKeyBeanTypeMap();

    @Override
    @Transactional
    public void synchronizeAll() {
        int deleted = delete();
        int updated = update();
        int created = create();

        log.info("Synchronized settings: [{}] created, [{}] updated, [{}] deleted", created, updated, deleted);
    }

    @Override
    @Transactional
    public void initialize() {
        synchronizeAll();
    }

    @Override
    @Transactional
    public Long reset(String key) {
        var settingsRecord = ctx.fetchSingle(SETTINGS, SETTINGS.KEY.eq(key));
        settingsRecord.setName(MoreBeans.getDisplayName(keyBeanTypeMap.get(key)));
        settingsRecord.setValue(schemaProcessor.create(keyBeanTypeMap.get(key)));
        settingsRecord.update();

        return settingsRecord.getId();
    }

    @Override
    public InitializeOrder getOrder() {
        return InitializeOrder.SETTINGS;
    }

    private int create() {
        var existingKeys = ctx.select(SETTINGS.KEY)
                .from(SETTINGS)
                .where(SETTINGS.KEY.in(keyBeanTypeMap.keySet()))
                .fetchInto(String.class);

        var recordsToInsert = new ArrayList<SettingsRecord>();
        for (var entry : Maps.filterEntries(keyBeanTypeMap, e -> !existingKeys.contains(e.getKey())).entrySet()) {
            var record = ctx.newRecord(SETTINGS);
            record.setKey(entry.getKey());
            record.setName(MoreBeans.getDisplayName(entry.getValue()));
            record.setValue(schemaProcessor.create(entry.getValue()));
            recordsToInsert.add(record);
        }

        var result = ctx.batchInsert(recordsToInsert).execute();

        return Arrays.stream(result).sum();
    }

    private int update() {
        var existingRecords = ctx.fetch(SETTINGS, SETTINGS.KEY.in(keyBeanTypeMap.keySet()));

        var settingsRecordsToUpdate = new ArrayList<SettingsRecord>();
        for (var record : existingRecords) {
            var dbName = record.getName();
            var codeName = MoreBeans.getDisplayName(keyBeanTypeMap.get(record.getKey()));

            var dbValue = record.getValue();
            var mergedValue = schemaProcessor.adaptToSchema(keyBeanTypeMap.get(record.getKey()), dbValue);

            if (!StringUtils.equals(dbName, codeName) || !Objects.equals(dbValue, mergedValue)) {
                record.setName(codeName);
                record.setValue(mergedValue);
                settingsRecordsToUpdate.add(record);
            }
        }

        var result = ctx.batchUpdate(settingsRecordsToUpdate).execute();

        return Arrays.stream(result).sum();
    }

    private int delete() {
        return ctx.deleteFrom(SETTINGS)
                .where(SETTINGS.KEY.notIn(keyBeanTypeMap.keySet()))
                .execute();
    }

    private static Map<String, Class<?>> newKeyBeanTypeMap() {
        var scanner = new ClassPathScanningCandidateComponentProvider(false);
        scanner.addIncludeFilter(new AnnotationTypeFilter(Settings.class));

        var timedFind = MoreFunctions.getTimed(() -> scanner.findCandidateComponents("echoflux"));
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