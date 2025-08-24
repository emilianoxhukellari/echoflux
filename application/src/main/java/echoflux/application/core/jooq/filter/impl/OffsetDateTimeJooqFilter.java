package echoflux.application.core.jooq.filter.impl;

import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.datepicker.DatePickerVariant;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import echoflux.application.core.security.AuthenticatedUser;
import org.jooq.Field;

import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;

public class OffsetDateTimeJooqFilter extends BetweenJooqFilter<OffsetDateTime> {

    private final DatePicker from;
    private final DatePicker to;
    private final ZoneId zoneId;

    public OffsetDateTimeJooqFilter(Field<OffsetDateTime> field) {
        super(field);

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
    protected OffsetDateTime getFrom() {
        if (from.getValue() == null) {
            return null;
        }

        return from.getValue()
                .atStartOfDay(zoneId)
                .toOffsetDateTime();
    }

    @Override
    protected OffsetDateTime getTo() {
        if (to.getValue() == null) {
            return null;
        }

        return to.getValue()
                .atTime(LocalTime.MAX)
                .atZone(zoneId)
                .toOffsetDateTime();
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
