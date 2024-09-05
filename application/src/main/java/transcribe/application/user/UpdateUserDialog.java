package transcribe.application.user;

import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.combobox.MultiSelectComboBox;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import transcribe.application.core.jpa.dialog.save.JpaSaveDialog;
import transcribe.application.spring.SpringContext;
import transcribe.domain.application_user.data.ApplicationUserEntity;
import transcribe.domain.application_user.data.Role;
import transcribe.domain.application_user.service.ApplicationUserMapper;
import transcribe.domain.application_user.service.ApplicationUserService;
import transcribe.domain.application_user.service.UpdateApplicationUserCommand;

public class UpdateUserDialog extends JpaSaveDialog<ApplicationUserEntity> {

    private final ApplicationUserService service;
    private final Binder<UpdateApplicationUserCommand> binder;

    public UpdateUserDialog(ApplicationUserEntity entity) {
        super(ApplicationUserEntity.class);
        this.service = SpringContext.getBean(ApplicationUserService.class);
        this.binder = new Binder<>(UpdateApplicationUserCommand.class);

        this.binder.setBean(SpringContext.getBean(ApplicationUserMapper.class).toUpdateCommand(entity));

        var usernameField = new TextField("Username");
        binder.forField(usernameField)
                .asRequired("Username is required")
                .bind(UpdateApplicationUserCommand::getUsername, UpdateApplicationUserCommand::setUsername);

        var nameField = new TextField("Name");
        binder.forField(nameField)
                .asRequired("Name is required")
                .bind(UpdateApplicationUserCommand::getName, UpdateApplicationUserCommand::setName);

        var enabledField = new Checkbox("Enabled");
        binder.forField(enabledField)
                .asRequired()
                .bind(UpdateApplicationUserCommand::getEnabled, UpdateApplicationUserCommand::setEnabled);

        var rolesField = new MultiSelectComboBox<Role>("Roles");
        rolesField.setItems(Role.values());
        binder.forField(rolesField)
                .bind(UpdateApplicationUserCommand::getRoles, UpdateApplicationUserCommand::setRoles);

        var form = new FormLayout();
        form.add(usernameField, nameField, enabledField, rolesField);

        add(form);
    }

    @Override
    protected void save() {
        service.update(binder.getBean());
    }

    @Override
    protected boolean validate() {
        return binder.validate().isOk();
    }

}
