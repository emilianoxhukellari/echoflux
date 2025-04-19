package echoflux.application.core.component;

import com.vaadin.flow.component.HasComponents;
import com.vaadin.flow.component.html.Anchor;

import java.util.Objects;

public class HelperDownloadAnchor extends Anchor {

    public HelperDownloadAnchor() {
        getElement().setAttribute("download", true);
        setTarget("_blank");
        getStyle().set("display", "none");
    }

    public void click() {
        getElement().callJsFunction("click");
    }

    public static Factory newFactory(HasComponents parent) {
        return new Factory(parent);
    }

    public static class Factory {

        private final HasComponents parent;

        public Factory(HasComponents parent) {
            this.parent = Objects.requireNonNull(parent, "parent cannot be null");
        }

        public HelperDownloadAnchor create() {
            var anchor = new HelperDownloadAnchor();
            parent.add(anchor);

            return anchor;
        }

    }

}
