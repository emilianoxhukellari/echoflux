package transcribe.application.core.jpa.grid;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.grid.HeaderRow;
import com.vaadin.flow.component.grid.contextmenu.GridContextMenu;
import com.vaadin.flow.component.icon.AbstractIcon;
import com.vaadin.flow.data.binder.PropertyDefinition;
import com.vaadin.flow.data.provider.ConfigurableFilterDataProvider;
import com.vaadin.flow.data.provider.DataProvider;
import com.vaadin.flow.data.provider.Query;
import com.vaadin.flow.data.renderer.Renderer;
import com.vaadin.flow.spring.data.VaadinSpringDataHelpers;
import lombok.Getter;
import org.apache.commons.lang3.Validate;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import transcribe.application.core.dialog.Dialogs;
import transcribe.application.core.jpa.core.CoreAttributePropertySet;
import transcribe.application.core.jpa.dialog.save.JpaSaveCorePropertiesDialog;
import transcribe.application.core.jpa.filter.CombinedFilter;
import transcribe.application.core.jpa.filter.FilterFactory;
import transcribe.application.core.jpa.filter.JpaFilter;
import transcribe.application.core.operation.Operation;
import transcribe.application.core.operation.OperationCallable;
import transcribe.application.core.operation.OperationRunner;
import transcribe.application.spring.SpringContext;
import transcribe.core.common.utils.MoreArrays;
import transcribe.core.common.utils.MoreLists;
import transcribe.domain.bean.BeanUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Stream;

public class JpaGrid<T, R extends JpaRepository<T, ?> & JpaSpecificationExecutor<T>> extends Grid<T> {

    private static final int DEFAULT_PAGE_SIZE = 30;

    @Getter
    private final R repository;
    @Getter
    private final Class<T> beanType;
    private final List<JpaFilter<T>> filters = new ArrayList<>();
    private final ConfigurableFilterDataProvider<T, Void, CombinedFilter<T>> filterDataProvider;

    @Getter
    private JpaGridCrudActionsData crudActionsData = JpaGridCrudActionsData.empty();
    private HeaderRow filterRow;
    private GridContextMenu<T> contextMenu;

    public JpaGrid(Class<T> beanType, R repository) {
        super(DEFAULT_PAGE_SIZE);

        this.beanType = Objects.requireNonNull(beanType, "Jpa grid requires a bean type");
        this.repository = Objects.requireNonNull(repository, "Jpa grid requires a JpaSpecificationExecutor");
        this.filterDataProvider = newDataProvider();

        configureBeanType(beanType, false);
        setDataProvider(this.filterDataProvider);
        addThemeVariants(GridVariant.LUMO_COLUMN_BORDERS);
    }

    public void refreshAll() {
        getDataProvider().refreshAll();
    }

    public void addCoreAttributeColumns() {
        addCoreAttributeColumnsExcluding();
    }

    public void addCoreAttributeColumnsExcluding(String... excludedProperties) {
        addColumns(CoreAttributePropertySet.getExcluding(beanType, MoreArrays.toList(excludedProperties)).getPropertyNamesAsArray());
    }

    public void addAuditColumns() {
        addColumns("createdAt", "createdBy", "updatedAt", "updatedBy", "id");
    }

    @Override
    public Column<T> addColumn(String propertyName) {
        var propertyDefinition = getPropertyDefinitionRequired(propertyName);

        return CustomRenderedType.ofAssignableType(propertyDefinition.getType())
                .map(crt -> addColumn(propertyName, crt::getRenderer))
                .orElseGet(() -> super.addColumn(propertyName));
    }

    @Override
    public void addColumns(String... propertyNames) {
        Objects.requireNonNull(propertyNames, "Property names cannot be null");
        Stream.of(propertyNames).forEach(name -> addColumn(name).setWidth("13rem"));
    }

    public Column<T> addColumn(String propertyName, Function<PropertyDefinition<T, ?>, Renderer<T>> rendererFactory) {
        var propertyDefinition = getPropertyDefinitionRequired(propertyName);

        return super.addColumn(propertyName).setRenderer(rendererFactory.apply(propertyDefinition));
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
        addFilters("createdAt", "createdBy", "updatedAt", "updatedBy", "id");
    }

