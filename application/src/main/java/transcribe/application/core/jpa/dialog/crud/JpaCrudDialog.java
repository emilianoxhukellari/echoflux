package transcribe.application.core.jpa.dialog.crud;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.Validate;
import transcribe.application.core.dialog.EnhancedDialog;
import transcribe.application.core.operation.Operation;
import transcribe.application.core.operation.OperationCallable;
import transcribe.application.core.operation.OperationRunner;
import transcribe.application.spring.SpringContext;
import transcribe.domain.bean.BeanUtils;
import transcribe.domain.operation.data.OperationType;

@Setter(AccessLevel.PROTECTED)
@Getter(AccessLevel.PROTECTED)
public abstract class JpaCrudDialog<T> extends EnhancedDialog {

    private final OperationRunner operationRunner = SpringContext.getBean(OperationRunner.class);
    protected final JpaCrudDialogMode mode;
    protected final Class<T> beanType;
    protected final FormLayout form;

    public JpaCrudDialog(JpaCrudDialogMode mode, Class<T> beanType) {
        this.mode = Validate.notNull(mode, "Mode must not be null");
        this.beanType = Validate.notNull(beanType, "Bean type must not be null");
        this.form = new FormLayout();

        add(form);
        setHeaderTitle(newHeaderTitle());
        getFooter().add(newFooterContent());
        setWidth("800px");
    }

    protected abstract void save();

    protected abstract void delete();

    protected abstract boolean validate();

    private HorizontalLayout newFooterContent() {
        var hl = new HorizontalLayout();
        hl.setSizeFull();
        hl.setAlignItems(FlexComponent.Alignment.CENTER);

        if (JpaCrudDialogMode.EDIT.equals(mode)) {
            hl.add(newDeleteButton());
            hl.setJustifyContentMode(FlexComponent.JustifyContentMode.BETWEEN);
        } else {
            hl.setJustifyContentMode(FlexComponent.JustifyContentMode.END);
        }

        hl.add(newSaveButton());

        return hl;
    }

    private Button newSaveButton() {
        var text = switch (mode) {
            case NEW -> "Create";
            case EDIT -> "Update";
        };

        var operation = Operation.builder()
                .name(String.format("%s entity", text))
                .description(String.format("Entity of type [%s]", beanType.getSimpleName()))
                .callable(OperationCallable.ofRunnable(this::save))
                .onFinally(this::close)
                .type(OperationType.BLOCKING)
                .build();
        var button = new Button(text, _ -> {
            if (validate()) {
                operationRunner.run(operation);
            }
        });
        button.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        return button;
    }

    private Button newDeleteButton() {
        Validate.isTrue(JpaCrudDialogMode.EDIT.equals(mode), "Delete button is only available in edit mode");

        var operation = Operation.builder()
                .name("Deleting entity")
                .description(String.format("Entity of type [%s]", beanType.getSimpleName()))
                .callable(OperationCallable.ofRunnable(this::delete))
                .onFinally(this::close)
                .type(OperationType.BLOCKING)
                .build();
        var button = new Button("Delete", _ -> operationRunner.run(operation));
        button.addThemeVariants(ButtonVariant.LUMO_ERROR);

        return button;
    }

    private String newHeaderTitle() {
        return String.format("%s %s", mode.getPrettyName(), BeanUtils.toPrettyName(beanType));
    }

}
