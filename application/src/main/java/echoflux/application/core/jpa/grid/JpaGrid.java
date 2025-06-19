package echoflux.application.core.jpa.grid;

import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.grid.HeaderRow;
import com.vaadin.flow.component.grid.contextmenu.GridContextMenu;
import com.vaadin.flow.data.binder.BeanPropertySet;
import com.vaadin.flow.data.binder.PropertyDefinition;
import com.vaadin.flow.data.binder.PropertyFilterDefinition;
import com.vaadin.flow.data.provider.CallbackDataProvider;
import com.vaadin.flow.data.provider.ConfigurableFilterDataProvider;
import com.vaadin.flow.data.provider.Query;
import com.vaadin.flow.data.renderer.Renderer;
import com.vaadin.flow.spring.data.VaadinSpringDataHelpers;
import echoflux.application.core.dialog.Dialogs;
import echoflux.application.core.jpa.core.JpaPropertyType;
import echoflux.application.core.jpa.core.PropertyDefinitionUtils;
import echoflux.application.core.jpa.filter.CombinedFilter;
import echoflux.application.core.jpa.filter.FilterFactory;
import echoflux.application.core.jpa.filter.JpaFilter;
import echoflux.core.core.executor.MoreExecutors;
import echoflux.core.core.validate.guard.Guard;
import echoflux.domain.core.data.BaseEntity;
import echoflux.domain.core.data.BaseEntity_;
import echoflux.domain.core.data.BaseProjection;
import echoflux.domain.core.data.HasId;
import echoflux.domain.core.repository.ProjectionJpaRepository;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.util.TypeInformation;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class JpaGrid<P extends BaseProjection<ID>, E extends BaseEntity<ID>, ID> extends Grid<P> {

    private final static String DEFAULT_COLUMN_WIDTH = "14.2rem";

    private final Class<P> projectionType;
    private final ProjectionJpaRepository<E, ID> repository;
    private final TypeInformation<P> projectionTypeInformation;
    private final Set<String> attributePaths;
    private final ConfigurableFilterDataProvider<P, Void, CombinedFilter<E>> filterDataProvider;
    private final List<JpaFilter<E>> filters;

    private Specification<E> defaultSpecification;
    private String defaultSortAttribute;
    private Sort.Direction defaultSortDirection;
    private boolean clearingFilters;
    private HeaderRow filterRow;
    private GridContextMenu<P> contextMenu;

    public JpaGrid(Class<P> projectionType, ProjectionJpaRepository<E, ID> repository) {
        this(projectionType, repository, resolveAttributePaths(projectionType));
    }

    public JpaGrid(Class<P> projectionType, ProjectionJpaRepository<E, ID> repository, Set<String> attributePaths) {
        this.projectionType = Guard.notNull(projectionType, "projectionType");
        this.repository = Guard.notNull(repository, "repository");
        this.attributePaths = Guard.notNull(attributePaths, "attributePaths");
        this.defaultSpecification = (_, _, cb) -> cb.conjunction();
        this.defaultSortAttribute = HasId.ID;
        this.defaultSortDirection = Sort.Direction.DESC;
        this.projectionTypeInformation = TypeInformation.of(projectionType);
        this.filterDataProvider = newDataProvider();
        this.filters = new ArrayList<>();

        configureBeanType(projectionType, false);
        getDataCommunicator().enablePushUpdates(MoreExecutors.virtualThreadExecutor());
        setDataProvider(this.filterDataProvider);
        addThemeVariants(GridVariant.LUMO_COLUMN_BORDERS);
    }

    public JpaGridControls<P, E, ID> withControls() {
        return new JpaGridControls<>(this);
    }

    public void setDefaultSpecification(Specification<E> defaultSpecification) {
        this.defaultSpecification = Guard.notNull(defaultSpecification, "defaultSpecification");
    }

    public void setDefaultSortAttribute(String defaultSortAttribute) {
        this.defaultSortAttribute = Guard.notBlank(defaultSortAttribute, "defaultSortAttribute");
    }

    public void setDefaultSortDirection(Sort.Direction defaultSortDirection) {
        this.defaultSortDirection = Guard.notNull(defaultSortDirection, "defaultSortDirection");
    }

    @Override
    public Column<P> addColumn(String attributePath) {
        Guard.notBlank(attributePath, "attributePath");

        var propertyTypeInformation = projectionTypeInformation.getRequiredProperty(attributePath);
        var propertyDefinition = getPropertyDefinitionRequired(attributePath);
        var jpaPropertyType = JpaPropertyType.ofBeanType(propertyTypeInformation.getType());

        @SuppressWarnings("unchecked")
        var renderer = (Renderer<P>) jpaPropertyType
                .getDefaultRendererFactory()
                .apply(propertyDefinition, propertyTypeInformation);

        return super.addColumn(attributePath)
                .setHeader(PropertyDefinitionUtils.toDisplayName(propertyDefinition))
                .setTextAlign(jpaPropertyType.getColumnTextAlign())
                .setRenderer(renderer)
                .setSortable(true)
                .setResizable(true)
                .setWidth(DEFAULT_COLUMN_WIDTH);
    }

    public Column<P> addColumnWithFilter(String attributePath) {
        var column = addColumn(attributePath);
        addFilter(attributePath);

        return column;
    }

    public Column<P> addColumnWithFilter(String... attributePathParts) {
        var attributePath = StringUtils.join(attributePathParts, ".");
        var column = addColumn(attributePath);
        addFilter(attributePath);

        return column;
    }

    public void clearFilters() {
        clearingFilters = true;
        filters.forEach(JpaFilter::clear);
        clearingFilters = false;
        setDataProviderFilter();
    }

    public void refreshItemById(ID id) {
        var item = repository.getProjectedById(id, projectionType, attributePaths);
        refreshItem(item);
    }

    public void refreshItem(P item) {
        getDataProvider().refreshItem(item);
    }

    public void refreshAll() {
        getDataProvider().refreshAll();
    }

    public Column<P> addIdColumnWithFilter() {
        return addColumnWithFilter(HasId.ID);
    }

    public List<Column<P>> addAuditColumnsWithFilter() {
        return List.of(
                addColumnWithFilter(BaseEntity_.CREATED_AT),
                addColumnWithFilter(BaseEntity_.CREATED_BY),
                addColumnWithFilter(BaseEntity_.UPDATED_AT),
                addColumnWithFilter(BaseEntity_.UPDATED_BY)
        );
    }

    public void addContextMenuItemWithDoubleClickListener(String text, Consumer<P> onClick) {
        addContextMenuItem(text, onClick);
        addItemDoubleClickListener(e -> onClick.accept(e.getItem()));
    }

    public void addContextMenuItem(String text, Consumer<P> onClick) {
        Guard.notBlank(text, "text");
        Guard.notNull(onClick, "onClick");

        ensureContextMenu().addItem(text, e -> e.getItem().ifPresent(onClick));
    }

    public void addConfirmContextMenuItem(String text, Consumer<P> onClick) {
        Guard.notNull(onClick, "onClick");

        addContextMenuItem(text, e -> Dialogs.confirm(
                String.format("Perform action [%s]?", text),
                () -> onClick.accept(e)
        ));
    }

    public void addFilter(String attributePath) {
        var propertyTypeInformation = projectionTypeInformation.getRequiredProperty(attributePath);
        var propertyDefinition = getPropertyDefinitionRequired(attributePath);

        JpaFilter<E> filter = FilterFactory.newFilter(
                propertyDefinition.getName(),
                propertyDefinition.getTopLevelName(),
                propertyTypeInformation,
                false
        );

        addFilter(filter);
    }

    public void addFilter(JpaFilter<E> filter) {
        Guard.notNull(filter, "Filter cannot be null");

        filters.add(filter);
        addFilterListener(filter);
        addFilterComponent(filter);
    }

    private void setDataProviderFilter() {
        filterDataProvider.setFilter(CombinedFilter.of(filters));
    }

    private ConfigurableFilterDataProvider<P, Void, CombinedFilter<E>> newDataProvider() {
        return new CallbackDataProvider<>(this::fetch, this::count, HasId::getId)
                .withConfigurableFilter();
    }

    private Stream<P> fetch(Query<P, CombinedFilter<E>> query) {
        var specification = toSpecification(query);
        var pageRequest = PageRequest.of(query.getPage(), query.getPageSize(), toSort(query));

        return repository
                .findAllProjected(specification, pageRequest, projectionType, attributePaths)
                .stream();
    }

    private int count(Query<P, CombinedFilter<E>> query) {
        var specification = toSpecification(query);

        return Math.toIntExact(repository.count(specification));
    }

    private Specification<E> toSpecification(Query<P, CombinedFilter<E>> query) {
        return query
                .getFilter()
                .orElse(CombinedFilter.empty())
                .specification()
                .and(defaultSpecification);
    }

    private Sort toSort(Query<P, CombinedFilter<E>> query) {
        var sort = VaadinSpringDataHelpers.toSpringDataSort(query);

        if (sort.isSorted()) {
            return sort;
        }

        return Sort.by(defaultSortDirection, defaultSortAttribute);
    }

    private HeaderRow ensureFilterRow() {
        if (filterRow == null) {
            filterRow = appendHeaderRow();
        }

        return filterRow;
    }

    private GridContextMenu<P> ensureContextMenu() {
        if (contextMenu == null) {
            contextMenu = addContextMenu();
        }

        return contextMenu;
    }

    private void addFilterListener(JpaFilter<E> filter) {
        filter.addValueChangeListener(() -> {
            if (!clearingFilters) {
                setDataProviderFilter();
            }
        });
    }

    private void addFilterComponent(JpaFilter<E> filter) {
        var column = Guard.notNull(
                getColumnByKey(filter.getProperty()),
                "Column not found for property [%s]".formatted(filter.getProperty())
        );

        ensureFilterRow()
                .getCell(column)
                .setComponent(filter);
    }

    private PropertyDefinition<P, ?> getPropertyDefinitionRequired(String property) {
        return getPropertySet()
                .getProperty(property)
                .orElseThrow(() -> new NoSuchElementException("Property not found: " + property));
    }

    private static Set<String> resolveAttributePaths(Class<?> projectionType) {
        Guard.notNull(projectionType, "projectionType");

        return BeanPropertySet.get(projectionType, true, PropertyFilterDefinition.getDefaultFilter())
                .getProperties()
                .map(PropertyDefinition::getName)
                .collect(Collectors.toUnmodifiableSet());
    }

}
