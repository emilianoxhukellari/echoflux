package echoflux.application.core.jpa.grid;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.grid.HeaderRow;
import com.vaadin.flow.component.grid.contextmenu.GridContextMenu;
import com.vaadin.flow.data.provider.CallbackDataProvider;
import com.vaadin.flow.data.provider.ConfigurableFilterDataProvider;
import com.vaadin.flow.data.provider.Query;
import com.vaadin.flow.data.renderer.Renderer;
import com.vaadin.flow.spring.data.VaadinSpringDataHelpers;
import jakarta.annotation.Nullable;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import echoflux.application.core.dialog.Dialogs;
import echoflux.application.core.jpa.core.JpaPropertyCache;
import echoflux.application.core.jpa.core.JpaPropertyDefinition;
import echoflux.application.core.jpa.core.JpaPropertyDefinitionUtils;
import echoflux.application.core.jpa.core.JpaSupportedType;
import echoflux.application.core.jpa.dialog.save.JpaSaveCorePropertiesDialog;
import echoflux.application.core.jpa.dto.JpaDtoService;
import echoflux.application.core.jpa.dto.impl.SimpleJpaDtoService;
import echoflux.application.core.jpa.filter.CombinedFilter;
import echoflux.application.core.jpa.filter.FilterFactory;
import echoflux.application.core.jpa.filter.JpaFilter;
import echoflux.application.core.operation.Operation;
import echoflux.application.core.operation.OperationCallable;
import echoflux.application.core.operation.OperationRunner;
import echoflux.core.core.bean.MoreBeans;
import echoflux.core.core.bean.loader.BeanLoader;
import echoflux.core.core.executor.MoreExecutors;
import echoflux.core.core.utils.MoreArrays;
import echoflux.core.core.utils.MoreLists;
import echoflux.domain.operation.data.OperationType;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.stream.Stream;

@Slf4j
public class JpaGrid<DTO, ENTITY, ID> extends Grid<DTO> {

    private static final String COLUMN_WIDTH = "14.2rem";

    @Getter
    private final BeanLoader beanLoader;
    @Getter
    private final JpaDtoService<DTO, ENTITY, ID> service;
    @Getter
    private final Class<DTO> beanType;
    @Nullable
    private final Specification<ENTITY> defaultSpecification;
    private final JpaPropertyDefinition<DTO, ID> idPropertyDefinition;
    @Nullable
    private final String defaultSortProperty;
    private final Sort.Direction defaultSortDirection;
    private final List<JpaFilter<ENTITY>> filters = new ArrayList<>();
    private final ConfigurableFilterDataProvider<DTO, Void, CombinedFilter<ENTITY>> filterDataProvider;

    @Getter
    private JpaGridCrudActionsData crudActionsData = JpaGridCrudActionsData.empty();
    private boolean clearingFilters;
    private HeaderRow filterRow;
    private GridContextMenu<DTO> contextMenu;

    public JpaGrid(Class<DTO> beanType, BeanLoader beanLoader) {
        this(
                JpaGridConfiguration.<DTO, ENTITY, ID>builder()
                        .service(new SimpleJpaDtoService<>(beanType, beanLoader))
                        .beanType(beanType)
                        .beanLoader(beanLoader)
                        .build()
        );
    }

    @SuppressWarnings("unchecked")
    public JpaGrid(JpaGridConfiguration<DTO, ENTITY, ID> configuration) {
        super(30);
        Objects.requireNonNull(configuration.getBeanType(), "Bean type cannot be null");
        Objects.requireNonNull(configuration.getService(), "Service cannot be null");
        Objects.requireNonNull(configuration.getBeanLoader(), "Bean loader cannot be null");

        this.service = configuration.getService();
        this.beanType = configuration.getBeanType();
        this.beanLoader = configuration.getBeanLoader();
        this.defaultSpecification = configuration.getDefaultSpecification();
        this.idPropertyDefinition = (JpaPropertyDefinition<DTO, ID>) JpaPropertyCache.getIdProperty(beanType);
        this.defaultSortProperty = configuration.getDefaultSortAttribute();
        this.defaultSortDirection = Objects.requireNonNullElse(configuration.getDefaultSortDirection(), Sort.Direction.DESC);
        this.filterDataProvider = newDataProvider();

        configureBeanType(beanType, false);
        getDataCommunicator().enablePushUpdates(MoreExecutors.virtualThreadExecutor());
        setDataProvider(this.filterDataProvider);
        addThemeVariants(GridVariant.LUMO_COLUMN_BORDERS);
    }

