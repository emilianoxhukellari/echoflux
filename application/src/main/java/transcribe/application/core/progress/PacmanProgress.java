package transcribe.application.core.progress;

import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.html.Div;

@Tag("div")
@CssImport("./styles/progress/la-pacman.css")
public class PacmanProgress extends Div {

    public PacmanProgress() {
        addClassName("la-pacman");
        getStyle().set("color", "var(--lumo-primary-color)");
        this.getElement().setProperty("innerHTML", """
                    <div></div>
                    <div></div>
                    <div></div>
                    <div></div>
                    <div></div>
                    <div></div>
                """);
    }

}
