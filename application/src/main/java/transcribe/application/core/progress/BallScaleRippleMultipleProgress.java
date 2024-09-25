package transcribe.application.core.progress;

import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.html.Div;

@Tag("div")
@CssImport("./styles/progress/la-ball-scale-ripple-multiple.css")
public class BallScaleRippleMultipleProgress extends Div {

    public BallScaleRippleMultipleProgress() {
        addClassName("la-ball-scale-ripple-multiple");
        getStyle().set("color", "var(--lumo-primary-color)");
        this.getElement().setProperty("innerHTML", """
                        <div></div>
                        <div></div>
                        <div></div>
                """);
    }

}