    public void refreshItem(DTO item) {
        getDataProvider().refreshItem(item);
    }

    public void refreshAll() {
        getDataProvider().refreshAll();
    }

    public void addAllColumns() {
        addCoreAttributeColumns();
        addAuditColumns();
        addIdColumn();
    }

    public void addAllFilters() {
        addCoreAttributeFilters();
        addAuditFilters();
        addIdFilter();
    }

    public void addCoreAttributeColumns() {
        addCoreAttributeColumnsExcluding();
    }

    public void addCoreAttributeColumnsExcluding(String... excludedProperties) {
        addColumns(
                JpaPropertyCache.getCorePropertiesExcluding(beanType, MoreArrays.toList(excludedProperties))
                        .stream()
                        .map(JpaPropertyDefinition::getName)
                        .toArray(String[]::new)
        );
    }

    public void addAuditColumns() {
        JpaPropertyCache.getAuditProperties(beanType)
                .stream()
                .map(JpaPropertyDefinition::getName)
                .forEach(this::addColumn);
    }

    public void addIdColumn() {
        addColumn(idPropertyDefinition.getName());
    }

    @Override
    @SuppressWarnings("unchecked")
    public Column<DTO> addColumn(String propertyName) {
        var propertyDefinition = getPropertyDefinitionRequired(propertyName);

        var jpaType = JpaSupportedType.ofBeanType(propertyDefinition.getType());

        var column = super.addColumn(propertyName)
                .setHeader(JpaPropertyDefinitionUtils.toDisplayName(propertyDefinition))
                .setTextAlign(jpaType.getColumnTextAlign())
                .setWidth(COLUMN_WIDTH);

        jpaType.findCustomRendererFactory()
                .ifPresent(f -> column.setRenderer((Renderer<DTO>) f.apply(propertyDefinition)));

        return column;
    }

    @Override
    public void addColumns(String... propertyNames) {
        Stream.of(ArrayUtils.nullToEmpty(propertyNames)).forEach(this::addColumn);
    }

    public Column<DTO> setColumnWidth(String propertyName, String width) {
        var column = getColumnByKey(propertyName);
        column.setWidth(width);

        return column;
    }

    public void setAllColumnsResizable() {
        getColumns().forEach(column -> column.setResizable(true));
    }

    public void addCoreAttributeFilters() {
        addCoreAttributeFiltersExcluding();
    }

    public void addCoreAttributeFiltersExcluding(String... excludedProperties) {
        JpaPropertyCache.getCorePropertiesExcluding(beanType, MoreArrays.toList(excludedProperties))
                .forEach(this::addFilter);
    }

    public void addAuditFilters() {
        JpaPropertyCache.getAuditProperties(beanType)
                .forEach(this::addFilter);
    }

    public void addIdFilter() {
        addFilter(idPropertyDefinition.getName());
    }

    public void addFilters(String... propertyNames) {
        Stream.of(ArrayUtils.nullToEmpty(propertyNames)).forEach(this::addFilter);
    }

    public JpaFilter<ENTITY> addFilter(String propertyName) {
        Validate.notBlank(propertyName, "Property name cannot be blank");

        var propertyDefinition = getPropertyDefinitionRequired(propertyName);
        JpaFilter<ENTITY> filter = FilterFactory.newFilter(propertyDefinition);
        addFilter(filter);

        return filter;
    }

    public JpaFilter<ENTITY> addFilter(JpaPropertyDefinition<DTO, ?> propertyDefinition) {
        Objects.requireNonNull(propertyDefinition, "Property definition cannot be null");

        JpaFilter<ENTITY> filter = FilterFactory.newFilter(propertyDefinition);
        addFilter(filter);

        return filter;
    }

    public void addFilter(JpaFilter<ENTITY> filter) {
        Objects.requireNonNull(filter, "Filter cannot be null");
        Validate.isTrue(!MoreLists.contains(filters, filter), "Filter already added");

        filters.add(filter);
        addFilterListener(filter);
        addFilterComponent(filter);
    }

    public void clearFilters() {
        clearingFilters = true;
        filters.forEach(JpaFilter::clear);
        clearingFilters = false;
        setDataProviderFilter();
    }

    private void addFilterListener(JpaFilter<ENTITY> filter) {
        filter.addValueChangeListener(() -> {
            if (!clearingFilters) {
                setDataProviderFilter();
            }
        });
    }

