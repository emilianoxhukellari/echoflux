package echoflux.application.operation;

import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.RolesAllowed;
import echoflux.application.core.jpa.grid.JpaGrid;
import echoflux.application.core.jpa.grid.JpaGridControls;
import echoflux.application.layout.MainLayout;
import echoflux.core.core.bean.loader.BeanLoader;

@PageTitle("Operations")
@Route(value = "operations", layout = MainLayout.class)
@RolesAllowed("ADMIN")
public class OperationsView extends Composite<VerticalLayout> {

    public OperationsView(BeanLoader beanLoader) {
        var grid = new JpaGrid<>(OperationJpaDto.class, beanLoader);
        grid.addAllColumns();
        grid.setAllColumnsResizable();
        grid.addAllFilters();
        grid.addCrudActions();

        getContent().addAndExpand(new JpaGridControls<>(grid));
    }

}
