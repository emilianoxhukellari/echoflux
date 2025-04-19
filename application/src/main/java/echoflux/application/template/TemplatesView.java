package echoflux.application.template;

import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.RolesAllowed;
import echoflux.application.core.jpa.grid.JpaGrid;
import echoflux.application.core.jpa.grid.JpaGridControls;
import echoflux.application.layout.MainLayout;
import echoflux.core.core.bean.loader.BeanLoader;

@PageTitle("Templates")
@Route(value = "templates", layout = MainLayout.class)
@RolesAllowed("ADMIN")
public class TemplatesView extends Composite<VerticalLayout> {

    public TemplatesView(BeanLoader beanLoader) {
        var grid = new JpaGrid<>(TemplateJpaDto.class, beanLoader);
        grid.addAllColumns();
        grid.setAllColumnsResizable();
        grid.addAllFilters();
        grid.addCrudActions();
        grid.setColumnWidth(TemplateJpaDto_.CONTENT, "400px");

        getContent().addAndExpand(new JpaGridControls<>(grid));
    }

}
