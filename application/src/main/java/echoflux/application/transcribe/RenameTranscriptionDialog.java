package echoflux.application.transcribe;

import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.Validator;
import echoflux.domain.transcription.data.TranscriptionProjection;
import org.apache.commons.lang3.StringUtils;
import echoflux.application.core.jpa.dialog.SaveDialog;
import echoflux.core.core.bean.loader.BeanLoader;
import echoflux.application.core.operation.OperationType;
import echoflux.domain.transcription.service.RenameTranscriptionCommand;
import echoflux.domain.transcription.service.TranscriptionService;

import java.util.Objects;

public class RenameTranscriptionDialog extends SaveDialog<Long> {

    private final TranscriptionService transcriptionService;
    private final Binder<RenameTranscriptionCommand> binder;

    public RenameTranscriptionDialog(TranscriptionProjection transcription, BeanLoader beanLoader) {
        Objects.requireNonNull(transcription, "transcription");
        Objects.requireNonNull(beanLoader, "beanLoader");

        this.transcriptionService = beanLoader.load(TranscriptionService.class);
        this.binder = new Binder<>();

        this.binder.setBean(
                RenameTranscriptionCommand.builder()
                        .id(transcription.getId())
                        .name(transcription.getName())
                        .build()
        );

        var nameField = new TextField("Name");
        binder.forField(nameField)
                .asRequired("Name must not be blank")
                .withValidator(
                        Validator.from(
                                StringUtils::isNotBlank,
                                "Name must have at least one non-whitespace character"
                        )
                )
                .withValidator(
                        Validator.from(
                                v -> v.length() <= 1024,
                                "Name must be at most 1024 characters"
                        )
                )
                .bind(RenameTranscriptionCommand::getName, RenameTranscriptionCommand::setName);

        var form = new FormLayout();
        form.add(nameField, 2);

        withContent(form);
        withTitle("Rename transcription");
        setModal(true);
        setHeight("270px");
        withOperationCustomizer(o -> o
                .withName("Renaming transcription")
                .withType(OperationType.BLOCKING)
        );
    }

    @Override
    protected Long save() {
        return transcriptionService.rename(binder.getBean()).getId();
    }

    @Override
    protected boolean validate() {
        return binder.validate().isOk();
    }

}
