package echoflux.application.completion;

import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import echoflux.application.core.jpa.grid.JpaGrid;
import echoflux.domain.application_user.data.ApplicationUserEntity_;
import echoflux.domain.completion.data.CompletionEntity_;
import echoflux.domain.completion.data.CompletionProjection;
import echoflux.domain.completion.data.CompletionRepository;
import echoflux.domain.transcription.data.TranscriptionEntity_;
import jakarta.annotation.security.RolesAllowed;
import echoflux.application.layout.MainLayout;

@PageTitle("Completions")
@Route(value = "completions", layout = MainLayout.class)
@RolesAllowed("ADMIN")
public class CompletionsView extends VerticalLayout {

    public CompletionsView(CompletionRepository completionRepository) {
        var grid = new JpaGrid<>(CompletionProjection.class, completionRepository);
        grid.addColumnWithFilter(CompletionEntity_.INPUT);
        grid.addColumnWithFilter(CompletionEntity_.OUTPUT);
        grid.addColumnWithFilter(CompletionEntity_.INPUT_TOKENS);
        grid.addColumnWithFilter(CompletionEntity_.OUTPUT_TOKENS);
        grid.addColumnWithFilter(CompletionEntity_.MODEL);
        grid.addColumnWithFilter(CompletionEntity_.TEMPERATURE);
        grid.addColumnWithFilter(CompletionEntity_.TOP_P);
        grid.addColumnWithFilter(CompletionEntity_.STATUS);
        grid.addColumnWithFilter(CompletionEntity_.DURATION);
        grid.addColumnWithFilter(CompletionEntity_.ERROR);
        grid.addColumnWithFilter(CompletionEntity_.TRANSCRIPTION, TranscriptionEntity_.ID);
        grid.addColumnWithFilter(CompletionEntity_.TRANSCRIPTION, TranscriptionEntity_.NAME);
        grid.addColumnWithFilter(CompletionEntity_.TRANSCRIPTION, TranscriptionEntity_.APPLICATION_USER, ApplicationUserEntity_.ID);
        grid.addColumnWithFilter(CompletionEntity_.TRANSCRIPTION, TranscriptionEntity_.APPLICATION_USER, ApplicationUserEntity_.USERNAME);
        grid.addColumnWithFilter(CompletionEntity_.TRANSCRIPTION, TranscriptionEntity_.APPLICATION_USER, ApplicationUserEntity_.NAME);
        grid.addIdColumnWithFilter();
        grid.addAuditColumnsWithFilter();

        addAndExpand(grid.withControls());
    }

}
