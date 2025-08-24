package echoflux.application.core.jooq.grid;

import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.grid.HeaderRow;
import com.vaadin.flow.component.grid.contextmenu.GridContextMenu;
import com.vaadin.flow.data.provider.CallbackDataProvider;
import com.vaadin.flow.data.provider.ConfigurableFilterDataProvider;
import com.vaadin.flow.data.provider.Query;
import com.vaadin.flow.data.renderer.Renderer;
import echoflux.application.core.dialog.Dialogs;
import echoflux.application.core.jooq.core.JooqPropertyType;
import echoflux.application.core.jooq.core.VaadinJooqUtils;
import echoflux.application.core.jooq.filter.CombinedJooqFilter;
import echoflux.application.core.jooq.filter.JooqFilter;
import echoflux.application.core.jooq.filter.JooqFilterFactory;
import echoflux.core.core.executor.MoreExecutors;
import echoflux.core.core.validate.guard.Guard;
import echoflux.domain.core.jooq.core.JooqUtils;
import jakarta.annotation.Nullable;
import org.jooq.Condition;
import org.jooq.DSLContext;
import org.jooq.Field;
import org.jooq.OrderField;
import org.jooq.Record;
import org.jooq.Table;
import org.jooq.impl.DSL;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.stream.Stream;

public class JooqGrid<R extends Record, ID> extends Grid<R> {

    private final static String DEFAULT_COLUMN_WIDTH = "14.2rem";

    private final DSLContext ctx;
    private final Table<R> table;
    private final Field<ID> idField;
    private final ConfigurableFilterDataProvider<R, Void, CombinedJooqFilter> filterDataProvider;
    private final List<JooqFilter<?>> filters;

    private Condition baseCondition;
    private OrderField<?>[] defaultOrderBy;
    private boolean clearingFilters;
    @Nullable
    private HeaderRow filterRow;
    @Nullable
    private GridContextMenu<R> contextMenu;

    public JooqGrid(DSLContext ctx, Table<R> table, Field<ID> idField) {
        this.ctx = Guard.notNull(ctx, "ctx");
        this.table = Guard.notNull(table, "table");
        this.idField = Guard.notNull(idField, "idField");

        this.baseCondition = DSL.noCondition();
        this.defaultOrderBy = new OrderField[]{idField.desc()};
        this.filterDataProvider = newDataProvider();
        this.filters = new ArrayList<>();

        getDataCommunicator().enablePushUpdates(MoreExecutors.virtualThreadExecutor());
        setDataProvider(filterDataProvider);
        addThemeVariants(GridVariant.LUMO_COLUMN_BORDERS);
        setMultiSort(true);
    }

    public JooqGridControls<R, ID> withControls() {
        return new JooqGridControls<>(this);
    }

    /**
     * Sets the base condition that will be applied to all queries.
     * This condition is combined with the filters applied to the grid.
     */
    public void setBaseCondition(Condition condition) {
        this.baseCondition = Guard.notNull(condition, "condition");
    }

    /**
     * These fields will be used as default ordering when no sorting is applied.
     */
    public void setDefaultOrderBy(OrderField<?>... fields) {
        Guard.notNull(fields, "fields");

        this.defaultOrderBy = fields;
    }

    public <T> void addDefaultFilter(Field<T> field) {
        var filter = JooqFilterFactory.newFilter(field);

        addFilter(filter);
    }

    public <T> void addFilter(JooqFilter<T> filter) {
        Guard.notNull(filter, "filter");

        filters.add(filter);
        addFilterListener(filter);
        addFilterComponent(filter);
    }

    public <T> JooqGridColumn<R, ID, T> addColumn(Field<T> field) {
        Guard.notNull(field, "field");

        var jooqPropertyType = JooqPropertyType.fromField(field);
        @SuppressWarnings("unchecked")
        var renderer = (Renderer<R>) jooqPropertyType
                .getDefaultRendererFactory()
                .apply(field);

        var column = (JooqGridColumn<R, ID, T>) addColumn(r -> r.get(field));
        column.setField(field);
        column.setHeader(VaadinJooqUtils.toDisplayName(field));
        column.setKey(field.getQualifiedName().toString());
        column.setSortProperty(field.getQualifiedName().toString());
        column.setRenderer(renderer);
        column.setTextAlign(jooqPropertyType.getColumnTextAlign());
        column.setWidth(DEFAULT_COLUMN_WIDTH);
        column.setResizable(true);

        return column;
    }

    public JooqGridColumn<R, ID, ?> addIdColumn() {
        return addColumn(idField);
    }

    public List<JooqGridColumn<R, ID, ?>> addAuditColumns() {
        return Stream.of(
                        "created_at",
                        "created_by",
                        "updated_at",
                        "updated_by"
                )
                .map(f -> JooqUtils.getFieldByUnqualifiedName(table, f))
                .<JooqGridColumn<R, ID, ?>>map(this::addColumn)
                .toList();
    }

