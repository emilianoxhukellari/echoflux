package echoflux.application.transcribe;

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
import echoflux.application.core.dialog.EnhancedDialog;
import echoflux.application.core.field.media.MediaField;
import echoflux.application.core.operation.Operation;
import echoflux.application.core.operation.OperationErrorImportance;
import echoflux.application.core.operation.OperationRunner;
import echoflux.application.core.operation.OperationSuccessImportance;
import echoflux.application.security.AuthenticatedUser;
import echoflux.application.transcribe.media_provider.MediaValue;
import echoflux.core.core.bean.loader.BeanLoader;
import echoflux.core.core.utils.EfEnums;
import echoflux.core.transcribe.common.Language;
import echoflux.domain.operation.data.OperationType;
import echoflux.domain.transcription.data.TranscriptionProjection;
import echoflux.domain.transcription.pipeline.TranscriptionPipeline;
import echoflux.domain.transcription.pipeline.TranscriptionPipelineCommand;

import java.util.Objects;

public class TranscribeDialog extends EnhancedDialog {

    private final TranscriptionPipeline transcriptionPipeline;
    private final OperationRunner operationRunner;
    private final AuthenticatedUser authenticatedUser;

    public TranscribeDialog(BeanLoader beanLoader) {
        Objects.requireNonNull(beanLoader, "beanLoader");

        this.transcriptionPipeline = beanLoader.load(TranscriptionPipeline.class);
        this.operationRunner = beanLoader.load(OperationRunner.class);
        this.authenticatedUser = beanLoader.load(AuthenticatedUser.class);

        var binder = new Binder<Command>();
        binder.setBean(
                Command.builder()
                        .build()
        );

        var mediaProviderField = new MediaField(beanLoader);
        binder.forField(mediaProviderField)
                .asRequired("Media is required")
                .bind(Command::getMediaValue, Command::setMediaValue);

        var language = new ComboBox<>("Language", Language.values());
        language.setClearButtonVisible(true);
        language.setItemLabelGenerator(EfEnums::toDisplayName);
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
