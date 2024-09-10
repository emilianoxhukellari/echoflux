package transcribe.application.core.dialog;

import com.vaadin.flow.component.confirmdialog.ConfirmDialog;
import transcribe.application.core.error.ErrorUtils;
import transcribe.application.core.spring.SpringContext;
import transcribe.application.security.AuthenticatedUser;

public final class Dialogs {

    public static void confirm(String message, Runnable onConfirm) {
        var dialog = new ConfirmDialog();
        dialog.setHeader("Confirm");
        dialog.setText(message);
        dialog.setConfirmButton("Yes", _ -> onConfirm.run());
        dialog.setConfirmButtonTheme("primary");
        dialog.setCancelButton("No", _ -> dialog.close());
        dialog.setCancelButtonTheme("error primary");
        dialog.open();
    }

    public static void show(String message) {
        var dialog = new ConfirmDialog();
        dialog.setHeader("Information");
        dialog.setText(message);
        dialog.setConfirmButton("OK", _ -> dialog.close());
        dialog.setConfirmButtonTheme("primary");
        dialog.open();
    }

    public static void error(Throwable e) {
        var dialog = new ConfirmDialog();
        dialog.setHeader("Error");
        dialog.setText(resolveErrorMessage(e));
        dialog.setConfirmButton("OK", _ -> dialog.close());
        dialog.setConfirmButtonTheme("error primary");
        dialog.open();
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
