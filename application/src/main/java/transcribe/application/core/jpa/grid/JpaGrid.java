package transcribe.application.core.jpa.grid;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.grid.HeaderRow;
import com.vaadin.flow.component.grid.contextmenu.GridContextMenu;
import com.vaadin.flow.data.binder.PropertyDefinition;
import com.vaadin.flow.data.provider.CallbackDataProvider;
import com.vaadin.flow.data.provider.ConfigurableFilterDataProvider;
import com.vaadin.flow.data.provider.Query;
import com.vaadin.flow.data.renderer.Renderer;
import com.vaadin.flow.shared.util.SharedUtil;
import com.vaadin.flow.spring.data.VaadinSpringDataHelpers;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import transcribe.application.core.dialog.Dialogs;
import transcribe.application.core.jpa.core.CoreAttributePropertySet;
import transcribe.application.core.jpa.core.JpaSupportedType;
import transcribe.application.core.jpa.dialog.save.JpaSaveCorePropertiesDialog;
import transcribe.application.core.jpa.filter.CombinedFilter;
import transcribe.application.core.jpa.filter.FilterFactory;
import transcribe.application.core.jpa.filter.JpaFilter;
import transcribe.application.core.operation.Operation;
import transcribe.application.core.operation.OperationCallable;
import transcribe.application.core.operation.OperationRunner;
import transcribe.application.core.spring.SpringContext;
import transcribe.core.core.utils.MoreArrays;
import transcribe.core.core.utils.MoreLists;
import transcribe.domain.audit.data.AuditEntity;
import transcribe.core.core.bean.BeanUtils;
import transcribe.domain.operation.data.OperationType;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
public class JpaGrid<T, R extends JpaRepository<T, ?> & JpaSpecificationExecutor<T>> extends Grid<T> {

    private static final int DEFAULT_PAGE_SIZE = 30;
    private static final String COLUMN_WIDTH = "13rem";

    @Getter
    private final R repository;
    @Getter
    private final Class<T> beanType;
    private final Field idField;
    private final List<JpaFilter<T>> filters = new ArrayList<>();
    private final ConfigurableFilterDataProvider<T, Void, CombinedFilter<T>> filterDataProvider;
    private final Specification<T> defaultSpecification;

    private boolean clearingFilters;
    @Getter
    private JpaGridCrudActionsData crudActionsData = JpaGridCrudActionsData.empty();
    private HeaderRow filterRow;
    private GridContextMenu<T> contextMenu;

    public JpaGrid(Class<T> beanType, R repository, Specification<T> defaultSpecification) {
        super(DEFAULT_PAGE_SIZE);

        this.beanType = Objects.requireNonNull(beanType, "Jpa grid requires a bean type");
        this.repository = Objects.requireNonNull(repository, "Jpa grid requires a JpaSpecificationExecutor");
        this.defaultSpecification = Objects.requireNonNull(defaultSpecification, "Default specification cannot be null");
        this.idField = BeanUtils.getSingleFieldWithAnnotation(beanType, Id.class);
        this.filterDataProvider = newDataProvider();

        configureBeanType(beanType, false);
        setDataProvider(this.filterDataProvider);
        addThemeVariants(GridVariant.LUMO_COLUMN_BORDERS);
    }

    public JpaGrid(Class<T> beanType, R repository) {
        this(beanType, repository, (_, _, criteriaBuilder) -> criteriaBuilder.conjunction());
    }

