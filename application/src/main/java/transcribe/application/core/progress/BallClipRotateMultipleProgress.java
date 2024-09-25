package transcribe.application.core.progress;

import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.html.Div;

@Tag("div")
@CssImport("./styles/progress/la-ball-clip-rotate-multiple.css")
public class BallClipRotateMultipleProgress extends Div {

    public BallClipRotateMultipleProgress() {
        addClassName("la-ball-clip-rotate-multiple");
        getStyle().set("color", "var(--lumo-primary-color)");
        this.getElement().setProperty("innerHTML", """
                    <div></div>
                    <div></div>
                """);
    }

}
