package echoflux.application.core.jooq.filter.impl;

import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import echoflux.application.core.field.duration.DurationField;
import echoflux.application.core.field.duration.DurationFieldVariant;
import org.jooq.Field;

import java.time.Duration;

public class DurationJooqFilter extends BetweenJooqFilter<Duration> {

    private final DurationField from;
    private final DurationField to;

    public DurationJooqFilter(Field<Duration> field) {
        super(field);

        this.from = new DurationField();
        from.addThemeVariants(DurationFieldVariant.LUMO_SMALL);
        from.setClearButtonVisible(true);
        from.setPlaceholder("From duration");
        from.setWidthFull();

        this.to = new DurationField();
        to.addThemeVariants(DurationFieldVariant.LUMO_SMALL);
        to.setClearButtonVisible(true);
        to.setPlaceholder("To duration");
        to.setWidthFull();

        var layout = new VerticalLayout(from, to);
        layout.setPadding(false);
        layout.setSpacing(false);

        addAndExpand(layout);
    }

    @Override
    protected Duration getFrom() {
        return from.getValue();
    }

    @Override
    protected Duration getTo() {
        return to.getValue();
    }

    @Override
    public void addValueChangeListener(Runnable listener) {
        from.addValueChangeListener(_ -> listener.run());
        to.addValueChangeListener(_ -> listener.run());
    }

    @Override
    public void clear() {
        from.clear();
        to.clear();
    }

}
