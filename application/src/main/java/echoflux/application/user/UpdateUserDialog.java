package echoflux.application.user;

import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.combobox.MultiSelectComboBox;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.ValidationException;
import echoflux.core.core.country.Country;
import echoflux.core.core.utils.MoreEnums;
import echoflux.domain.application_user.data.ApplicationUserProjection;
import echoflux.domain.application_user.service.ApplicationUserMapper;
import lombok.SneakyThrows;
import echoflux.application.core.jpa.dialog.SaveDialog;
import echoflux.core.core.bean.loader.BeanLoader;
import echoflux.domain.application_user.data.Role;
import echoflux.domain.application_user.service.ApplicationUserService;
import echoflux.domain.application_user.service.UpdateApplicationUserCommand;

import java.time.ZoneId;
import java.util.List;
import java.util.Objects;

public class UpdateUserDialog extends SaveDialog<Long> {

    private final ApplicationUserService applicationUserService;
    private final Binder<UpdateApplicationUserCommand> binder;

    public UpdateUserDialog(ApplicationUserProjection applicationUser, BeanLoader beanLoader) {
        Objects.requireNonNull(applicationUser, "applicationUser");

        this.applicationUserService = beanLoader.load(ApplicationUserService.class);
        this.binder = new Binder<>(UpdateApplicationUserCommand.class);

        var command = beanLoader.load(ApplicationUserMapper.class).toCommand(applicationUser);
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

        var zoneIdField = new ComboBox<ZoneId>("Zone ID");
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
                .bind(UpdateApplicationUserCommand::getCountry, UpdateApplicationUserCommand::setCountry);
        binder.forField(zoneIdField)
                .asRequired("Zone ID is required")
                .bind(UpdateApplicationUserCommand::getZoneId, UpdateApplicationUserCommand::setZoneId);

        var rolesField = new MultiSelectComboBox<Role>("Roles");
        rolesField.setItems(Role.values());
        binder.forField(rolesField)
                .bind(UpdateApplicationUserCommand::getRoles, UpdateApplicationUserCommand::setRoles);

        var form = new FormLayout();
        form.add(usernameField, nameField, enabledField, countryField, zoneIdField, rolesField);

        add(form);
    }

    @SneakyThrows({ValidationException.class})
    @Override
    protected Long save() {
        binder.writeBean(binder.getBean());

        return applicationUserService.update(binder.getBean()).getId();
    }

    @Override
    protected boolean validate() {
        return binder.validate().isOk();
    }

}
