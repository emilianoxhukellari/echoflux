package echoflux.application.template;

import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.ValidationException;
import echoflux.application.core.jpa.dialog.SaveDialog;
import echoflux.core.core.bean.loader.BeanLoader;
import echoflux.core.core.validate.guard.Guard;
import echoflux.domain.template.data.TemplateProjection;
import echoflux.domain.template.service.SaveTemplateCommand;
import echoflux.domain.template.service.TemplateService;
import jakarta.annotation.Nullable;
import lombok.SneakyThrows;

public class SaveTemplateDialog extends SaveDialog<Long> {

    private final TemplateService templateService;
    private final Binder<SaveTemplateCommand> binder;

    public static SaveTemplateDialog newCreate(BeanLoader beanLoader) {
        return new SaveTemplateDialog(null, beanLoader);
    }

    public static SaveTemplateDialog newUpdate(TemplateProjection templateProjection, BeanLoader beanLoader) {
        return new SaveTemplateDialog(templateProjection, beanLoader);
    }

    public SaveTemplateDialog(@Nullable TemplateProjection templateProjection, BeanLoader beanLoader) {
        Guard.notNull(beanLoader);

        this.templateService = beanLoader.load(TemplateService.class);
        this.binder = new Binder<>();

        boolean updateMode = templateProjection != null;
        var bean = new SaveTemplateCommand();
        if (updateMode) {
            bean.setId(templateProjection.getId());
            bean.setName(templateProjection.getName());
            bean.setContent(templateProjection.getContent());
        }

        binder.setBean(bean);

        var nameField = new TextField("Name");
        binder.forField(nameField)
                .asRequired("Name is required")
                .bind(SaveTemplateCommand::getName, SaveTemplateCommand::setName);

        var contentField = new TextArea("Content");
        binder.forField(contentField)
                .asRequired("Content is required")
                .bind(SaveTemplateCommand::getContent, SaveTemplateCommand::setContent);

        var formLayout = new FormLayout();
        formLayout.add(nameField, 2);
        formLayout.add(contentField, 2);

        withContent(formLayout);
        setHeaderTitle(updateMode ? "Update Template" : "Create Template");
        withOperationCustomizer(
                o -> o.withName(
                        updateMode
                                ? "Updating Template with ID [%s]".formatted(templateProjection.getId())
                                : "Creating Template"
                )
        );
    }

    @SneakyThrows({ValidationException.class})
    @Override
    protected Long save() {
        binder.writeBean(binder.getBean());

        return templateService.save(binder.getBean()).getId();
    }

    @Override
    protected boolean validate() {
        return binder.validate().isOk();
    }

}
