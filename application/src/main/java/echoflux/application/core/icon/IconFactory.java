package echoflux.application.core.icon;

import com.vaadin.flow.component.icon.AbstractIcon;

import java.util.function.Supplier;

public final class IconFactory {

    public static <T extends AbstractIcon<T>> AbstractIcon<T> newIcon(Supplier<AbstractIcon<T>> supplier,
                                                                      String color,
                                                                      String size,
                                                                      String tooltip) {
        var icon = supplier.get();
        icon.setSize(size);
        icon.setColor(color);
        icon.setTooltipText(tooltip);

        return icon;
    }

}
