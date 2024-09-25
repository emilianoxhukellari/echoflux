package transcribe.application.core.progress;

import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.html.Div;

@Tag("div")
@CssImport("./styles/progress/la-line-scale-pulse-out-rapid.css")
public class LineScalePulseOutRapidProgress extends Div {

    public LineScalePulseOutRapidProgress() {
        addClassName("la-line-scale-pulse-out-rapid");
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
