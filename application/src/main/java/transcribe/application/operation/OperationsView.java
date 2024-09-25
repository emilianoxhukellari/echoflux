package transcribe.application.operation;

import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.RolesAllowed;
import transcribe.application.core.jpa.grid.JpaGrid;
import transcribe.application.core.jpa.grid.JpaGridControls;
import transcribe.application.main.MainLayout;
import transcribe.domain.operation.data.OperationEntity;
import transcribe.domain.operation.data.OperationRepositoryJpa;

@PageTitle("Operations")
@Route(value = "operations", layout = MainLayout.class)
@RolesAllowed("ADMIN")
public class OperationsView extends Composite<VerticalLayout> {

    public OperationsView(OperationRepositoryJpa repository) {
        var grid = new JpaGrid<>(OperationEntity.class, repository);

        grid.addCoreAttributeColumns();
        grid.addAuditColumns();
        grid.addIdColumn();

        grid.setAllColumnsResizable();

        grid.addCoreAttributeFilters();
        grid.addAuditFilters();
        grid.addIdFilter();

        grid.addCrudActionsExcluding("durationSeconds");

        getContent().addAndExpand(new JpaGridControls<>(grid));
    }

}
