package echoflux.application.core.dialog;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import echoflux.application.core.operation.OperationRunner;
import echoflux.core.core.validate.guard.Guard;
import echoflux.application.core.operation.Operation;
import echoflux.application.core.operation.OperationErrorImportance;
import echoflux.core.core.no_op.NoOp;
import echoflux.application.core.operation.OperationType;

import java.util.function.Consumer;
import java.util.function.Function;

public abstract class SaveDialog<T> extends EnhancedDialog {

    private Consumer<T> saveListener = NoOp.consumer();
    private Function<Operation<T>, Operation<T>> operationCustomizer = Function.identity();

    public SaveDialog() {
        withTitle("Save");
        addFooterComponentRight(newSaveButton());
        setWidth("600px");
        setMaxWidth("95vw");
    }

    public SaveDialog<T> withSaveListener(Consumer<T> saveListener) {
        Guard.notNull(saveListener, "Save listener must not be null");
        this.saveListener = saveListener;

        return this;
    }

    public SaveDialog<T> withOperationCustomizer(Function<Operation<T>, Operation<T>> operationCustomizer) {
        Guard.notNull(operationCustomizer, "Operation customizer must not be null");
        this.operationCustomizer = operationCustomizer;

        return this;
    }

    protected abstract T save();

    protected abstract boolean validate();

    private Button newSaveButton() {
        var button = new Button("Save", _ -> {
            if (validate()) {
                var operation = Operation.<T>builder()
                        .name("Saving")
                        .callable(this::save)
                        .onSuccess(result -> {
                            saveListener.accept(result);
                            close();
                        })
                        .type(OperationType.BLOCKING)
                        .errorImportance(OperationErrorImportance.HIGH)
                        .build();

                OperationRunner.run(operationCustomizer.apply(operation));
            }
        });
        button.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        return button;
    }

}
