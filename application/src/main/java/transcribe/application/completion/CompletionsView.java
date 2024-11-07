package transcribe.application.completion;

import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.RolesAllowed;
import transcribe.application.core.jpa.grid.JpaGrid;
import transcribe.application.core.jpa.grid.JpaGridControls;
import transcribe.application.main.MainLayout;
import transcribe.domain.completion.data.CompletionEntity;
import transcribe.domain.completion.data.CompletionRepository;

@PageTitle("Completions")
@Route(value = "completions", layout = MainLayout.class)
@RolesAllowed("ADMIN")
public class CompletionsView extends Composite<VerticalLayout> {

    public CompletionsView(CompletionRepository repository) {
        var grid = new JpaGrid<>(CompletionEntity.class, repository);
        grid.addAllColumns();
        grid.setAllColumnsResizable();
        grid.addAllFilters();
        grid.addCrudActions();

        getContent().addAndExpand(new JpaGridControls<>(grid));
    }

}
