package transcribe.application.core.dialog;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.TextArea;
import lombok.Getter;
import org.apache.commons.collections4.ListUtils;
import org.apache.commons.collections4.PredicateUtils;

import java.util.ArrayList;
import java.util.List;

public class EnhancedDialog extends Dialog {

    private DialogFooterLayout footerLayout;
    private final List<Runnable> closeButtonListeners = ListUtils.predicatedList(
            new ArrayList<>(),
            PredicateUtils.notNullPredicate()
    );

    public EnhancedDialog() {
        getHeader().add(newCloseButton());

        setCloseOnEsc(true);
        setCloseOnOutsideClick(false);
        setResizable(true);
        setDraggable(true);
        setModal(false);
    }

    public EnhancedDialog withWidth(String width) {
        setWidth(width);

        return this;
    }

    public EnhancedDialog withTitle(String title) {
        setHeaderTitle(title);

        return this;
    }

    public EnhancedDialog withContent(Component content) {
        removeAll();
        add(content);

        return this;
    }

    public EnhancedDialog addFooterComponentLeft(Component component) {
        ensureFooterLayout().getLeft().add(component);

        return this;
    }

    public EnhancedDialog addFooterComponentMiddle(Component component) {
        ensureFooterLayout().getMiddle().add(component);

        return this;
    }

    public EnhancedDialog addFooterComponentRight(Component component) {
        ensureFooterLayout().getRight().add(component);

        return this;
    }

    public EnhancedDialog withContent(String content) {
        var textArea = new TextArea();
        textArea.setValue(content);
        textArea.setReadOnly(true);
        textArea.setWidthFull();

        return withContent(textArea);
    }

    public EnhancedDialog stopClosePropagation() {
        if (isModal()) {
            UI.getCurrent().add(this);
        }

        return this;
    }

    public EnhancedDialog resizable(boolean resizable) {
        setResizable(resizable);

        return this;
    }

    public EnhancedDialog draggable(boolean draggable) {
        setDraggable(draggable);

        return this;
    }

    public EnhancedDialog modal(boolean modal) {
        setModal(modal);

        return this;
    }

    public EnhancedDialog opened(boolean opened) {
        setOpened(opened);

        return this;
    }

    public EnhancedDialog addCloseButtonListener(Runnable listener) {
        closeButtonListeners.add(listener);

        return this;
    }

    private Button newCloseButton() {
        var closeButton = new Button(VaadinIcon.CLOSE_BIG.create(), _ -> {
            close();
            closeButtonListeners.forEach(Runnable::run);
        });
        closeButton.addThemeVariants(ButtonVariant.LUMO_ICON, ButtonVariant.LUMO_ERROR);

        return closeButton;
    }

    private DialogFooterLayout ensureFooterLayout() {
        if (footerLayout == null) {
            footerLayout = new DialogFooterLayout();
            getFooter().add(footerLayout);
        }

        return footerLayout;
    }

    @Getter
    private static class DialogFooterLayout extends HorizontalLayout {

        private final HorizontalLayout left;
        private final HorizontalLayout middle;
        private final HorizontalLayout right;

        public DialogFooterLayout() {
            this.left = new HorizontalLayout();
            left.setAlignItems(Alignment.CENTER);
            left.setJustifyContentMode(JustifyContentMode.START);

            this.right = new HorizontalLayout();
            right.setAlignItems(Alignment.CENTER);
            right.setJustifyContentMode(JustifyContentMode.END);

            this.middle = new HorizontalLayout();
            middle.setAlignItems(Alignment.CENTER);
            middle.setJustifyContentMode(JustifyContentMode.CENTER);

            add(left, middle, right);
            setAlignItems(Alignment.CENTER);
            setJustifyContentMode(JustifyContentMode.BETWEEN);
            setWidthFull();
        }

    }

}
