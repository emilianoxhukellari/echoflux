package transcribe.application.core.field.duration;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.popover.Popover;
import com.vaadin.flow.component.popover.PopoverPosition;
import com.vaadin.flow.component.popover.PopoverVariant;
import com.vaadin.flow.component.textfield.IntegerField;
import lombok.Setter;
import transcribe.core.core.utils.TsFunctions;

import java.time.Duration;
import java.util.Objects;
import java.util.function.Consumer;

class DurationFieldPopover extends Popover {

    private final IntegerField daysField;
    private final IntegerField hoursField;
    private final IntegerField minutesField;
    private final IntegerField secondsField;
    private final IntegerField millisField;
    @Setter
    private Consumer<Duration> valueChangedFromClient;

    public DurationFieldPopover() {
        this.daysField = newIntegerField();
        this.hoursField = newIntegerField();
        this.minutesField = newIntegerField();
        this.secondsField = newIntegerField();
        this.millisField = newIntegerField();

        var daysVl = newTimeFragmentLayout(daysField, "D");
        var hoursVl = newTimeFragmentLayout(hoursField, "H");
        var minutesVl = newTimeFragmentLayout(minutesField, "M");
        var secondsVl = newTimeFragmentLayout(secondsField, "S");
        var millisVl = newTimeFragmentLayout(millisField, "MS");

        var popoverContent = new HorizontalLayout(daysVl, hoursVl, minutesVl, secondsVl, millisVl);
        popoverContent.setPadding(false);

        addThemeVariants(PopoverVariant.ARROW);
        setOpenOnClick(true);
        setOpenOnFocus(true);
        setPosition(PopoverPosition.BOTTOM);
        getElement().getStyle().set("--vaadin-popover-offset-top", "10px");
        add(popoverContent);
    }

    public void clear() {
        daysField.setValue(0);
        hoursField.setValue(0);
        minutesField.setValue(0);
        secondsField.setValue(0);
        millisField.setValue(0);
    }

    public void setValue(Duration duration) {
        if (duration == null) {
            clear();
        } else {
            int days = Math.toIntExact(duration.toDaysPart());
            daysField.setValue(days);

            int hours = Math.toIntExact(duration.toHoursPart());
            hoursField.setValue(hours);

            int minutes = Math.toIntExact(duration.toMinutesPart());
            minutesField.setValue(minutes);

            int seconds = Math.toIntExact(duration.toSecondsPart());
            secondsField.setValue(seconds);

            int millis = Math.toIntExact(duration.toMillisPart());
            millisField.setValue(millis);
        }
    }

    private void onTimeUnitChangedFromClient() {
        var days = Objects.requireNonNullElse(daysField.getValue(), 0);
        var hours = Objects.requireNonNullElse(hoursField.getValue(), 0);
        var minutes = Objects.requireNonNullElse(minutesField.getValue(), 0);
        var seconds = Objects.requireNonNullElse(secondsField.getValue(), 0);
        var millis = Objects.requireNonNullElse(millisField.getValue(), 0);

        var duration = Duration.ofDays(days)
                .plusHours(hours)
                .plusMinutes(minutes)
                .plusSeconds(seconds)
                .plusMillis(millis);

        var nonNegativeDuration = duration.isPositive()
                ? duration
                : Duration.ZERO;

        setValue(nonNegativeDuration);
        TsFunctions.consumeIfPresent(valueChangedFromClient, nonNegativeDuration);
    }

    private IntegerField newIntegerField() {
        var field = new IntegerField();
        field.addClassName("column-reverse");
        field.setWidth("48px");
        field.setStep(1);
        field.setStepButtonsVisible(true);
        field.setValue(0);
        field.addValueChangeListener(e -> {
            if (!e.isFromClient()) {
                return;
            }

            onTimeUnitChangedFromClient();
        });

        return field;
    }

    private static VerticalLayout newTimeFragmentLayout(Component field, String label) {
        var layout = new VerticalLayout(
                new Span(label),
                field
        );
        layout.setAlignItems(FlexComponent.Alignment.CENTER);
        layout.setPadding(false);
        layout.setSpacing(false);

        return layout;
    }

}
