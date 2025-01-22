package transcribe.application.transcribe;

import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.Validator;
import org.apache.commons.lang3.StringUtils;
import transcribe.application.core.jpa.dialog.save.JpaSaveDialog;
import transcribe.application.core.jpa.dto.impl.SimpleJpaDtoService;
import transcribe.application.core.spring.SpringContext;
import transcribe.application.transcription.TranscriptionJpaDto;
import transcribe.domain.operation.data.OperationType;
import transcribe.domain.transcription.service.RenameTranscriptionCommand;
import transcribe.domain.transcription.service.TranscriptionService;

public class RenameTranscriptionDialog extends JpaSaveDialog<TranscriptionJpaDto> {

    private final TranscriptionService service;
    private final Binder<RenameTranscriptionCommand> binder;

    public RenameTranscriptionDialog(TranscriptionJpaDto transcription) {
        super(TranscriptionJpaDto.class);
        this.service = SpringContext.getBean(TranscriptionService.class);
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
        setOperationCustomizer(o -> o
                .withName("Renaming transcription")
                .withCustomSuccessMessage("Transcription renamed")
                .withType(OperationType.BLOCKING)
        );
    }

    @Override
    protected TranscriptionJpaDto save() {
        return SimpleJpaDtoService.ofBeanType(TranscriptionJpaDto.class)
                .perform(() -> service.rename(binder.getBean()));
    }

    @Override
    protected boolean validate() {
        return binder.validate().isOk();
    }

}
