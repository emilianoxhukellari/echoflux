package echoflux.application.core.dialog;

import com.vaadin.flow.component.confirmdialog.ConfirmDialog;
import echoflux.application.core.error.MoreErrors;
import echoflux.application.security.AuthenticatedUser;

public final class TsDialogs {

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

    public static void info(String message) {
        info("Information", message);
    }

    public static void info(String header, String message) {
        var dialog = new ConfirmDialog();
        dialog.setHeader(header);
        dialog.setText(message);
        dialog.setConfirmButton("OK", _ -> dialog.close());
        dialog.setConfirmButtonTheme("primary");
        dialog.open();
    }

    public static void error(Throwable e, AuthenticatedUser authenticatedUser) {
        error(MoreErrors.resolveErrorMessage(e, authenticatedUser));
    }

    public static void error(String error) {
        var dialog = new ConfirmDialog();
        dialog.setHeader("Error");
        dialog.setText(error);
        dialog.setConfirmButton("OK", _ -> dialog.close());
        dialog.setConfirmButtonTheme("error primary");
        dialog.open();
    }

    public static void warn(String message) {
        warn("Warning", message);
    }

    public static void warn(String header, String message) {
        var dialog = new ConfirmDialog();
        dialog.setHeader(header);
        dialog.setText(message);
        dialog.setConfirmButton("OK", _ -> dialog.close());
        dialog.setConfirmButtonTheme("contrast primary");
        dialog.open();
    }

}
