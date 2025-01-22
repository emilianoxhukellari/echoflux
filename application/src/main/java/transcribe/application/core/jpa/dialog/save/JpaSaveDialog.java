package transcribe.application.core.jpa.dialog.save;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import lombok.Setter;
import lombok.experimental.Accessors;
import transcribe.application.core.dialog.EnhancedDialog;
import transcribe.application.core.operation.Operation;
import transcribe.application.core.operation.OperationErrorImportance;
import transcribe.application.core.operation.OperationRunner;
import transcribe.application.core.spring.SpringContext;
import transcribe.core.core.no_op.NoOp;
import transcribe.core.core.bean.utils.MoreBeans;
import transcribe.domain.operation.data.OperationType;

import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;

public abstract class JpaSaveDialog<T> extends EnhancedDialog {

    protected final OperationRunner operationRunner = SpringContext.getBean(OperationRunner.class);
    protected final Class<T> beanType;

    @Setter
    @Accessors(chain = true)
    protected Consumer<T> saveListener = NoOp.consumer();

    @Setter
    @Accessors(chain = true)
    protected Function<Operation<T>, Operation<T>> operationCustomizer = Function.identity();

    public JpaSaveDialog(Class<T> beanType) {
        this.beanType = Objects.requireNonNull(beanType, "Bean bean type must not be null");

        setHeaderTitle(String.format("Save %s", MoreBeans.getDisplayName(beanType)));
        getFooter().add(newFooterContent());
        setWidth("600px");
        setMaxWidth("95vw");
    }

    protected abstract T save();

    protected abstract boolean validate();

    private HorizontalLayout newFooterContent() {
        var hl = new HorizontalLayout();
        hl.setSizeFull();
        hl.add(newSaveButton());
        hl.setJustifyContentMode(FlexComponent.JustifyContentMode.END);

        return hl;
    }

    private Button newSaveButton() {
        var button = new Button("Save", _ -> {
            if (validate()) {
                var operation = Operation.<T>builder()
                        .name("Saving [%s]".formatted(MoreBeans.getDisplayName(beanType)))
                        .beforeCall(this::close)
                        .callable(this::save)
                        .onSuccess(saveListener)
                        .type(OperationType.NON_BLOCKING)
                        .errorImportance(OperationErrorImportance.HIGH)
                        .build();

                operationRunner.run(operationCustomizer.apply(operation), UI.getCurrent());
            }
        });
        button.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        return button;
    }

}
