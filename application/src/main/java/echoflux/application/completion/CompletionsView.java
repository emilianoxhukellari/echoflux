package echoflux.application.completion;

import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.RolesAllowed;
import echoflux.application.core.jpa.grid.JpaGrid;
import echoflux.application.core.jpa.grid.JpaGridControls;
import echoflux.application.layout.MainLayout;
import echoflux.core.core.bean.loader.BeanLoader;

@PageTitle("Completions")
@Route(value = "completions", layout = MainLayout.class)
@RolesAllowed("ADMIN")
public class CompletionsView extends Composite<VerticalLayout> {

    public CompletionsView(BeanLoader beanLoader) {
        var grid = new JpaGrid<>(CompletionJpaDto.class, beanLoader);
        grid.addAllColumns();
        grid.setAllColumnsResizable();
        grid.addAllFilters();
        grid.addCrudActions();

        getContent().addAndExpand(new JpaGridControls<>(grid));
    }

}
