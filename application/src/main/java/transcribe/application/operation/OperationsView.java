package transcribe.application.operation;

import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.RolesAllowed;
import transcribe.application.core.jpa.grid.JpaGrid;
import transcribe.application.core.jpa.grid.JpaGridControls;
import transcribe.application.main.MainLayout;

@PageTitle("Operations")
@Route(value = "operations", layout = MainLayout.class)
@RolesAllowed("ADMIN")
public class OperationsView extends Composite<VerticalLayout> {

    public OperationsView() {
        var grid = new JpaGrid<>(OperationJpaDto.class);
        grid.addAllColumns();
        grid.setAllColumnsResizable();
        grid.addAllFilters();
        grid.addCrudActions();

        getContent().addAndExpand(new JpaGridControls<>(grid));
    }

}
