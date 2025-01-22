package transcribe.application.core.operation;

import com.vaadin.flow.component.notification.Notification;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum OperationErrorImportance {

    HIGH(Notification.Position.MIDDLE, -1),
    NORMAL(Notification.Position.TOP_END, 5000),
    LOW(Notification.Position.TOP_END, 3000);

    private final Notification.Position notificationPosition;
    private final int durationMillis;

}
