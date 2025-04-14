package transcribe.application.user;

import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.combobox.MultiSelectComboBox;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import org.apache.commons.lang3.StringUtils;
import transcribe.application.core.jpa.dialog.save.JpaSaveDialog;
import transcribe.application.core.jpa.dto.JpaDtoService;
import transcribe.application.core.jpa.dto.impl.SimpleJpaDtoService;
import transcribe.application.core.operation.OperationRunner;
import transcribe.core.core.bean.loader.BeanLoader;
import transcribe.domain.application_user.data.ApplicationUserEntity;
import transcribe.domain.application_user.data.Role;
import transcribe.domain.application_user.service.ApplicationUserService;
import transcribe.domain.application_user.service.CreateApplicationUserCommand;

import java.util.Objects;

public class CreateUserDialog extends JpaSaveDialog<ApplicationUserJpaDto> {

    private final ApplicationUserService applicationUserService;
    private final JpaDtoService<ApplicationUserJpaDto, ApplicationUserEntity, Long> jpaDtoService;
    private final Binder<CreateApplicationUserCommand> binder;

    public CreateUserDialog(BeanLoader beanLoader) {
        super(ApplicationUserJpaDto.class, beanLoader.load(OperationRunner.class));
        Objects.requireNonNull(beanLoader, "beanLoader");

        this.applicationUserService = beanLoader.load(ApplicationUserService.class);
        this.jpaDtoService = new SimpleJpaDtoService<>(ApplicationUserJpaDto.class, beanLoader);
        this.binder = new Binder<>(CreateApplicationUserCommand.class);
        this.binder.setBean(CreateApplicationUserCommand.builder().build());

        var usernameField = new TextField("Username");
        binder.forField(usernameField)
                .asRequired("Username is required")
                .bind(CreateApplicationUserCommand::getUsername, CreateApplicationUserCommand::setUsername);

        var nameField = new TextField("Name");
        binder.forField(nameField)
                .asRequired("Name is required")
                .bind(CreateApplicationUserCommand::getName, CreateApplicationUserCommand::setName);

        var passwordField = new PasswordField("Password");
        binder.forField(passwordField)
                .asRequired("Password is required")
                .bind(CreateApplicationUserCommand::getPassword, CreateApplicationUserCommand::setPassword);

        var confirmPasswordField = new PasswordField("Confirm Password");
        binder.forField(confirmPasswordField)
                .asRequired("Confirm Password is required")
                .withValidator(cp -> StringUtils.equals(cp, passwordField.getValue()), "Passwords do not match")
                .bind(CreateApplicationUserCommand::getPasswordConfirmation, CreateApplicationUserCommand::setPasswordConfirmation);

        var enabledField = new Checkbox("Enabled");
        binder.forField(enabledField)
                .bind(CreateApplicationUserCommand::getEnabled, CreateApplicationUserCommand::setEnabled);

        var rolesField = new MultiSelectComboBox<Role>("Roles");
        rolesField.setItems(Role.values());
        binder.forField(rolesField)
                .bind(CreateApplicationUserCommand::getRoles, CreateApplicationUserCommand::setRoles);

        var form = new FormLayout();
        form.add(usernameField, nameField, passwordField, confirmPasswordField, enabledField, rolesField);

        add(form);
    }

    @Override
    protected ApplicationUserJpaDto save() {
        var created = applicationUserService.create(binder.getBean());

        return jpaDtoService.getById(created.id());
    }

    @Override
    protected boolean validate() {
        return binder.validate().isOk();
    }

}
