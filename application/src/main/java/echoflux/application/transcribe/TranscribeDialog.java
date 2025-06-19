package echoflux.application.transcribe;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.data.binder.Binder;
import echoflux.application.security.AuthenticatedUser;
import echoflux.domain.transcription.data.ScalarTranscriptionProjection;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.vaadin.lineawesome.LineAwesomeIcon;
import echoflux.application.core.dialog.EnhancedDialog;
import echoflux.application.core.field.media.MediaField;
import echoflux.application.core.operation.Operation;
import echoflux.application.core.operation.OperationErrorImportance;
import echoflux.application.core.operation.OperationSuccessImportance;
import echoflux.application.transcribe.media_provider.MediaValue;
import echoflux.core.core.bean.loader.BeanLoader;
import echoflux.core.core.utils.MoreEnums;
import echoflux.core.transcribe.Language;
import echoflux.domain.transcription.pipeline.TranscriptionPipeline;
import echoflux.domain.transcription.pipeline.TranscriptionPipelineCommand;

import java.util.Objects;

public class TranscribeDialog extends EnhancedDialog {

    private final TranscriptionPipeline transcriptionPipeline;

    public TranscribeDialog(BeanLoader beanLoader) {
        Objects.requireNonNull(beanLoader, "beanLoader");

        this.transcriptionPipeline = beanLoader.load(TranscriptionPipeline.class);

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
        language.setItemLabelGenerator(MoreEnums::toDisplayName);
        binder.forField(language)
                .asRequired("Language is required")
                .bind(Command::getLanguage, Command::setLanguage);

        var form = new FormLayout();
        form.add(mediaProviderField, 2);
        form.add(language, 2);

        var transcribeButton = newTranscribeButton();
        transcribeButton.addClickListener(_ -> {
            if (binder.writeBeanIfValid(binder.getBean())) {
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
                .applicationUserId(AuthenticatedUser.getId())
                .build();

        Operation.<ScalarTranscriptionProjection>builder()
                .name(String.format("Transcribing \"%s\"", command.getMediaValue().name()))
                .beforeCall(this::close)
                .callable(() -> transcriptionPipeline.transcribe(pipelineCommand))
                .customSuccessMessage("Transcribed \"%s\"".formatted(command.getMediaValue().name()))
                .successImportance(OperationSuccessImportance.HIGH)
                .customErrorMessage("Transcription of \"%s\" failed".formatted(command.getMediaValue().name()))
                .errorImportance(OperationErrorImportance.NORMAL)
                .onProgressNotify(false)
                .build()
                .runBackground();
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

    }

}