    public void refreshItem(T item) {
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
                CoreAttributePropertySet.getExcluding(beanType, MoreArrays.toList(excludedProperties))
                        .getPropertyNamesAsArray()
        );
    }

    public void addAuditColumns() {
        addColumns(BeanUtils.getFieldNames(AuditEntity.class).toArray(String[]::new));
    }

    public void addIdColumn() {
        addColumn(idField.getName()).setWidth(COLUMN_WIDTH);
    }

    @Override
    @SuppressWarnings("unchecked")
    public Column<T> addColumn(String propertyName) {
        var propertyDefinition = getPropertyDefinitionRequired(propertyName);

        return JpaSupportedType.ofBeanType(propertyDefinition.getType())
                .findCustomRendererFactory()
                .map(f ->
                        addColumn(propertyName, (Function<PropertyDefinition<T, ?>, Renderer<T>>) (Function<?, ?>) f)
                )
                .orElseGet(() ->
                        super.addColumn(propertyName)
                                .setHeader(propertyNameToHumanFriendly(propertyName))
                );
    }

    @Override
    public void addColumns(String... propertyNames) {
        Stream.of(ArrayUtils.nullToEmpty(propertyNames)).forEach(name -> addColumn(name)
                .setWidth(COLUMN_WIDTH));
    }

    public Column<T> addColumn(String propertyName, Function<PropertyDefinition<T, ?>, Renderer<T>> rendererFactory) {
        var propertyDefinition = getPropertyDefinitionRequired(propertyName);

        return super.addColumn(propertyName)
                .setRenderer(rendererFactory.apply(propertyDefinition))
                .setHeader(propertyNameToHumanFriendly(propertyName));
    }

    public void setAllColumnsResizable() {
        getColumns().forEach(column -> column.setResizable(true));
    }

    public void addCoreAttributeFilters() {
        addCoreAttributeFiltersExcluding();
    }

    public void addCoreAttributeFiltersExcluding(String... excludedProperties) {
        CoreAttributePropertySet.getExcluding(beanType, MoreArrays.toList(excludedProperties)).getPropertiesAsList()
                .forEach(this::addFilter);
    }

    public void addAuditFilters() {
        addFilters(BeanUtils.getFieldNames(AuditEntity.class).toArray(String[]::new));
    }

    public void addIdFilter() {
        addFilter(idField.getName());
    }

    public void addFilters(String... propertyNames) {
        Stream.of(ArrayUtils.nullToEmpty(propertyNames)).forEach(this::addFilter);
    }

    public void addFilter(String propertyName) {
        Validate.notBlank(propertyName, "Property name cannot be blank");

        addFilter(FilterFactory.newFilter(getPropertyDefinitionRequired(propertyName)));
    }

    public void addFilter(PropertyDefinition<T, ?> propertyDefinition) {
        Objects.requireNonNull(propertyDefinition, "Property definition cannot be null");

        addFilter(FilterFactory.newFilter(propertyDefinition));
    }

    public void addFilter(JpaFilter<T> filter) {
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

    private void addFilterListener(JpaFilter<T> filter) {
        filter.addValueChangeListener(() -> {
            if (!clearingFilters) {
                setDataProviderFilter();
            }
        });
    }

    private void addFilterComponent(JpaFilter<T> filter) {
        ensureFilterRow().getCell(getColumnByKey(filter.getProperty())).setComponent(filter.getComponent());
    }

    public void addContextMenuItem(String text, Consumer<T> onClick) {
        Validate.notBlank(text, "Text cannot be blank");
        Objects.requireNonNull(onClick, "OnClick consumer cannot be null");

        ensureContextMenu().addItem(text, e -> e.getItem().ifPresent(onClick));
    }

    public void addConfirmedContextMenuItem(String text, Consumer<T> onClick) {
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

    private GridContextMenu<T> ensureContextMenu() {
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
                v -> new JpaSaveCorePropertiesDialog<>(v, beanType, repository, excludedPropertiesList)
                        .setSaveListener(this::refreshItem)
                        .open()
        );
        addItemDoubleClickListener(v -> new JpaSaveCorePropertiesDialog<>(v.getItem(), beanType, repository, excludedPropertiesList)
                .setSaveListener(this::refreshItem)
                .open()
        );
        addConfirmedContextMenuItem("Delete", e -> {
            var operation = Operation.builder()
                    .name("Deleting entity")
                    .description(String.format(
                            "Entity of type [%s] with ID [%s]",
                            BeanUtils.getDisplayName(beanType),
                            BeanUtils.getFieldValue(e, idField)
                    ))
                    .callable(OperationCallable.ofRunnable(() -> repository.delete(e)))
                    .onSuccess(_ -> refreshAll())
                    .type(OperationType.NON_BLOCKING)
                    .build();

            SpringContext.getBean(OperationRunner.class).run(operation, UI.getCurrent());
        });

        crudActionsData = JpaGridCrudActionsData.builder()
                .withCrudActions(true)
                .excludedPropertiesList(excludedPropertiesList)
                .build();
    }

    private PropertyDefinition<T, ?> getPropertyDefinitionRequired(String property) {
        return Objects.requireNonNull(getPropertySet()).getProperty(property)
                .orElseThrow();
    }

    private void setDataProviderFilter() {
        filterDataProvider.setFilter(CombinedFilter.of(filters));
    }

    private ConfigurableFilterDataProvider<T, Void, CombinedFilter<T>> newDataProvider() {
        return new CallbackDataProvider<>(this::fetch, this::count, item -> BeanUtils.getFieldValue(item, idField))
                .withConfigurableFilter();
    }

    private Stream<T> fetch(Query<T, CombinedFilter<T>> query) {
        var specification = toSpecification(query);
        var pageRequest = PageRequest.of(query.getPage(), query.getPageSize(), toSort(query));

        return repository.findAll(specification, pageRequest).stream();
    }

    private int count(Query<T, CombinedFilter<T>> query) {
        var specification = toSpecification(query);

        return Math.toIntExact(repository.count(specification));
    }

    private Specification<T> toSpecification(Query<T, CombinedFilter<T>> query) {
        return query.getFilter().orElse(CombinedFilter.empty()).specification()
                .and(defaultSpecification);
    }

    private Sort toSort(Query<T, CombinedFilter<T>> query) {
        var sort = VaadinSpringDataHelpers.toSpringDataSort(query);

        return sort.isSorted()
                ? sort
                : Sort.by(Sort.Direction.DESC, idField.getName());
    }

    private static String propertyNameToHumanFriendly(String propertyName) {
        Objects.requireNonNull(propertyName, "Property name cannot be null");

        var parts = propertyName.split("\\.");

        return Arrays.stream(parts)
                .map(SharedUtil::camelCaseToHumanFriendly)
                .collect(Collectors.joining(StringUtils.SPACE));
    }

}
