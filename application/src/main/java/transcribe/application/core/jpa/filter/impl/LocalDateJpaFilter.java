package transcribe.application.core.jpa.filter.impl;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.datepicker.DatePickerVariant;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;

import java.time.LocalDate;

public class LocalDateJpaFilter<ENTITY> extends BetweenJpaFilter<ENTITY, LocalDate> {

    private final VerticalLayout layout;
    private final DatePicker from;
    private final DatePicker to;

    public LocalDateJpaFilter(String attribute, String property, boolean asCollection) {
        super(attribute, property, asCollection);

        this.from = new DatePicker();
        from.addThemeVariants(DatePickerVariant.LUMO_SMALL);
        from.setPlaceholder("From date");
        from.setClearButtonVisible(true);

        this.to = new DatePicker();
        to.addThemeVariants(DatePickerVariant.LUMO_SMALL);
        to.setPlaceholder("To date");
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
