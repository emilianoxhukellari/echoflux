package echoflux.application.user;

import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.combobox.MultiSelectComboBox;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.ValidationException;
import echoflux.core.core.country.Country;
import echoflux.core.core.utils.MoreEnums;
import lombok.SneakyThrows;
import org.apache.commons.lang3.StringUtils;
import echoflux.application.core.jpa.dialog.SaveDialog;
import echoflux.core.core.bean.loader.BeanLoader;
import echoflux.domain.application_user.data.Role;
import echoflux.domain.application_user.service.ApplicationUserService;
import echoflux.domain.application_user.service.CreateApplicationUserCommand;

import java.time.ZoneId;
import java.util.List;
import java.util.Objects;

public class CreateUserDialog extends SaveDialog<Long> {

    private final ApplicationUserService applicationUserService;
    private final Binder<CreateApplicationUserCommand> binder;

    public CreateUserDialog(BeanLoader beanLoader) {
        Objects.requireNonNull(beanLoader, "beanLoader");

        this.applicationUserService = beanLoader.load(ApplicationUserService.class);
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

        var zoneIdField = new ComboBox<ZoneId>("Zone ID");
        binder.forField(zoneIdField)
                .asRequired("Zone ID is required")
                .bind(CreateApplicationUserCommand::getZoneId, CreateApplicationUserCommand::setZoneId);

        var countryField = new ComboBox<>("Country", Country.values());
        countryField.setItemLabelGenerator(MoreEnums::toDisplayName);
        countryField.addValueChangeListener(e -> {
            if (e.getValue() != null) {
                zoneIdField.setItems(e.getValue().getZoneIds());
            } else {
                zoneIdField.setItems(List.of());
            }
        });
        binder.forField(countryField)
                .asRequired("Country is required")
                .bind(CreateApplicationUserCommand::getCountry, CreateApplicationUserCommand::setCountry);

        var rolesField = new MultiSelectComboBox<Role>("Roles");
        rolesField.setItems(Role.values());
        binder.forField(rolesField)
                .bind(CreateApplicationUserCommand::getRoles, CreateApplicationUserCommand::setRoles);

        var form = new FormLayout();
        form.add(usernameField, nameField, passwordField, confirmPasswordField, enabledField, countryField, zoneIdField, rolesField);

        add(form);
    }

    @SneakyThrows({ValidationException.class})
    @Override
    protected Long save() {
        binder.writeBean(binder.getBean());

        return applicationUserService.create(binder.getBean()).getId();
    }

    @Override
    protected boolean validate() {
        return binder.validate().isOk();
    }

}