    private void addFilterComponent(JpaFilter<ENTITY> filter) {
        var column = getColumnByKey(filter.getProperty());

        ensureFilterRow().getCell(column)
                .setComponent(filter);
    }

    public void addContextMenuItem(String text, Consumer<DTO> onClick) {
        Validate.notBlank(text, "Text cannot be blank");
        Objects.requireNonNull(onClick, "OnClick consumer cannot be null");

        ensureContextMenu().addItem(text, e -> e.getItem().ifPresent(onClick));
    }

    public void addConfirmedContextMenuItem(String text, Consumer<DTO> onClick) {
        Objects.requireNonNull(onClick, "OnClick consumer cannot be null");

        addContextMenuItem(text, e -> Dialogs.confirm(
                String.format("Perform action [%s]?", text),
                () -> onClick.accept(e)
        ));
    }

    private HeaderRow ensureFilterRow() {
        if (filterRow == null) {
            filterRow = appendHeaderRow();
        }

        return filterRow;
    }

    private GridContextMenu<DTO> ensureContextMenu() {
        if (contextMenu == null) {
            contextMenu = addContextMenu();
        }

        return contextMenu;
    }

    public void addCrudActions() {
        addCrudActionsExcluding();
    }

    public void addCrudActionsExcluding(String... excludedProperties) {
        var excludedPropertiesList = MoreArrays.toList(excludedProperties);
        addContextMenuItem(
                "Edit",
                v -> new JpaSaveCorePropertiesDialog<>(v, beanType, service, excludedPropertiesList, beanLoader)
                        .setSaveListener(this::refreshItem)
                        .open()
        );
        addItemDoubleClickListener(v ->
                new JpaSaveCorePropertiesDialog<>(v.getItem(), beanType, service, excludedPropertiesList, beanLoader)
                        .setSaveListener(this::refreshItem)
                        .open()
        );
        addConfirmedContextMenuItem("Delete", e -> {
            var operation = Operation.builder()
                    .name("Deleting entity")
                    .description(String.format(
                            "Entity with DTO type [%s] with ID [%s]",
                            MoreBeans.getDisplayName(beanType),
                            idPropertyDefinition.getGetter().apply(e)
                    ))
                    .callable(OperationCallable.ofRunnable(() -> service.delete(e)))
                    .onSuccess(_ -> refreshAll())
                    .type(OperationType.NON_BLOCKING)
                    .build();

            beanLoader.load(OperationRunner.class)
                    .run(operation, UI.getCurrent());
        });

        crudActionsData = JpaGridCrudActionsData.builder()
                .withCrudActions(true)
                .excludedPropertiesList(excludedPropertiesList)
                .build();
    }

    private JpaPropertyDefinition<DTO, ?> getPropertyDefinitionRequired(String property) {
        return JpaPropertyCache.findPropertyByName(beanType, property)
                .orElseThrow(() -> new NoSuchElementException(String.format("Property [%s] not found", property)));
    }

    private void setDataProviderFilter() {
        filterDataProvider.setFilter(CombinedFilter.of(filters));
    }

    private ConfigurableFilterDataProvider<DTO, Void, CombinedFilter<ENTITY>> newDataProvider() {
        return new CallbackDataProvider<>(this::fetch, this::count, item -> idPropertyDefinition.getGetter().apply(item))
                .withConfigurableFilter();
    }

    private Stream<DTO> fetch(Query<DTO, CombinedFilter<ENTITY>> query) {
        var specification = toSpecification(query);
        var pageRequest = PageRequest.of(query.getPage(), query.getPageSize(), toSort(query));

        return service.findAll(specification, pageRequest).stream();
    }

    private int count(Query<DTO, CombinedFilter<ENTITY>> query) {
        var specification = toSpecification(query);

        return Math.toIntExact(service.count(specification));
    }

    private Specification<ENTITY> toSpecification(Query<DTO, CombinedFilter<ENTITY>> query) {
        return query.getFilter()
                .orElse(CombinedFilter.empty())
                .specification()
                .and(defaultSpecification);
    }

    private Sort toSort(Query<DTO, CombinedFilter<ENTITY>> query) {
        var sort = VaadinSpringDataHelpers.toSpringDataSort(query);

        return sort.isSorted()
                ? sort
                : Sort.by(defaultSortDirection, StringUtils.firstNonBlank(defaultSortProperty, idPropertyDefinition.getAttributeName()));
    }

}
