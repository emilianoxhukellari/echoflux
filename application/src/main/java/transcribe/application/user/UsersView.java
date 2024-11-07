package transcribe.application.user;

import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.RolesAllowed;
import transcribe.application.core.jpa.grid.JpaGridControls;
import transcribe.application.core.operation.Operation;
import transcribe.application.core.operation.OperationCallable;
import transcribe.application.core.operation.OperationRunner;
import transcribe.application.main.MainLayout;
import transcribe.application.core.jpa.grid.JpaGrid;
import transcribe.domain.application_user.data.ApplicationUserEntity;
import transcribe.domain.application_user.data.ApplicationUserRepository;
import transcribe.domain.application_user.service.ApplicationUserService;
import transcribe.domain.operation.data.OperationType;

@PageTitle("Users")
@Route(value = "users", layout = MainLayout.class)
@RolesAllowed("ADMIN")
public class UsersView extends Composite<VerticalLayout> {

    public UsersView(ApplicationUserRepository repository,
                     ApplicationUserService service,
                     OperationRunner operationRunner) {
        var grid = new JpaGrid<>(ApplicationUserEntity.class, repository);

        grid.addCoreAttributeColumnsExcluding("password");
        grid.addAuditColumns();
        grid.addIdColumn();
        grid.setAllColumnsResizable();

        grid.addCoreAttributeFiltersExcluding("password");
        grid.addAuditFilters();
        grid.addIdFilter();

        grid.addContextMenuItem(
                "Edit",
                e -> new UpdateUserDialog(e)
                        .setSaveListener(grid::refreshAll)
                        .open()
        );
        grid.addItemDoubleClickListener(
                e -> new UpdateUserDialog(e.getItem())
                        .setSaveListener(grid::refreshAll)
                        .open()
        );
        grid.addConfirmedContextMenuItem("Delete", e -> {
            var operation = Operation.builder()
                    .name("Deleting application user")
                    .description("User with ID " + e.getId())
                    .callable(OperationCallable.ofRunnable(() -> service.delete(e.getId())))
                    .onSuccess(_ -> grid.refreshAll())
                    .type(OperationType.NON_BLOCKING)
                    .build();

            operationRunner.run(operation, UI.getCurrent());
        });
        grid.addContextMenuItem("Change password", e -> new ChangePasswordDialog(e).open());

        var jpaGridControls = new JpaGridControls<>(grid);
        jpaGridControls.addCreateEntityButton(
                () -> new CreateUserDialog()
                        .setSaveListener(grid::refreshAll)
                        .open()
        );

        getContent().addAndExpand(jpaGridControls);
    }

}
