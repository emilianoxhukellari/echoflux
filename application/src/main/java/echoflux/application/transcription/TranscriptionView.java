package echoflux.application.transcription;

import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.BeforeEvent;
import com.vaadin.flow.router.HasDynamicTitle;
import com.vaadin.flow.router.HasUrlParameter;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.PermitAll;
import echoflux.application.core.component.AudioTextConnector;
import echoflux.application.layout.MainLayout;
import echoflux.core.core.bean.loader.BeanLoader;
import echoflux.domain.transcription.data.TranscriptionProjection;
import echoflux.domain.transcription.service.TranscriptionService;


@Route(value = "transcription", layout = MainLayout.class)
@PermitAll
public class TranscriptionView extends Composite<VerticalLayout> implements HasUrlParameter<Long>, HasDynamicTitle {

    private final TranscriptionService transcriptionService;
    private final BeanLoader beanLoader;

    private TranscriptionProjection transcription;

    public TranscriptionView(TranscriptionService transcriptionService, BeanLoader beanLoader) {
        this.transcriptionService = transcriptionService;
        this.beanLoader = beanLoader;
    }

    @Override
    public void setParameter(BeforeEvent event, Long parameter) {
        this.transcription = transcriptionService.projectById(parameter);

        var audioTextConnector = new AudioTextConnector(transcription.id(), beanLoader);
        audioTextConnector.setHeightFull();
        audioTextConnector.setMaxWidth("1500px");

        var vl = getContent();
        vl.setSizeFull();
        vl.setAlignItems(FlexComponent.Alignment.CENTER);
        vl.add(audioTextConnector);
    }

    @Override
    public String getPageTitle() {
        return transcription.name();
    }

}
