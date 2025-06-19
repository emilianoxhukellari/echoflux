package echoflux.application.user;

import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import echoflux.application.core.jpa.grid.JpaGrid;
import echoflux.domain.application_user.data.ApplicationUserEntity_;
import echoflux.domain.application_user.data.ApplicationUserProjection;
import echoflux.domain.application_user.data.ApplicationUserRepository;
import jakarta.annotation.security.RolesAllowed;
import echoflux.application.core.operation.Operation;
import echoflux.application.core.operation.OperationCallable;
import echoflux.application.layout.MainLayout;
import echoflux.core.core.bean.loader.BeanLoader;
import echoflux.domain.application_user.service.ApplicationUserService;

@PageTitle("Users")
@Route(value = "users", layout = MainLayout.class)
@RolesAllowed("ADMIN")
public class UsersView extends VerticalLayout {

    public UsersView(ApplicationUserRepository applicationUserRepository,
                     ApplicationUserService applicationUserService,
                     BeanLoader beanLoader) {
        var grid = new JpaGrid<>(ApplicationUserProjection.class, applicationUserRepository);
        grid.addColumnWithFilter(ApplicationUserEntity_.NAME);
        grid.addColumnWithFilter(ApplicationUserEntity_.USERNAME);
        grid.addColumnWithFilter(ApplicationUserEntity_.ENABLED);
        grid.addColumnWithFilter(ApplicationUserEntity_.COUNTRY);
        grid.addColumnWithFilter(ApplicationUserEntity_.ZONE_ID);
        grid.addColumnWithFilter(ApplicationUserEntity_.ROLES);
        grid.addIdColumnWithFilter();
        grid.addAuditColumnsWithFilter();
        grid.addContextMenuItemWithDoubleClickListener(
                "Edit",
                item -> new UpdateUserDialog(item, beanLoader)
                        .withSaveListener(grid::refreshItemById)
                        .open()
        );
        grid.addConfirmContextMenuItem(
                "Delete",
                e -> Operation.builder()
                        .name("Deleting application user")
                        .callable(OperationCallable.ofRunnable(() -> applicationUserService.deleteById(e.getId())))
                        .onSuccess(_ -> grid.refreshAll())
                        .build()
                        .runBackground()
        );
        grid.addContextMenuItem(
                "Change password",
                dto -> new ChangePasswordDialog(dto, beanLoader).open()
        );

        var jpaGridControls = grid.withControls();
        jpaGridControls.addCreateButton(
                () -> new CreateUserDialog(beanLoader)
                        .withSaveListener(_ -> grid.refreshAll())
                        .open()
        );

        addAndExpand(jpaGridControls);
    }

}
