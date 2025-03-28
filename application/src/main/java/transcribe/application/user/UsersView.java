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
import transcribe.application.layout.MainLayout;
import transcribe.application.core.jpa.grid.JpaGrid;
import transcribe.domain.application_user.service.ApplicationUserService;
import transcribe.domain.operation.data.OperationType;

@PageTitle("Users")
@Route(value = "users", layout = MainLayout.class)
@RolesAllowed("ADMIN")
public class UsersView extends Composite<VerticalLayout> {

    public UsersView(ApplicationUserService service,
                     OperationRunner operationRunner) {
        var grid = new JpaGrid<>(ApplicationUserJpaDto.class);
        grid.addAllColumns();
        grid.setAllColumnsResizable();
        grid.setAllColumnsAutoWidth(true);
        grid.addAllFilters();

        grid.addContextMenuItem(
                "Edit",
                dto -> new UpdateUserDialog(dto)
                        .setSaveListener(_ -> grid.refreshAll())
                        .open()
        );
        grid.addItemDoubleClickListener(
                e -> new UpdateUserDialog(e.getItem())
                        .setSaveListener(grid::refreshItem)
                        .open()
        );
        grid.addConfirmedContextMenuItem("Delete", e -> {
            var operation = Operation.builder()
                    .name("Deleting application user")
                    .description("User with ID " + e.getId())
                    .callable(OperationCallable.ofRunnable(() -> service.deleteById(e.getId())))
                    .onSuccess(_ -> grid.refreshAll())
                    .type(OperationType.NON_BLOCKING)
                    .build();

            operationRunner.run(operation, UI.getCurrent());
        });
        grid.addContextMenuItem("Change password", dto -> new ChangePasswordDialog(dto).open());

        var jpaGridControls = new JpaGridControls<>(grid);
        jpaGridControls.addCreateEntityButton(
                () -> new CreateUserDialog()
                        .setSaveListener(_ -> grid.refreshAll())
                        .open()
        );

        getContent().addAndExpand(jpaGridControls);
    }

}
