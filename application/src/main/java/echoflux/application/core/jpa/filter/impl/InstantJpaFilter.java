package echoflux.application.core.jpa.filter.impl;

import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.datepicker.DatePickerVariant;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import echoflux.application.security.AuthenticatedUser;

import java.time.Instant;
import java.time.LocalTime;
import java.time.ZoneId;

public class InstantJpaFilter<E> extends BetweenJpaFilter<E, Instant> {

    private final DatePicker from;
    private final DatePicker to;
    private final ZoneId zoneId;

    public InstantJpaFilter(String property, boolean asCollection) {
        super(property, asCollection);

        this.zoneId = AuthenticatedUser.getZoneId();

        this.from = new DatePicker();
        from.addThemeVariants(DatePickerVariant.LUMO_SMALL);
        from.setPlaceholder("From date");
        from.setClearButtonVisible(true);
        from.setWidthFull();

        this.to = new DatePicker();
        to.addThemeVariants(DatePickerVariant.LUMO_SMALL);
        to.setPlaceholder("To date");
        to.setClearButtonVisible(true);
        to.setWidthFull();

        var layout = new VerticalLayout(from, to);
        layout.setPadding(false);
        layout.setSpacing(false);

        addAndExpand(layout);
    }

    @Override
    protected Instant getFrom() {
        if (from.getValue() == null) {
            return null;
        }

        return from
                .getValue()
                .atStartOfDay(zoneId)
                .toInstant();
    }

    @Override
    protected Instant getTo() {
        if (to.getValue() == null) {
            return null;
        }

        return to
                .getValue()
                .atTime(LocalTime.MAX)
                .atZone(zoneId)
                .toInstant();
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
