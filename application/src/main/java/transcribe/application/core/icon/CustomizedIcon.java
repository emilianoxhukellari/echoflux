package transcribe.application.core.icon;

import com.vaadin.flow.component.dependency.JsModule;
import com.vaadin.flow.component.icon.Icon;

@JsModule("./icons/customized-iconset.js")
public enum CustomizedIcon {

    FILTER_SLASH;

    private final String iconName = this.name().toLowerCase().replace("_", "-");

    public Icon create() {
        return new Icon("customized-iconset", iconName);
    }

}
