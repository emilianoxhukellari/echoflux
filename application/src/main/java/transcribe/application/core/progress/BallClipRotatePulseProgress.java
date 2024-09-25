package transcribe.application.core.progress;

import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.html.Div;

@Tag("div")
@CssImport("./styles/progress/la-ball-clip-rotate-pulse.css")
public class BallClipRotatePulseProgress extends Div {

    public BallClipRotatePulseProgress() {
        addClassName("la-ball-clip-rotate-pulse");
        getStyle().set("color", "var(--lumo-primary-color)");
        this.getElement().setProperty("innerHTML", """
                    <div></div>
                    <div></div>
                """);
    }

}
