package transcribe.application.core.notification;

import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;

public final class Notifications {

    public static void success(String message) {
        success(message, Notification.Position.BOTTOM_END, 3000);
    }

    public static void success(String message, Notification.Position position, int durationMillis) {
        newNotification(message, position, durationMillis, NotificationVariant.LUMO_SUCCESS);
    }

    public static void newNotification(String message,
                                       Notification.Position position,
                                       int durationMillis,
                                       NotificationVariant variant) {
        var notification = new Notification(message, durationMillis);
        notification.addThemeVariants(variant);
        notification.setPosition(position);
        notification.open();
    }

}
