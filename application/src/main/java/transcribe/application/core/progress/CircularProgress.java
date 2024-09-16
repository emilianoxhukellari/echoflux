package transcribe.application.core.progress;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HasSize;
import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.dependency.JsModule;

@Tag("md-circular-progress")
@JsModule("@material/web/progress/circular-progress.js")
public class CircularProgress extends Component implements HasSize {

    public CircularProgress() {
        setIndeterminate(true);
        getElement().getStyle().set("--md-circular-progress-active-indicator-color", "var(--lumo-primary-color)");
        setSize("30px");
    }

    public void setProgress(double value) {
        if (value < 0 || value > 1) {
            throw new IllegalArgumentException("Progress value must be between 0 and 1.");
        }
        setIndeterminate(false);
        getElement().setProperty("progress", value);
    }

    public void setIndeterminate(boolean indeterminate) {
        getElement().setProperty("indeterminate", indeterminate);
    }

    public void setSize(String size) {
        getElement().getStyle().set("--md-circular-progress-size", size);
        setMinHeight(size);
        setMinWidth(size);
    }

    public void setIndicatorWidth(String size) {
        getElement().getStyle().set("--md-circular-progress-active-indicator-width", size);
    }

    public void setMax(int max) {
        getElement().setProperty("max", max);
    }

}
