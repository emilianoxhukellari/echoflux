package transcribe.application.core.operation.impl;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.html.H4;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.progressbar.ProgressBar;
import transcribe.application.core.operation.OperationNotifier;

public class NonBlockingOperationNotifier implements OperationNotifier {

    private final Notification notification;

    public NonBlockingOperationNotifier(String name) {
        this.notification = new Notification();
        this.notification.setPosition(Notification.Position.TOP_END);
        this.notification.addThemeVariants(NotificationVariant.LUMO_CONTRAST);

        var vl = new VerticalLayout(newProgressBar());
        vl.setPadding(false);
        vl.setWidth("200px");

        this.notification.add(vl);
    }

    @Override
    public void open() {
        notification.open();
    }

    @Override
    public void close() {
        notification.close();
    }

    private static Component newNameComponent(String name) {
        var nameComponent = new H4(name);
        nameComponent.setWidthFull();
        nameComponent.getStyle().set("text-align", "center");

        return nameComponent;
    }

    private static ProgressBar newProgressBar() {
        var progressBar = new ProgressBar();
        progressBar.setIndeterminate(true);
        progressBar.setHeight("6px");
        progressBar.setWidthFull();

        return progressBar;
    }

}
