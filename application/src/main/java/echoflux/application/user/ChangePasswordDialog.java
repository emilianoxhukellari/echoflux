package echoflux.application.user;

import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.data.binder.Binder;
import echoflux.application.core.jpa.dialog.save.JpaSaveDialog;
import echoflux.application.core.jpa.dto.JpaDtoService;
import echoflux.application.core.jpa.dto.impl.SimpleJpaDtoService;
import echoflux.application.core.operation.OperationRunner;
import echoflux.core.core.bean.loader.BeanLoader;
import echoflux.domain.application_user.data.ApplicationUserEntity;
import echoflux.domain.application_user.service.ApplicationUserService;
import echoflux.domain.application_user.service.ChangePasswordCommand;

import java.util.Objects;

public class ChangePasswordDialog extends JpaSaveDialog<ApplicationUserJpaDto> {

    private final ApplicationUserService applicationUserService;
    private final JpaDtoService<ApplicationUserJpaDto, ApplicationUserEntity, Long> jpaDtoService;
    private final Binder<ChangePasswordCommand> binder;

    public ChangePasswordDialog(ApplicationUserJpaDto applicationUser, BeanLoader beanLoader) {
        super(ApplicationUserJpaDto.class, beanLoader.load(OperationRunner.class));
        Objects.requireNonNull(applicationUser, "applicationUser");
        Objects.requireNonNull(beanLoader, "beanLoader");

        this.applicationUserService = beanLoader.load(ApplicationUserService.class);
        this.jpaDtoService = new SimpleJpaDtoService<>(ApplicationUserJpaDto.class, beanLoader);
        this.binder = new Binder<>(ChangePasswordCommand.class);

        this.binder.setBean(
                ChangePasswordCommand.builder()
                        .id(applicationUser.getId())
                        .build()
        );

        var passwordField = new PasswordField("New Password");
        binder.forField(passwordField)
                .asRequired("Password is required")
                .bind(ChangePasswordCommand::getPassword, ChangePasswordCommand::setPassword);

        var confirmPasswordField = new PasswordField("Confirm Password");
        binder.forField(confirmPasswordField)
                .asRequired("Confirm Password is required")
                .withValidator(cp -> cp.equals(passwordField.getValue()), "Passwords do not match")
                .bind(ChangePasswordCommand::getPasswordConfirmation, ChangePasswordCommand::setPasswordConfirmation);

        var form = new FormLayout();
        form.add(passwordField, confirmPasswordField);

        add(form);
        setHeaderTitle("Change Password");
    }

    @Override
    protected ApplicationUserJpaDto save() {
        var updatedUser = applicationUserService.changePassword(binder.getBean());

        return jpaDtoService.getById(updatedUser.id());
    }

    @Override
    protected boolean validate() {
        return binder.validate().isOk();
    }

}
