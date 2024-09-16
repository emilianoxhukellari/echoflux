package transcribe.application.core.notification;

import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;

public final class Notifications {

    public static void success(String message) {
        success(message, Notification.Position.BOTTOM_END, 3000);
    }

    public static void success(String message, Notification.Position position, int durationMillis) {
        var notification = new Notification(message, durationMillis);
        notification.addThemeVariants(NotificationVariant.LUMO_SUCCESS);
        notification.setPosition(position);
        notification.open();
    }

}
