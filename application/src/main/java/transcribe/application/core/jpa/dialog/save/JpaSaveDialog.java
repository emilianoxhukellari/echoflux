package transcribe.application.core.jpa.dialog.save;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.apache.commons.lang3.Validate;
import transcribe.application.core.dialog.EnhancedDialog;
import transcribe.application.core.operation.Operation;
import transcribe.application.core.operation.OperationCallable;
import transcribe.application.core.operation.OperationRunner;
import transcribe.application.spring.SpringContext;
import transcribe.core.common.no_op.NoOp;
import transcribe.domain.bean.BeanUtils;

public abstract class JpaSaveDialog<T> extends EnhancedDialog {

    private final OperationRunner operationRunner = SpringContext.getBean(OperationRunner.class);
    protected final Class<T> entityBeanType;

    /**
     * Listener will be called after the entity has been saved.
     * */
    @Setter
    @Accessors(chain = true)
    private Runnable saveListener = NoOp.runnable();

    public JpaSaveDialog(Class<T> entityBeanType) {
        this.entityBeanType = Validate.notNull(entityBeanType, "Entity bean type must not be null");

        setHeaderTitle(String.format("Save %s", BeanUtils.getPrettyName(entityBeanType)));
        getFooter().add(newFooterContent());
        setWidth("800px");
    }

    protected abstract void save();

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
                var operation = Operation.builder()
                        .name("Save entity")
                        .description(String.format("Entity of type [%s]", BeanUtils.getPrettyName(entityBeanType)))
                        .callable(OperationCallable.ofRunnable(this::save))
                        .onFinally(this::close)
                        .onSuccess(_ -> saveListener.run())
                        .build();

                operationRunner.run(operation);
            }
        });
        button.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        return button;
    }

}
