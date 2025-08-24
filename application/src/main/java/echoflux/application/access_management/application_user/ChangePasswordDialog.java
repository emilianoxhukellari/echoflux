package echoflux.application.access_management.application_user;

import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.data.binder.Binder;
import echoflux.application.core.dialog.SaveDialog;
import echoflux.core.core.bean.accessor.BeanAccessor;
import echoflux.core.core.validate.guard.Guard;
import echoflux.domain.access_management.endpoint.AccessManagementEndpoint;
import echoflux.domain.access_management.application_user.service.ChangePasswordCommand;

public class ChangePasswordDialog extends SaveDialog<Long> {

    private final AccessManagementEndpoint accessManagementEndpoint;
    private final Binder<ChangePasswordCommand> binder;

    public ChangePasswordDialog(Long applicationUserId, BeanAccessor beanAccessor) {
        Guard.notNull(applicationUserId, "applicationUserId");
        Guard.notNull(beanAccessor, "beanAccessor");

        this.accessManagementEndpoint = beanAccessor.get(AccessManagementEndpoint.class);
        this.binder = new Binder<>();

        this.binder.setBean(
                ChangePasswordCommand.builder()
                        .id(applicationUserId)
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
    protected Long save() {
        return accessManagementEndpoint.changeApplicationUserPassword(binder.getBean());
    }

    @Override
    protected boolean validate() {
        return binder.validate().isOk();
    }

}
