package transcribe.application.core.operation.impl;

import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import transcribe.application.core.operation.OperationProgress;
import transcribe.application.core.progress.CircularProgress;

public class NonBlockingOperationProgress implements OperationProgress {

    private final Notification notification;

    public NonBlockingOperationProgress(String name) {
        this.notification = new Notification();
        this.notification.setPosition(Notification.Position.TOP_END);
        this.notification.addThemeVariants(NotificationVariant.LUMO_CONTRAST);

        var hl = new HorizontalLayout(newProgressBar(), new Text(name));
        hl.setAlignItems(FlexComponent.Alignment.CENTER);
        hl.setJustifyContentMode(FlexComponent.JustifyContentMode.BETWEEN);
        hl.setWidth("300px");

        this.notification.add(hl);
    }

    @Override
    public void open() {
        notification.open();
    }

    @Override
    public void close() {
        notification.close();
    }

    private static CircularProgress newProgressBar() {
        var progress = new CircularProgress();
        progress.setIndeterminate(true);
        progress.setSize("45px");

        return progress;
    }

}
