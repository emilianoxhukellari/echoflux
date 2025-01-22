package transcribe.application.template;

import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.RolesAllowed;
import transcribe.application.core.jpa.grid.JpaGrid;
import transcribe.application.core.jpa.grid.JpaGridControls;
import transcribe.application.main.MainLayout;

@PageTitle("Templates")
@Route(value = "templates", layout = MainLayout.class)
@RolesAllowed("ADMIN")
public class TemplatesView extends Composite<VerticalLayout> {

    public TemplatesView() {
        var grid = new JpaGrid<>(TemplateJpaDto.class);
        grid.addAllColumns();
        grid.setAllColumnsResizable();
        grid.addAllFilters();
        grid.addCrudActions();

        getContent().addAndExpand(new JpaGridControls<>(grid));
    }

}
