package transcribe.application.core.icon;

import com.vaadin.flow.component.Unit;
import com.vaadin.flow.component.icon.AbstractIcon;

import java.util.function.Supplier;

public final class IconFactory {

    public static <T extends AbstractIcon<T>> AbstractIcon<T> newIcon(Supplier<AbstractIcon<T>> supplier,
                                                                      String color,
                                                                      float size,
                                                                      Unit unit,
                                                                      String tooltip) {
        var icon = supplier.get();
        icon.setSize(toCssSize(size, unit));
        icon.setColor(color);
        icon.setTooltipText(tooltip);

        return icon;
    }

    private static String toCssSize(float size, Unit unit) {
        return size + unit.getSymbol();
    }

}
