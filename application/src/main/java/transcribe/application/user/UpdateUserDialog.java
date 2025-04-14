package transcribe.application.user;

import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.combobox.MultiSelectComboBox;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.ValidationException;
import lombok.SneakyThrows;
import transcribe.application.core.jpa.dialog.save.JpaSaveDialog;
import transcribe.application.core.jpa.dto.JpaDtoService;
import transcribe.application.core.jpa.dto.impl.SimpleJpaDtoService;
import transcribe.application.core.operation.OperationRunner;
import transcribe.core.core.bean.loader.BeanLoader;
import transcribe.domain.application_user.data.ApplicationUserEntity;
import transcribe.domain.application_user.data.Role;
import transcribe.domain.application_user.service.ApplicationUserService;
import transcribe.domain.application_user.service.UpdateApplicationUserCommand;

import java.util.Objects;

public class UpdateUserDialog extends JpaSaveDialog<ApplicationUserJpaDto> {

    private final ApplicationUserService applicationUserService;
    private final JpaDtoService<ApplicationUserJpaDto, ApplicationUserEntity, Long> jpaDtoService;
    private final Binder<UpdateApplicationUserCommand> binder;

    public UpdateUserDialog(ApplicationUserJpaDto applicationUser, BeanLoader beanLoader) {
        super(ApplicationUserJpaDto.class, beanLoader.load(OperationRunner.class));
        Objects.requireNonNull(applicationUser, "applicationUser");

        this.applicationUserService = beanLoader.load(ApplicationUserService.class);
        this.jpaDtoService = new SimpleJpaDtoService<>(ApplicationUserJpaDto.class, beanLoader);
        this.binder = new Binder<>(UpdateApplicationUserCommand.class);

        var command = beanLoader.load(ApplicationUserJpaDtoMapper.class)
                .toUpdateCommand(applicationUser);
        this.binder.setBean(command);

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

    @SneakyThrows({ValidationException.class})
    @Override
    protected ApplicationUserJpaDto save() {
        binder.writeBean(binder.getBean());
        var patchedUser = applicationUserService.patch(binder.getBean());

        return jpaDtoService.getById(patchedUser.id());
    }

    @Override
    protected boolean validate() {
        return binder.validate().isOk();
    }

}
