package echoflux.application.template;

import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.ValidationException;
import echoflux.application.core.dialog.SaveDialog;
import echoflux.core.core.bean.accessor.BeanAccessor;
import echoflux.core.core.validate.guard.Guard;
import echoflux.domain.jooq.tables.pojos.Template;
import echoflux.domain.template.endpoint.TemplateEndpoint;
import echoflux.domain.template.service.SaveTemplateCommand;
import jakarta.annotation.Nullable;
import lombok.SneakyThrows;

public class SaveTemplateDialog extends SaveDialog<Long> {

    private final TemplateEndpoint templateEndpoint;
    private final Binder<SaveTemplateCommand> binder;

    public static SaveTemplateDialog newCreate(BeanAccessor beanAccessor) {
        return new SaveTemplateDialog(null, beanAccessor);
    }

    public static SaveTemplateDialog newUpdate(Template template, BeanAccessor beanAccessor) {
        return new SaveTemplateDialog(template, beanAccessor);
    }

    public SaveTemplateDialog(@Nullable Template template, BeanAccessor beanAccessor) {
        Guard.notNull(beanAccessor);

        this.templateEndpoint = beanAccessor.get(TemplateEndpoint.class);
        this.binder = new Binder<>();

        boolean updateMode = template != null;
        var bean = new SaveTemplateCommand();
        if (updateMode) {
            bean.setId(template.getId());
            bean.setName(template.getName());
            bean.setContent(template.getContent());
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
                                ? "Updating Template with ID [%s]".formatted(template.getId())
                                : "Creating Template"
                )
        );
    }

    @SneakyThrows({ValidationException.class})
    @Override
    protected Long save() {
        binder.writeBean(binder.getBean());

        return templateEndpoint.save(binder.getBean());
    }

    @Override
    protected boolean validate() {
        return binder.validate().isOk();
    }

}
