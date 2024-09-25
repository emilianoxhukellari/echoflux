package transcribe.application.core.progress;

import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.html.Div;

@Tag("div")
@CssImport("./styles/progress/la-ball-running-dots.css")
public class BallRunningDotsProgress extends Div {

    public BallRunningDotsProgress() {
        addClassName("la-ball-running-dots");
        getStyle().set("color", "var(--lumo-primary-color)");
        this.getElement().setProperty("innerHTML", """
                    <div></div>
                    <div></div>
                    <div></div>
                    <div></div>
                    <div></div>
                """);
    }

}
