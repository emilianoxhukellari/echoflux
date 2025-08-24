package echoflux.application.transcription;

import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.BeforeEvent;
import com.vaadin.flow.router.HasDynamicTitle;
import com.vaadin.flow.router.HasUrlParameter;
import com.vaadin.flow.router.Route;
import echoflux.domain.core.security.PermissionType;
import echoflux.domain.core.security.RequiredPermissions;
import echoflux.domain.jooq.tables.pojos.Transcription;
import echoflux.application.core.component.AudioTextConnector;
import echoflux.application.layout.MainLayout;
import echoflux.core.core.bean.accessor.BeanAccessor;
import echoflux.domain.transcription.service.TranscriptionService;

@Route(value = "transcription", layout = MainLayout.class)
@RequiredPermissions(PermissionType.TRANSCRIPTION_VIEW)
public class TranscriptionView extends Composite<VerticalLayout> implements HasUrlParameter<Long>, HasDynamicTitle {

    private final TranscriptionService transcriptionService;
    private final BeanAccessor beanAccessor;

    private Transcription transcription;

    public TranscriptionView(TranscriptionService transcriptionService, BeanAccessor beanAccessor) {
        this.transcriptionService = transcriptionService;
        this.beanAccessor = beanAccessor;
    }

    @Override
    public void setParameter(BeforeEvent event, Long parameter) {
        this.transcription = transcriptionService.getById(parameter);

        var audioTextConnector = new AudioTextConnector(transcription, beanAccessor);
        audioTextConnector.setHeightFull();
        audioTextConnector.setMaxWidth("1500px");

        var vl = getContent();
        vl.setSizeFull();
        vl.setAlignItems(FlexComponent.Alignment.CENTER);
        vl.add(audioTextConnector);
    }

    @Override
    public String getPageTitle() {
        return transcription.getName();
    }

}