    public void clearFilters() {
        clearingFilters = true;
        filters.forEach(JooqFilter::clear);
        clearingFilters = false;
        setDataProviderFilter();
    }

    public void refreshItemById(ID id) {
        var item = ctx.selectFrom(table)
                .where(idField.eq(id))
                .fetchSingle();

        refreshItem(item);
    }

    public void refreshItem(R item) {
        filterDataProvider.refreshItem(item);
    }

    public void refreshAll() {
        getDataProvider().refreshAll();
    }

    public void addContextMenuItemWithDoubleClickListener(String text, Consumer<R> onClick) {
        addContextMenuItem(text, onClick);
        addItemDoubleClickListener(e -> onClick.accept(e.getItem()));
    }

    public void addContextMenuItem(String text, Consumer<R> onClick) {
        Guard.notBlank(text, "text");
        Guard.notNull(onClick, "onClick");

        ensureContextMenu().addItem(text, e -> e.getItem().ifPresent(onClick));
    }

    public void addConfirmContextMenuItem(String text, Consumer<R> onClick) {
        Guard.notNull(onClick, "onClick");

        addContextMenuItem(text, e -> Dialogs.confirm(
                String.format("Perform action [%s]?", text),
                () -> onClick.accept(e)
        ));
    }

    @Override
    protected BiFunction<Renderer<R>, String, Grid.Column<R>> getDefaultColumnFactory() {
        return (renderer, columnId) -> new JooqGridColumn<>(this, columnId, renderer);
    }

    private ConfigurableFilterDataProvider<R, Void, CombinedJooqFilter> newDataProvider() {
        return new CallbackDataProvider<>(this::fetch, this::count, idField::get)
                .withConfigurableFilter();
    }

    private void setDataProviderFilter() {
        filterDataProvider.setFilter(CombinedJooqFilter.of(filters));
    }

    private Stream<R> fetch(Query<R, CombinedJooqFilter> query) {
        var condition = buildCondition(query);
        var orderFields = VaadinJooqUtils.toOrderFields(query, table);
        var orderBy = orderFields.isEmpty()
                ? List.of(defaultOrderBy)
                : orderFields;

        return ctx.selectFrom(table)
                .where(condition)
                .orderBy(orderBy)
                .offset(query.getOffset())
                .limit(query.getLimit())
                .fetch()
                .stream();
    }

    private int count(Query<R, CombinedJooqFilter> query) {
        var condition = buildCondition(query);

        return ctx.fetchCount(table, condition);
    }

    private HeaderRow ensureFilterRow() {
        if (filterRow == null) {
            filterRow = appendHeaderRow();
        }

        return filterRow;
    }

    private GridContextMenu<R> ensureContextMenu() {
        if (contextMenu == null) {
            contextMenu = addContextMenu();
        }

        return contextMenu;
    }

    private <T> void addFilterListener(JooqFilter<T> filter) {
        filter.addValueChangeListener(() -> {
            if (!clearingFilters) {
                setDataProviderFilter();
            }
        });
    }

    private <T> void addFilterComponent(JooqFilter<T> filter) {
        var column = Guard.notNull(
                getColumnByKey(filter.getField().getQualifiedName().toString()),
                "Column not found for property [%s]".formatted(filter.getField().getQualifiedName().toString())
        );

        ensureFilterRow()
                .getCell(column)
                .setComponent(filter);
    }

    private Condition buildCondition(Query<R, CombinedJooqFilter> query) {
        return query.getFilter()
                .orElseGet(CombinedJooqFilter::empty)
                .getCondition()
                .and(baseCondition);
    }

    public static class JooqGridColumn<R extends Record, ID, T> extends Grid.Column<R> {

        private Field<T> field;

        private JooqGridColumn(JooqGrid<R, ID> grid, String columnId, Renderer<R> renderer) {
            Guard.notNull(grid, "grid");
            Guard.notBlank(columnId, "columnId");
            Guard.notNull(renderer, "renderer");

            super(grid, columnId, renderer);
        }

        public JooqGridColumn<R, ID, T> setDefaultFilter() {
            var field = getFieldRequired();
            getJooqGrid().addDefaultFilter(field);

            return this;
        }

        @Override
        public JooqGridColumn<R, ID, T> setSortable(boolean sortable) {
            super.setSortable(sortable);

            return this;
        }

        public Field<T> getFieldRequired() {
            return Guard.notNull(field, "field is not set");
        }

        public JooqGrid<R, ID> getJooqGrid() {
            @SuppressWarnings("unchecked")
            var grid = (JooqGrid<R, ID>) getGrid();

            return grid;
        }

        private JooqGridColumn<R, ID, T> setField(Field<T> field) {
            this.field = Guard.notNull(field, "field");

            return this;
        }

    }

}
