package transcribe.application.core.notification;

import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import lombok.extern.slf4j.Slf4j;
import transcribe.application.core.error.ErrorUtils;
import transcribe.application.security.AuthenticatedUser;
import transcribe.application.spring.SpringContext;

@Slf4j
public final class Notifications {

    public static void showError(Throwable e) {
        var notification = new Notification();
        notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
        notification.setPosition(Notification.Position.MIDDLE);

        var closeButton = new Button(VaadinIcon.CLOSE.create());
        closeButton.addClickListener(_ -> notification.close());
        var text = new Text(resolveErrorMessage(e));
        var hl = new HorizontalLayout(text, closeButton);
        hl.setAlignItems(HorizontalLayout.Alignment.CENTER);

        notification.add(hl);
        notification.open();
    }

    private static String resolveErrorMessage(Throwable e) {
        if (SpringContext.getBean(AuthenticatedUser.class).isAdmin()) {
            return e.getMessage();
        } else {
            if (ErrorUtils.isPropagated(e)) {
                return e.getCause().getMessage();
            } else {
                return "An error occurred. Please try again or contact support.";
            }
        }
    }

}
