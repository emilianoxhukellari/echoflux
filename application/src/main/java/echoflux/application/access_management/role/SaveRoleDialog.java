package echoflux.application.access_management.role;

import com.vaadin.flow.component.combobox.MultiSelectComboBox;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.ValidationException;
import com.vaadin.flow.theme.lumo.LumoUtility;
import echoflux.application.access_management.data_provider.PermissionEntryDataProvider;
import echoflux.application.access_management.permission.PermissionEntry;
import echoflux.application.core.dialog.SaveDialog;
import echoflux.core.core.bean.accessor.BeanAccessor;
import echoflux.core.core.utils.MoreEnums;
import echoflux.core.core.utils.MoreSets;
import echoflux.core.core.validate.guard.Guard;
import echoflux.domain.access_management.endpoint.AccessManagementEndpoint;
import echoflux.domain.access_management.role.service.SaveRoleCommand;
import echoflux.domain.core.security.PermissionType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.SneakyThrows;
import org.apache.commons.lang3.StringUtils;

import java.util.Set;

public class SaveRoleDialog extends SaveDialog<Long> {

    private final Binder<InternalCommand> binder;
    private final AccessManagementEndpoint accessManagementEndpoint;

    public static SaveRoleDialog newCreate(BeanAccessor beanAccessor) {
        Guard.notNull(beanAccessor, "beanAccessor");

        var command = InternalCommand.builder().build();
        var accessManagementEndpoint = beanAccessor.get(AccessManagementEndpoint.class);
        var permissionEntryDataProvider = beanAccessor.get(PermissionEntryDataProvider.class);

        return new SaveRoleDialog(command, accessManagementEndpoint, permissionEntryDataProvider);
    }

    public static SaveRoleDialog newUpdate(Long roleId, BeanAccessor beanAccessor) {
        Guard.notNull(roleId, "roleId");
        Guard.notNull(beanAccessor, "beanAccessor");

        var accessManagementEndpoint = beanAccessor.get(AccessManagementEndpoint.class);
        var permissionEntryDataProvider = beanAccessor.get(PermissionEntryDataProvider.class);

        var role = accessManagementEndpoint.getRoleById(roleId);
        var permissionEntries = permissionEntryDataProvider.findEntriesByRoleId(roleId);
        var command = InternalCommand.builder()
                .id(role.getId())
                .name(role.getName())
                .description(role.getDescription())
                .permissions(permissionEntries)
                .build();

        return new SaveRoleDialog(command, accessManagementEndpoint, permissionEntryDataProvider);
    }

    private SaveRoleDialog(InternalCommand command,
                           AccessManagementEndpoint accessManagementEndpoint,
                           PermissionEntryDataProvider permissionEntryDataProvider) {
        Guard.notNull(command, "command");
        Guard.notNull(accessManagementEndpoint, "accessManagementEndpoint");
        Guard.notNull(permissionEntryDataProvider, "permissionEntryDataProvider");

        this.accessManagementEndpoint = accessManagementEndpoint;
        this.binder = new Binder<>();
        binder.setBean(command);

        var nameField = new TextField("Name");
        binder.forField(nameField)
                .asRequired("Name is required")
                .bind(InternalCommand::getName, InternalCommand::setName);

        var descriptionField = new TextArea("Description");
        binder.forField(descriptionField)
                .bind(InternalCommand::getDescription, InternalCommand::setDescription);

        var permissionsField = new MultiSelectComboBox<PermissionEntry>("Permissions");
        permissionsField.setItems(permissionEntryDataProvider);
        permissionsField.setItemLabelGenerator(entry -> {
            var permission = MoreEnums.toDisplayName(entry.type());

            if (StringUtils.isNotBlank(entry.description())) {
                return "(%s) - %s".formatted(permission, StringUtils.lowerCase(entry.description()));
            }

            return permission;
        });
        permissionsField.setClassNameGenerator(entry -> {
            if (entry.type() == PermissionType.ROOT) {
                return LumoUtility.TextColor.WARNING;
            }

            return null;
        });
        binder.forField(permissionsField)
                .bind(InternalCommand::getPermissions, InternalCommand::setPermissions);

        var form = new FormLayout();
        form.add(nameField, descriptionField, permissionsField);

        add(form);
        withTitle(command.getId() == null ? "Create Role" : "Update Role");
    }

    @SneakyThrows({ValidationException.class})
    @Override
    protected Long save() {
        binder.writeBean(binder.getBean());

        var bean = binder.getBean();
        var permissionIds = MoreSets.collect(bean.getPermissions(), PermissionEntry::id);
        var command = SaveRoleCommand.builder()
                .id(bean.getId())
                .name(bean.getName())
                .description(bean.getDescription())
                .permissionIds(permissionIds)
                .build();

        return accessManagementEndpoint.saveRole(command);
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

        private Long id;
        private String name;
        private String description;
        @Builder.Default
        private Set<PermissionEntry> permissions = Set.of();

    }

}
