package transcribe.application.core.field.duration;

import com.vaadin.flow.component.shared.ThemeVariant;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum DurationFieldVariant implements ThemeVariant {

    LUMO_SMALL("small");

    private final String variantName;

}
