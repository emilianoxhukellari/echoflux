package transcribe.application.user;

import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.combobox.MultiSelectComboBox;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import transcribe.application.core.jpa.dialog.save.JpaSaveDialog;
import transcribe.application.core.jpa.dto.impl.SimpleJpaDtoService;
import transcribe.application.core.spring.SpringContext;
import transcribe.domain.application_user.data.Role;
import transcribe.domain.application_user.service.ApplicationUserService;
import transcribe.domain.application_user.service.UpdateApplicationUserCommand;

public class UpdateUserDialog extends JpaSaveDialog<ApplicationUserJpaDto> {

    private final ApplicationUserService service;
    private final Binder<UpdateApplicationUserCommand> binder;

    public UpdateUserDialog(ApplicationUserJpaDto applicationUser) {
        super(ApplicationUserJpaDto.class);
        this.service = SpringContext.getBean(ApplicationUserService.class);
        this.binder = new Binder<>(UpdateApplicationUserCommand.class);

        this.binder.setBean(SpringContext.getBean(ApplicationUserJpaDtoMapper.class).toUpdateCommand(applicationUser));

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
    protected ApplicationUserJpaDto save() {
        return SimpleJpaDtoService.ofBeanType(ApplicationUserJpaDto.class)
                .perform(() -> service.update(binder.getBean()));
    }

    @Override
    protected boolean validate() {
        return binder.validate().isOk();
    }

}
