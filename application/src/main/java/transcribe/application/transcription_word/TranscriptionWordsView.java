package transcribe.application.transcription_word;

import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.RolesAllowed;
import transcribe.application.core.jpa.grid.JpaGrid;
import transcribe.application.core.jpa.grid.JpaGridControls;
import transcribe.application.layout.MainLayout;
import transcribe.core.core.bean.loader.BeanLoader;

@PageTitle("Transcription Words")
@Route(value = "transcription-words", layout = MainLayout.class)
@RolesAllowed("ADMIN")
public class TranscriptionWordsView extends Composite<VerticalLayout> {

    public TranscriptionWordsView(BeanLoader beanLoader) {
        var grid = new JpaGrid<>(TranscriptionWordJpaDto.class, beanLoader);
        grid.addAllColumns();
        grid.setAllColumnsResizable();
        grid.addAllFilters();
        grid.addCrudActions();

        getContent().addAndExpand(new JpaGridControls<>(grid));
    }

}
