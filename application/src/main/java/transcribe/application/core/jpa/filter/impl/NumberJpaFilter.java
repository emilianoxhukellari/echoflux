package transcribe.application.core.jpa.filter.impl;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.data.value.ValueChangeMode;

public class NumberJpaFilter<T> extends BetweenJpaFilter<T, Double> {

    private final NumberField from;
    private final NumberField to;
    private final HorizontalLayout layout;

    public NumberJpaFilter(String property, boolean asCollection) {
        super(property, asCollection);

        this.from = new NumberField();
        from.setPlaceholder("From");
        from.setValueChangeMode(ValueChangeMode.EAGER);
        from.addValueChangeListener(_ -> listener.run());
        from.setMinWidth("0");

        this.to = new NumberField();
        to.setPlaceholder("To");
        to.setValueChangeMode(ValueChangeMode.EAGER);
        to.addValueChangeListener(_ -> listener.run());
        to.setMinWidth("0");

        this.layout = new HorizontalLayout(from, to);
        layout.setPadding(false);
        layout.setSpacing(false);
        layout.getThemeList().add("spacing-s");
        layout.setWidth("10.6rem");

        layout.setFlexGrow(1, from, to);
        layout.setFlexShrink(1, from, to);
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
    public Component getComponent() {
        return layout;
    }

}
