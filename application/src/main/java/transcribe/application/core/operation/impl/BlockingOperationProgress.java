package transcribe.application.core.operation.impl;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.progressbar.ProgressBar;
import transcribe.application.core.operation.OperationProgress;

public class BlockingOperationProgress implements OperationProgress {

    private final Dialog dialog;

    public BlockingOperationProgress(String name) {
        this.dialog = new Dialog();
        this.dialog.getHeader().add(newNameComponent(name));
        this.dialog.add(newProgressBar());

        this.dialog.setCloseOnEsc(false);
        this.dialog.setCloseOnOutsideClick(false);
        this.dialog.setModal(true);
        this.dialog.setWidth("400px");
    }

    @Override
    public void open() {
        dialog.open();
    }

    @Override
    public void close() {
        dialog.close();
    }

    private static Component newNameComponent(String name) {
        var nameComponent = new H3(name);
        nameComponent.setWidthFull();
        nameComponent.getStyle().set("text-align", "center");

        return nameComponent;
    }

    private static ProgressBar newProgressBar() {
        var progressBar = new ProgressBar();
        progressBar.setIndeterminate(true);
        progressBar.setHeight("8px");
        progressBar.setWidthFull();

        return progressBar;
    }

}
