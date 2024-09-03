package transcribe.application.core.operation;

import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.H4;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.progressbar.ProgressBar;
import org.apache.commons.lang3.Validate;
import transcribe.domain.operation.data.OperationType;

import java.util.Objects;

public class OperationNotifier {

    private OperationDialog dialog;
    private OperationNotification notification;

    private final String name;
    private final OperationType type;

    public OperationNotifier(String name, OperationType type) {
        this.name = Validate.notBlank(name);
        this.type = Objects.requireNonNull(type);
    }

    public void open() {
        switch (type) {
            case BLOCKING -> {
                if (dialog == null) {
                    dialog = new OperationDialog(name);
                }
                dialog.open();
            }
            case NON_BLOCKING -> {
                if (notification == null) {
                    notification = new OperationNotification(name);
                }
                notification.open();
            }
            default -> throw new IllegalArgumentException("Unsupported operation type: " + type);
        }
    }

    public void close() {
        switch (type) {
            case BLOCKING -> {
                if (dialog != null) {
                    dialog.close();
                }
            }
            case NON_BLOCKING -> {
                if (notification != null) {
                    notification.close();
                }
            }
            default -> throw new IllegalArgumentException("Unsupported operation type: " + type);
        }
    }

    private static class OperationDialog extends Dialog {

        public OperationDialog(String name) {
            var nameComponent = new H3(name);
            nameComponent.setWidthFull();
            nameComponent.getStyle().set("text-align", "center");
            getHeader().add(nameComponent);
            setCloseOnEsc(false);
            setCloseOnOutsideClick(false);
            setModal(true);

            var progressBar = new ProgressBar();
            progressBar.setIndeterminate(true);
            progressBar.setHeight("8px");
            progressBar.setWidthFull();

            add(progressBar);
            setWidth("400px");
        }

    }

    private static class OperationNotification extends Notification {

        public OperationNotification(String name) {
            setPosition(Position.TOP_END);
            addThemeVariants(NotificationVariant.LUMO_CONTRAST);

            var nameComponent = new H4(name);
            nameComponent.setWidthFull();
            nameComponent.getStyle().set("text-align", "center");

            var progressBar = new ProgressBar();
            progressBar.setIndeterminate(true);
            progressBar.setWidthFull();
            progressBar.setHeight("6px");

            var vl = new VerticalLayout(progressBar);
            vl.setPadding(false);
            vl.setWidth("200px");

            add(vl);
        }

    }

}
