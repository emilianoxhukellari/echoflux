package echoflux.application.transcribe;

import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.Validator;
import echoflux.core.core.validate.guard.Guard;
import echoflux.domain.transcription.endpoint.TranscriptionEndpoint;
import org.apache.commons.lang3.StringUtils;
import echoflux.application.core.dialog.SaveDialog;
import echoflux.core.core.bean.accessor.BeanAccessor;
import echoflux.application.core.operation.OperationType;
import echoflux.domain.transcription.service.RenameTranscriptionCommand;

public class RenameTranscriptionDialog extends SaveDialog<Long> {

    private final TranscriptionEndpoint transcriptionEndpoint;
    private final Binder<RenameTranscriptionCommand> binder;

    public RenameTranscriptionDialog(Long transcriptionId, BeanAccessor beanAccessor) {
        Guard.notNull(transcriptionId, "transcriptionId");
        Guard.notNull(beanAccessor, "beanAccessor");

        this.transcriptionEndpoint = beanAccessor.get(TranscriptionEndpoint.class);
        this.binder = new Binder<>();

        var transcription = transcriptionEndpoint.getTranscriptionById(transcriptionId);
        var bean = RenameTranscriptionCommand.builder()
                .id(transcription.getId())
                .name(transcription.getName())
                .build();
        binder.setBean(bean);

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
        return transcriptionEndpoint.renameTranscription(binder.getBean());
    }

    @Override
    protected boolean validate() {
        return binder.validate().isOk();
    }

}
