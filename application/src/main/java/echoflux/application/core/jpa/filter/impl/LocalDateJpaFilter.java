package echoflux.application.core.jpa.filter.impl;

import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.datepicker.DatePickerVariant;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;

import java.time.LocalDate;

public class LocalDateJpaFilter<ENTITY> extends BetweenJpaFilter<ENTITY, LocalDate> {

    private final DatePicker from;
    private final DatePicker to;

    public LocalDateJpaFilter(String attribute, String property, boolean asCollection) {
        super(attribute, property, asCollection);

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
    protected LocalDate getFrom() {
        return from.getValue();
    }

    @Override
    protected LocalDate getTo() {
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
