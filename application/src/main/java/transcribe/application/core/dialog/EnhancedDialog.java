package transcribe.application.core.dialog;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.icon.VaadinIcon;

public class EnhancedDialog extends Dialog {

    public EnhancedDialog() {
        getHeader().add(newCloseButton());

        setCloseOnEsc(true);
        setCloseOnOutsideClick(false);
        setResizable(true);
        setDraggable(true);
        setModal(false);
    }

    public Dialog stopClosePropagation() {
        if (isModal()) {
            UI.getCurrent().add(this);
        }

        return this;
    }

    public Dialog resizable(boolean resizable) {
        setResizable(resizable);

        return this;
    }

    public Dialog draggable(boolean draggable) {
        setDraggable(draggable);

        return this;
    }

    public Dialog modal(boolean modal) {
        setModal(modal);

        return this;
    }

    public Dialog opened(boolean opened) {
        setOpened(opened);

        return this;
    }

    private Button newCloseButton() {
        var closeButton = new Button(VaadinIcon.CLOSE_BIG.create(), _ -> close());
        closeButton.addThemeVariants(ButtonVariant.LUMO_ICON, ButtonVariant.LUMO_ERROR);

        return closeButton;
    }

}
