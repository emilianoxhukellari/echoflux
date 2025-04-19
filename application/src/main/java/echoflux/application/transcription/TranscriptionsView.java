package echoflux.application.transcription;

import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.RolesAllowed;
import echoflux.application.core.jpa.grid.JpaGrid;
import echoflux.application.core.jpa.grid.JpaGridControls;
import echoflux.application.layout.MainLayout;
import echoflux.core.core.bean.loader.BeanLoader;

@PageTitle("Transcriptions")
@Route(value = "transcriptions", layout = MainLayout.class)
@RolesAllowed("ADMIN")
public class TranscriptionsView extends Composite<VerticalLayout> {

    public TranscriptionsView(BeanLoader beanLoader) {
        var grid = new JpaGrid<>(TranscriptionJpaDto.class, beanLoader);
        grid.addAllColumns();
        grid.setAllColumnsResizable();
        grid.addAllFilters();
        grid.addCrudActions();
        grid.addContextMenuItem("Open", t -> UI.getCurrent().navigate(TranscriptionView.class, t.getId()));

        getContent().addAndExpand(new JpaGridControls<>(grid));
    }

}
