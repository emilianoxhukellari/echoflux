package transcribe.application.transcribe;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.data.binder.Binder;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.vaadin.lineawesome.LineAwesomeIcon;
import transcribe.application.core.dialog.EnhancedDialog;
import transcribe.application.core.field.media.MediaField;
import transcribe.application.core.operation.Operation;
import transcribe.application.core.operation.OperationErrorImportance;
import transcribe.application.core.operation.OperationRunner;
import transcribe.application.core.operation.OperationSuccessImportance;
import transcribe.application.core.spring.SpringContext;
import transcribe.application.security.AuthenticatedUser;
import transcribe.application.transcribe.media_provider.MediaValue;
import transcribe.core.core.utils.TsEnums;
import transcribe.core.transcribe.common.Language;
import transcribe.domain.operation.data.OperationType;
import transcribe.domain.transcription.data.TranscriptionProjection;
import transcribe.domain.transcription.pipeline.TranscriptionPipeline;
import transcribe.domain.transcription.pipeline.TranscriptionPipelineCommand;

public class TranscribeDialog extends EnhancedDialog {

    private final TranscriptionPipeline transcriptionPipeline;
    private final OperationRunner operationRunner;
    private final AuthenticatedUser authenticatedUser;

    public TranscribeDialog() {
        this.transcriptionPipeline = SpringContext.getBean(TranscriptionPipeline.class);
        this.operationRunner = SpringContext.getBean(OperationRunner.class);
        this.authenticatedUser = SpringContext.getBean(AuthenticatedUser.class);

        var binder = new Binder<Command>();
        binder.setBean(
                Command.builder()
                        .build()
        );

        var mediaProviderField = new MediaField();
        binder.forField(mediaProviderField)
                .asRequired("Media is required")
                .bind(Command::getMediaValue, Command::setMediaValue);

        var language = new ComboBox<>("Language", Language.values());
        language.setClearButtonVisible(true);
        language.setItemLabelGenerator(TsEnums::toDisplayName);
        binder.forField(language)
                .asRequired("Language is required")
                .bind(Command::getLanguage, Command::setLanguage);

        var enhanced = new Checkbox("Enhance with AI");
        binder.forField(enhanced)
                .bind(Command::getEnhanced, Command::setEnhanced);

        var form = new FormLayout();
        form.add(mediaProviderField, 2);
        form.add(language, 2);
        form.add(enhanced, 2);

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
        setHeight("584px");
        setMaxWidth("95vw");
    }

    private void startTranscribe(Command command) {
        var pipelineCommand = TranscriptionPipelineCommand.builder()
                .name(command.getMediaValue().name())
                .sourceUri(command.getMediaValue().uri())
                .mediaOrigin(command.getMediaValue().mediaOrigin())
                .language(command.getLanguage())
                .applicationUserId(authenticatedUser.getId())
                .enhanced(command.getEnhanced())
                .build();

        var operation = Operation.<TranscriptionProjection>builder()
                .name(String.format("Transcribing \"%s\"", command.getMediaValue().name()))
                .beforeCall(this::close)
                .type(OperationType.NON_BLOCKING)
                .callable(() -> transcriptionPipeline.transcribe(pipelineCommand))
                .customSuccessMessage("Transcribed \"%s\"".formatted(command.getMediaValue().name()))
                .successImportance(OperationSuccessImportance.HIGH)
                .customErrorMessage("Transcription of \"%s\" failed".formatted(command.getMediaValue().name()))
                .errorImportance(OperationErrorImportance.NORMAL)
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
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    private static class Command {

        private MediaValue mediaValue;

        private Language language;

        @Builder.Default
        private Boolean enhanced = true;

    }

}
