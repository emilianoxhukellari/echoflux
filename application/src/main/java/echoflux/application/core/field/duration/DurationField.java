package echoflux.application.core.field.duration;

import com.vaadin.flow.component.HasPlaceholder;
import com.vaadin.flow.component.customfield.CustomField;
import com.vaadin.flow.component.customfield.CustomFieldVariant;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.shared.HasClearButton;
import com.vaadin.flow.component.shared.HasThemeVariant;
import com.vaadin.flow.component.textfield.Autocomplete;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.textfield.TextFieldVariant;
import echoflux.core.core.utils.MoreDurations;

import java.time.Duration;
import java.util.Objects;

public class DurationField extends CustomField<Duration>
        implements HasThemeVariant<DurationFieldVariant>, HasClearButton, HasPlaceholder {

    private final TextField durationTextField;
    private final DurationFieldPopover durationFieldPopover;

    public DurationField(String label) {
        this();
        setLabel(label);
    }

    public DurationField() {
        this.durationFieldPopover = new DurationFieldPopover();
        this.durationTextField = new TextField();
        var stopwatchIcon = VaadinIcon.STOPWATCH.create();
        stopwatchIcon.addClassName("custom-field-icon");
        durationTextField.addClassName("clear-button-before-suffix");
        durationTextField.setWidthFull();
        durationTextField.setSuffixComponent(stopwatchIcon);
        durationTextField.setAutocomplete(Autocomplete.OFF);
        durationTextField.addValueChangeListener(e -> {
            if (!e.isFromClient()) {
                return;
            }

            var parsed = MoreDurations.tryParse(e.getValue());

            if (parsed.isEmpty()) {
                durationTextField.clear();
                durationFieldPopover.clear();
            } else {
                var formatted = MoreDurations.format(parsed.get());
                durationTextField.setValue(formatted);
                durationFieldPopover.setValue(parsed.get());
            }
        });

        durationFieldPopover.setTarget(durationTextField);
        durationFieldPopover.setValueChangedFromClient(duration -> {
            var formatted = MoreDurations.format(duration);
            durationTextField.setValue(formatted);
            updateValue();
        });

        add(durationTextField);
    }

    @Override
    protected Duration generateModelValue() {
        var parsed = MoreDurations.tryParse(durationTextField.getValue());

        return parsed.orElse(null);
    }

    @Override
    protected void setPresentationValue(Duration newPresentationValue) {
        if (newPresentationValue == null) {
            durationTextField.clear();
            durationFieldPopover.clear();
        } else {
            var formatted = MoreDurations.format(newPresentationValue);
            durationTextField.setValue(formatted);
            durationFieldPopover.setValue(newPresentationValue);
        }
    }

    @Override
    public void setReadOnly(boolean readOnly) {
        durationTextField.setReadOnly(readOnly);

        if (readOnly) {
            durationFieldPopover.close();
            durationFieldPopover.setTarget(null);
        } else {
            durationFieldPopover.setTarget(durationTextField);
        }
    }

    @Override
    public void setPlaceholder(String placeholder) {
        durationTextField.setPlaceholder(placeholder);
    }

    @Override
    public String getPlaceholder() {
        return durationTextField.getPlaceholder();
    }

    @Override
    public boolean isClearButtonVisible() {
        return durationTextField.isClearButtonVisible();
    }

    @Override
    public void setClearButtonVisible(boolean clearButtonVisible) {
        durationTextField.setClearButtonVisible(clearButtonVisible);
    }

    @Override
    public void addThemeVariants(DurationFieldVariant... variants) {
        Objects.requireNonNull(variants, "variants");

        for (var variant : variants) {
            if (DurationFieldVariant.LUMO_SMALL.equals(variant)) {
                durationTextField.addThemeVariants(TextFieldVariant.LUMO_SMALL);
                addThemeVariants(CustomFieldVariant.LUMO_SMALL);
            }
        }
    }

    @Override
    public void removeThemeVariants(DurationFieldVariant... variants) {
        Objects.requireNonNull(variants, "variants");

        for (var variant : variants) {
            if (DurationFieldVariant.LUMO_SMALL.equals(variant)) {
                durationTextField.removeThemeVariants(TextFieldVariant.LUMO_SMALL);
                removeThemeVariants(CustomFieldVariant.LUMO_SMALL);
            }
        }
    }

}
