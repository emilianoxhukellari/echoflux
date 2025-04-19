package echoflux.application.home;

import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.timepicker.TimePicker;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.PermitAll;
import lombok.extern.slf4j.Slf4j;
import echoflux.annotation.core.AttributeOverride;
import echoflux.annotation.core.ParentProperty;
import echoflux.annotation.projection.AttributeProjectType;
import echoflux.application.core.field.duration.DurationField;
import echoflux.application.core.notification.Notifications;
import echoflux.application.layout.MainLayout;
import echoflux.application.security.AuthenticatedUser;
import echoflux.core.audio.ffmpeg.FFprobeWrapper;
import echoflux.core.audio.transcoder.AudioTranscoder;
import echoflux.core.cloud_storage.CloudStorage;
import echoflux.core.core.json.JsonMapper;
import echoflux.core.diarization.pyannote.client.PyannoteDiarizationClient;
import echoflux.domain.application_user.data.Role;
import echoflux.domain.template.service.TemplateService;
import echoflux.domain.transcription.data.TranscriptionRepository;
import echoflux.domain.transcription.manager.TranscriptionManager;
import echoflux.domain.transcription.service.TranscriptionService;

import java.util.Set;

@PageTitle("Home")
@Route(value = "", layout = MainLayout.class)
@PermitAll
@Slf4j
public class HomeView extends Composite<VerticalLayout> {

    public HomeView(TranscriptionService transcriptionService,
                    TranscriptionManager transcriptionManager,
                    AuthenticatedUser authenticatedUser,
                    AudioTranscoder audioTranscoder,
                    FFprobeWrapper ffprobeWrapper,
                    CloudStorage cloudStorage,
                    PyannoteDiarizationClient pyannoteDiarizationClient,
                    TemplateService templateService,
                    TranscriptionRepository transcriptionRepository,
                    JsonMapper jsonMapper) {

        var button = new Button("Click me");
        button.addClickListener(_ -> {
            var result = transcriptionRepository.findByIdEnhanced(10L, Transcription.class).get();

            Notifications.success(jsonMapper.toString(result));
        });

        var timePicker = new TimePicker();
        timePicker.setClearButtonVisible(true);
        timePicker.addValueChangeListener(e ->
                log.info("Time selected: {}", e.getValue()));

        var durationField = new DurationField("Duration");
        durationField.setClearButtonVisible(true);
        getContent().add(new VerticalLayout(durationField));
    }

    private interface Transcription {

        Long getId();

        String getName();

        @ParentProperty
        ApplicationUser getApplicationUser();

    }

    private interface ApplicationUser {

        Long getId();

        String getUsername();

        @AttributeOverride(projectType = AttributeProjectType.DEFAULT)
        Set<Role> getRoles();

    }

    private interface Info {

        Long getId();

        String getAge();

        String getGender();

    }


}
