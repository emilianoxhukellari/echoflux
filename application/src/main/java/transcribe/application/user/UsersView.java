package transcribe.application.user;

import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.RolesAllowed;
import transcribe.application.main.MainLayout;
import transcribe.application.core.jpa.grid.JpaGrid;
import transcribe.domain.application_user.data.ApplicationUserEntity;
import transcribe.domain.application_user.data.ApplicationUserRepository;

@PageTitle("Users")
@Route(value = "users", layout = MainLayout.class)
@RolesAllowed("ADMIN")
public class UsersView extends Composite<VerticalLayout> {

    public UsersView(ApplicationUserRepository repository) {
        var grid = new JpaGrid<>(ApplicationUserEntity.class, repository);
        grid.addCrudActions();

        grid.addCoreAttributeColumnsExcluding("password");
        grid.addAuditColumns();
        grid.setAllColumnsResizable();

        grid.addCoreAttributeFiltersExcluding("password");
        grid.addAuditFilters();

        getContent().addAndExpand(grid);
    }

}
