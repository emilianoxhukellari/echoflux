package echoflux.application.core.jpa.filter.impl;

import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.component.textfield.TextFieldVariant;
import com.vaadin.flow.data.value.ValueChangeMode;

public class NumberJpaFilter<ENTITY> extends BetweenJpaFilter<ENTITY, Double> {

    private final NumberField from;
    private final NumberField to;

    public NumberJpaFilter(String attribute, String property, boolean asCollection) {
        super(attribute, property, asCollection);

        this.from = new NumberField();
        from.addThemeVariants(TextFieldVariant.LUMO_SMALL);
        from.setPlaceholder("From");
        from.setValueChangeMode(ValueChangeMode.LAZY);
        from.setClearButtonVisible(true);
        from.setWidthFull();

        this.to = new NumberField();
        to.addThemeVariants(TextFieldVariant.LUMO_SMALL);
        to.setPlaceholder("To");
        to.setValueChangeMode(ValueChangeMode.LAZY);
        to.setClearButtonVisible(true);
        to.setWidthFull();

        var layout = new VerticalLayout(from, to);
        layout.setPadding(false);
        layout.setSpacing(false);

        addAndExpand(layout);
    }

    @Override
    protected Double getFrom() {
        return from.getValue();
    }

    @Override
    protected Double getTo() {
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
