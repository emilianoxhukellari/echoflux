package transcribe.application.user;

import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.data.binder.Binder;
import transcribe.application.core.jpa.dialog.save.JpaSaveDialog;
import transcribe.application.spring.SpringContext;
import transcribe.domain.application_user.data.ApplicationUserEntity;
import transcribe.domain.application_user.service.ApplicationUserService;
import transcribe.domain.application_user.service.ChangePasswordCommand;

public class ChangePasswordDialog extends JpaSaveDialog<ApplicationUserEntity> {

    private final ApplicationUserService service;
    private final Binder<ChangePasswordCommand> binder;

    public ChangePasswordDialog(ApplicationUserEntity entity) {
        super(ApplicationUserEntity.class);
        this.service = SpringContext.getBean(ApplicationUserService.class);
        this.binder = new Binder<>(ChangePasswordCommand.class);

        this.binder.setBean(
                ChangePasswordCommand.builder()
                        .id(entity.getId())
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
    protected void save() {
        service.changePassword(binder.getBean());
    }

    @Override
    protected boolean validate() {
        return binder.validate().isOk();
    }

}
