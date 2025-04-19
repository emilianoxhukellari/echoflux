package echoflux.application.core.operation;

import com.vaadin.flow.component.notification.Notification;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum OperationSuccessImportance {

    HIGH(Notification.Position.TOP_CENTER, 5000),
    NORMAL(Notification.Position.BOTTOM_END, 3000),
    LOW(Notification.Position.BOTTOM_END, 1500);

    private final Notification.Position notificationPosition;
    private final int durationMillis;

}
