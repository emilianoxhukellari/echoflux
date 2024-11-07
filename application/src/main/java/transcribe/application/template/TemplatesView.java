package transcribe.application.template;

import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.RolesAllowed;
import transcribe.application.core.jpa.grid.JpaGrid;
import transcribe.application.core.jpa.grid.JpaGridControls;
import transcribe.application.main.MainLayout;
import transcribe.domain.template.data.TemplateEntity;
import transcribe.domain.template.data.TemplateRepository;

@PageTitle("Templates")
@Route(value = "templates", layout = MainLayout.class)
@RolesAllowed("ADMIN")
public class TemplatesView extends Composite<VerticalLayout> {

    public TemplatesView(TemplateRepository repository) {
        var grid = new JpaGrid<>(TemplateEntity.class, repository);
        grid.addAllColumns();
        grid.setAllColumnsResizable();
        grid.addAllFilters();
        grid.addCrudActions();

        getContent().addAndExpand(new JpaGridControls<>(grid));
    }

}
