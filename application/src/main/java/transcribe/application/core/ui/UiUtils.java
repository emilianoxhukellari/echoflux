package transcribe.application.core.ui;

import com.vaadin.flow.component.UI;
import jakarta.annotation.Nullable;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.Validate;

import java.util.Objects;

@Slf4j
public final class UiUtils {

    /**
     * If the UI is not attached, or is null, the runnable will not be executed.
     * */
    public static void safeAccess(@Nullable UI ui, Runnable runnable) {
        Objects.requireNonNull(runnable, "runnable must not be null");

        if (ui == null) {
            log.warn("UI is null, skipping runnable execution");
        } else if (!ui.isAttached()) {
            log.warn("UI is not attached, skipping runnable execution");
        } else {
            ui.access(runnable::run);
        }
    }

}
