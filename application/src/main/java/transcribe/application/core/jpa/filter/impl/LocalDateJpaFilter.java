package transcribe.application.core.jpa.filter.impl;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.datepicker.DatePickerVariant;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;

import java.time.LocalDate;

public class LocalDateJpaFilter<T> extends BetweenJpaFilter<T, LocalDate> {

    private final VerticalLayout layout;
    private final DatePicker from;
    private final DatePicker to;

    public LocalDateJpaFilter(String property, boolean asCollection) {
        super(property, asCollection);

        this.from = new DatePicker();
        from.addThemeVariants(DatePickerVariant.LUMO_SMALL);
        from.setPlaceholder("From date");
        from.addValueChangeListener(_ -> listener.run());
        from.setClearButtonVisible(true);

        this.to = new DatePicker();
        to.addThemeVariants(DatePickerVariant.LUMO_SMALL);
        to.setPlaceholder("To date");
        to.addValueChangeListener(_ -> listener.run());
        to.setClearButtonVisible(true);

        this.layout = new VerticalLayout(from, to);
        layout.setPadding(false);
        layout.setSpacing(false);
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
    public Component getComponent() {
        return layout;
    }

}