    public void addFilters(String... propertyNames) {
        Objects.requireNonNull(propertyNames, "Property names cannot be null");

        Stream.of(propertyNames).forEach(this::addFilter);
    }

    public void addFilter(String propertyName) {
        Validate.notBlank(propertyName, "Property name cannot be blank");

        addFilter(FilterFactory.newFilter(getPropertyDefinitionRequired(propertyName)));
    }

    public void addFilter(PropertyDefinition<T, ?> propertyDefinition) {
        Validate.notNull(propertyDefinition, "Property definition cannot be null");

        addFilter(FilterFactory.newFilter(propertyDefinition));
    }

    public void addFilter(JpaFilter<T> filter) {
        Objects.requireNonNull(filter, "Filter cannot be null");
        Validate.isTrue(!MoreLists.contains(filters, filter), "Filter already added");

        filters.add(filter);
        addFilterListener(filter);
        addFilterComponent(filter);
        setDataProviderFilter();
    }

    private void addFilterListener(JpaFilter<T> filter) {
        filter.setListener(this::setDataProviderFilter);
    }

    private void addFilterComponent(JpaFilter<T> filter) {
        ensureFilterRow().getCell(getColumnByKey(filter.getProperty())).setComponent(filter.getComponent());
    }

    public void addContextMenuItem(String text, Consumer<T> onClick) {
        Validate.notBlank(text, "Text cannot be blank");
        Validate.notNull(onClick, "OnClick consumer cannot be null");

        ensureContextMenu().addItem(text, e -> e.getItem().ifPresent(onClick));
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
                        .setSaveListener(this::refreshAll)
                        .open()
        );
        addContextMenuItem("Delete", e -> Dialogs.confirm(
                "Are you sure you want to delete this entity?",
                () -> {
                    var operation = Operation.builder()
                            .name("Deleting entity")
                            .description(String.format(
                                    "Entity of type [%s] with ID [%s]",
                                    BeanUtils.getPrettyName(beanType),
                                    BeanUtils.getIdFieldValue(e, beanType)
                            ))
                            .callable(OperationCallable.ofRunnable(() -> repository.delete(e)))
                            .onSuccess(_ -> refreshAll())
                            .build();

                    SpringContext.getBean(OperationRunner.class).run(operation);
                }
        ));

        crudActionsData = JpaGridCrudActionsData.builder()
                .withCrudActions(true)
                .excludedPropertiesList(excludedPropertiesList)
                .build();
    }

    public <I extends AbstractIcon<I>> Column<T> addIconActionColumn(Supplier<AbstractIcon<I>> iconSupplier, Consumer<T> onClick) {
        return addComponentColumn(v -> new Button(iconSupplier.get(), _ -> onClick.accept(v)))
                .setWidth("4.8rem");
    }

    private PropertyDefinition<T, ?> getPropertyDefinitionRequired(String property) {
        return Objects.requireNonNull(getPropertySet()).getProperty(property)
                .orElseThrow();
    }

    private void setDataProviderFilter() {
        filterDataProvider.setFilter(CombinedFilter.of(filters));
    }

    private ConfigurableFilterDataProvider<T, Void, CombinedFilter<T>> newDataProvider() {
        return DataProvider.fromFilteringCallbacks(this::fetch, this::count).withConfigurableFilter();
    }

    private Stream<T> fetch(Query<T, CombinedFilter<T>> query) {
        var specification = query.getFilter().orElse(CombinedFilter.empty()).specification();
        var pageRequest = PageRequest.of(query.getPage(), query.getPageSize(), newSort(query));

        return repository.findAll(specification, pageRequest).stream();
    }

    private int count(Query<T, CombinedFilter<T>> query) {
        var specification = query.getFilter().orElse(CombinedFilter.empty()).specification();

        return Math.toIntExact(repository.count(specification));
    }

    private Sort newSort(Query<T, CombinedFilter<T>> query) {
        var sort = VaadinSpringDataHelpers.toSpringDataSort(query);

        if (sort.isEmpty()) {
            var sortById = BeanUtils.findIdField(beanType)
                    .map(f -> Sort.by(Sort.Direction.DESC, f.getName()));

            if (sortById.isPresent()) {
                return sortById.get();
            }
        }

        return sort;
    }

}
