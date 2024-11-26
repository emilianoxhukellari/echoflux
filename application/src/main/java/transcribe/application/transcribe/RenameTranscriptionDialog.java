package transcribe.application.transcribe;

import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.Validator;
import transcribe.application.core.jpa.dialog.save.JpaSaveDialog;
import transcribe.application.core.spring.SpringContext;
import transcribe.domain.transcription.data.TranscriptionEntity;
import transcribe.domain.transcription.service.RenameTranscriptionCommand;
import transcribe.domain.transcription.service.TranscriptionService;

public class RenameTranscriptionDialog extends JpaSaveDialog<TranscriptionEntity> {

    private final TranscriptionService service;
    private final Binder<RenameTranscriptionCommand> binder;

    public RenameTranscriptionDialog(TranscriptionEntity entity) {
        super(TranscriptionEntity.class);
        this.service = SpringContext.getBean(TranscriptionService.class);
        this.binder = new Binder<>(RenameTranscriptionCommand.class);

        this.binder.setBean(
                RenameTranscriptionCommand.builder()
                        .id(entity.getId())
                        .name(entity.getName())
                        .build()
        );

        var nameField = new TextField("Name");
        binder.forField(nameField)
                .asRequired("Name must not be blank")
                .withValidator(
                        Validator.from(
                                v -> !v.isEmpty(),
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

        add(form);

        setHeaderTitle("Rename transcription");
        setHeight("270px");
        setOperationCustomizer(o -> o.withName("Renaming transcription"));
    }

    @Override
    protected TranscriptionEntity save() {
        return service.rename(binder.getBean());
    }

    @Override
    protected boolean validate() {
        return binder.validate().isOk();
    }

}
