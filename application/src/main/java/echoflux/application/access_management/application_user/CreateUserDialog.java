package echoflux.application.access_management.application_user;

import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.combobox.MultiSelectComboBox;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.ValidationException;
import echoflux.application.access_management.data_provider.RoleEntryDataProvider;
import echoflux.core.core.country.Country;
import echoflux.core.core.utils.MoreEnums;
import echoflux.core.core.utils.MoreSets;
import echoflux.core.core.validate.guard.Guard;
import echoflux.application.access_management.role.RoleEntry;
import echoflux.domain.access_management.application_user.service.CreateApplicationUserCommand;
import echoflux.domain.access_management.endpoint.AccessManagementEndpoint;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.SneakyThrows;
import org.apache.commons.lang3.StringUtils;
import echoflux.application.core.dialog.SaveDialog;
import echoflux.core.core.bean.accessor.BeanAccessor;

import java.time.ZoneId;
import java.util.List;
import java.util.Set;

public class CreateUserDialog extends SaveDialog<Long> {

    private final AccessManagementEndpoint accessManagementEndpoint;
    private final Binder<InternalCommand> binder;

    public CreateUserDialog(BeanAccessor beanAccessor) {
        Guard.notNull(beanAccessor, "beanAccessor");

        this.accessManagementEndpoint = beanAccessor.get(AccessManagementEndpoint.class);
        this.binder = new Binder<>();
        this.binder.setBean(InternalCommand.builder().build());

        var usernameField = new TextField("Username");
        binder.forField(usernameField)
                .asRequired("Username is required")
                .bind(InternalCommand::getUsername, InternalCommand::setUsername);

        var nameField = new TextField("Name");
        binder.forField(nameField)
                .asRequired("Name is required")
                .bind(InternalCommand::getName, InternalCommand::setName);

        var passwordField = new PasswordField("Password");
        binder.forField(passwordField)
                .asRequired("Password is required")
                .bind(InternalCommand::getPassword, InternalCommand::setPassword);

        var confirmPasswordField = new PasswordField("Confirm Password");
        binder.forField(confirmPasswordField)
                .asRequired("Confirm Password is required")
                .withValidator(cp -> StringUtils.equals(cp, passwordField.getValue()), "Passwords do not match")
                .bind(InternalCommand::getPasswordConfirmation, InternalCommand::setPasswordConfirmation);

        var enabledField = new Checkbox("Enabled");
        binder.forField(enabledField)
                .bind(InternalCommand::getEnabled, InternalCommand::setEnabled);

        var zoneIdField = new ComboBox<ZoneId>("Zone ID");
        binder.forField(zoneIdField)
                .asRequired("Zone ID is required")
                .bind(InternalCommand::getZoneId, InternalCommand::setZoneId);

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
                .bind(InternalCommand::getCountry, InternalCommand::setCountry);

        var rolesField = new MultiSelectComboBox<RoleEntry>("Roles");
        rolesField.setItemLabelGenerator(entry -> {
            if (StringUtils.isNotBlank(entry.description())) {
                return "(%s) - %s".formatted(entry.name(), StringUtils.lowerCase(entry.description()));
            }

            return entry.name();
        });
        rolesField.setItems(beanAccessor.get(RoleEntryDataProvider.class));
        binder.forField(rolesField)
                .bind(InternalCommand::getRoles, InternalCommand::setRoles);

        var form = new FormLayout();
        form.add(usernameField, nameField, passwordField, confirmPasswordField, enabledField, countryField, zoneIdField, rolesField);

        add(form);
    }

    @SneakyThrows({ValidationException.class})
    @Override
    protected Long save() {
        binder.writeBean(binder.getBean());

        var bean = binder.getBean();
        var roleIds = MoreSets.collect(bean.getRoles(), RoleEntry::id);
        var command = CreateApplicationUserCommand.builder()
                .username(bean.getUsername())
                .name(bean.getName())
                .password(bean.getPassword())
                .passwordConfirmation(bean.getPasswordConfirmation())
                .enabled(bean.getEnabled())
                .country(bean.getCountry())
                .zoneId(bean.getZoneId())
                .roleIds(roleIds)
                .build();

        return accessManagementEndpoint.createApplicationUser(command);
    }

    @Override
    protected boolean validate() {
        return binder.validate().isOk();
    }

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    private static class InternalCommand {

        private String username;
        private String name;
        private String password;
        private String passwordConfirmation;
        private Boolean enabled;
        private Country country;
        private ZoneId zoneId;
        @Builder.Default
        private Set<RoleEntry> roles = Set.of();

    }

}
