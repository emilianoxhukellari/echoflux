package transcribe.application.completion;

import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.RolesAllowed;
import transcribe.application.core.jpa.grid.JpaGrid;
import transcribe.application.core.jpa.grid.JpaGridControls;
import transcribe.application.layout.MainLayout;

@PageTitle("Completions")
@Route(value = "completions", layout = MainLayout.class)
@RolesAllowed("ADMIN")
public class CompletionsView extends Composite<VerticalLayout> {

    public CompletionsView() {
        var grid = new JpaGrid<>(CompletionJpaDto.class);
        grid.addAllColumns();
        grid.setAllColumnsResizable();
        grid.addAllFilters();
        grid.addCrudActions();

        getContent().addAndExpand(new JpaGridControls<>(grid));
    }

}
