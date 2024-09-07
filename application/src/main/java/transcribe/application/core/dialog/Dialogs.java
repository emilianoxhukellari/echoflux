package transcribe.application.core.dialog;

import com.vaadin.flow.component.confirmdialog.ConfirmDialog;

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

}
