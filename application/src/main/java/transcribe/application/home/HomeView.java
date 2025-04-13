package transcribe.application.home;

import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.timepicker.TimePicker;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.PermitAll;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.support.TransactionTemplate;
import transcribe.annotation.core.AttributeOverride;
import transcribe.annotation.core.ParentProperty;
import transcribe.annotation.projection.AttributeProjectType;
import transcribe.application.core.field.duration.DurationField;
import transcribe.application.core.jpa.dto.impl.SimpleJpaDtoService;
import transcribe.application.core.notification.Notifications;
import transcribe.application.layout.MainLayout;
import transcribe.application.security.AuthenticatedUser;
import transcribe.application.transcription.TranscriptionJpaDto;
import transcribe.core.audio.ffmpeg.FFprobeWrapper;
import transcribe.core.audio.transcoder.AudioTranscoder;
import transcribe.core.cloud_storage.CloudStorage;
import transcribe.core.core.json.JsonMapper;
import transcribe.core.diarization.pyannote.client.PyannoteDiarizationClient;
import transcribe.domain.application_user.data.Role;
import transcribe.domain.template.service.TemplateService;
import transcribe.domain.transcription.data.TranscriptionEntity;
import transcribe.domain.transcription.data.TranscriptionRepository;
import transcribe.domain.transcription.manager.TranscriptionManager;
import transcribe.domain.transcription.service.TranscriptionService;

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
                    TransactionTemplate transactionTemplate,
                    PyannoteDiarizationClient pyannoteDiarizationClient,
                    TemplateService templateService,
                    TranscriptionRepository transcriptionRepository,
                    JsonMapper jsonMapper) {

        var service = SimpleJpaDtoService.
                <TranscriptionJpaDto, TranscriptionEntity, Long>ofBeanType(TranscriptionJpaDto.class);

        var button = new Button("Click me");
        transactionTemplate.setReadOnly(false);
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
