package transcribe.application.transcribe;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.data.binder.Binder;
import lombok.Data;
import lombok.experimental.Accessors;
import org.vaadin.lineawesome.LineAwesomeIcon;
import transcribe.application.core.dialog.EnhancedDialog;
import transcribe.application.core.notification.Notifications;
import transcribe.application.core.operation.Operation;
import transcribe.application.core.operation.OperationRunner;
import transcribe.application.core.spring.SpringContext;
import transcribe.application.security.AuthenticatedUser;
import transcribe.application.core.field.MediaField;
import transcribe.application.transcribe.media_provider.MediaValue;
import transcribe.core.transcribe.common.Language;
import transcribe.core.transcribe.common.TranscribeResult;
import transcribe.domain.application_user.data.ApplicationUserEntity;
import transcribe.domain.operation.data.OperationType;
import transcribe.domain.transcription.pipeline.TranscriptionPipeline;
import transcribe.domain.transcription.pipeline.TranscriptionPipelineCommand;

import java.util.Optional;

public class TranscribeDialog extends EnhancedDialog {

    private final TranscriptionPipeline transcriptionPipeline;
    private final OperationRunner operationRunner;
    private final AuthenticatedUser authenticatedUser;

    public TranscribeDialog() {
        this.transcriptionPipeline = SpringContext.getBean(TranscriptionPipeline.class);
        this.operationRunner = SpringContext.getBean(OperationRunner.class);
        this.authenticatedUser = SpringContext.getBean(AuthenticatedUser.class);

        var binder = new Binder<Command>();
        binder.setBean(new Command());

        var mediaProviderField = new MediaField();
        binder.forField(mediaProviderField)
                .asRequired("Media is required")
                .bind(Command::getMediaValue, Command::setMediaValue);

        var language = new ComboBox<>("Language", Language.values());
        language.setClearButtonVisible(true);
        language.setItemLabelGenerator(Language::getDisplayName);
        binder.forField(language)
                .asRequired("Language is required")
                .bind(Command::getLanguage, Command::setLanguage);

        var form = new FormLayout();
        form.add(mediaProviderField, 2);
        form.add(language, 2);

        var transcribeButton = newTranscribeButton();
        transcribeButton.addClickListener(_ -> {
            if (binder.validate().isOk()) {
                startTranscribe(binder.getBean());
            }
        });

        binder.addValueChangeListener(_ -> {
            if (binder.isValid()) {
                transcribeButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
            } else {
                transcribeButton.removeThemeVariants(ButtonVariant.LUMO_PRIMARY);
            }
        });

        addCloseButtonListener(mediaProviderField::clearAndCleanup);

        getFooter().add(newTranscribeButtonContainer(transcribeButton));
        add(form);
        setHeaderTitle("Media source");
        setModal(true);
        setDraggable(false);
        setResizable(false);
        setCloseOnEsc(false);
        setWidth("500px");
        setHeight("560px");
    }

    private void startTranscribe(Command command) {
        var pipelineCommand = TranscriptionPipelineCommand.builder()
                .name(command.getMediaValue().name())
                .mediaUri(command.getMediaValue().uri())
                .mediaOrigin(command.getMediaValue().mediaOrigin())
                .language(command.getLanguage())
                .applicationUserId(authenticatedUser.find().map(ApplicationUserEntity::getId).orElse(null))
                .build();

        var operation = Operation.<Optional<TranscribeResult>>builder()
                .name(String.format("Transcribing \"%s\"", command.getMediaValue().name()))
                .beforeCall(this::close)
                .type(OperationType.NON_BLOCKING)
                .callable(() -> transcriptionPipeline.transcribe(pipelineCommand))
                .onSuccess(r -> {
                    if (r.isPresent()) {
                        Notifications.success(
                                String.format("Transcribed \"%s\"", command.getMediaValue().name()),
                                Notification.Position.TOP_CENTER,
                                5000
                        );
                    } else {
                        Notifications.newNotification(
                                String.format("Transcription of \"%s\" failed", command.getMediaValue().name()),
                                Notification.Position.TOP_END,
                                4000,
                                NotificationVariant.LUMO_ERROR
                        );
                    }
                })
                .onSuccessNotify(false)
                .onProgressNotify(false)

                .build();

        operationRunner.run(operation, UI.getCurrent());
    }

    private static Button newTranscribeButton() {
        var button = new Button("TRANSCRIBE", LineAwesomeIcon.PODCAST_SOLID.create());
        button.addThemeVariants(ButtonVariant.LUMO_LARGE);
        button.setHeight("50px");
        button.setWidth("65%");

        return button;
    }

    private static HorizontalLayout newTranscribeButtonContainer(Button button) {
        var buttonContainer = new HorizontalLayout(button);
        buttonContainer.setWidthFull();
        buttonContainer.setAlignItems(FlexComponent.Alignment.CENTER);
        buttonContainer.setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER);

        return buttonContainer;
    }

    @Data
    @Accessors(chain = true)
    private static class Command {

        private MediaValue mediaValue;
        private Language language;

    }

}
