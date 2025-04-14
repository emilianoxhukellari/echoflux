package transcribe.application.transcribe;

import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.Validator;
import org.apache.commons.lang3.StringUtils;
import transcribe.application.core.jpa.dialog.save.JpaSaveDialog;
import transcribe.application.core.jpa.dto.impl.SimpleJpaDtoService;
import transcribe.application.core.operation.OperationRunner;
import transcribe.application.transcription.TranscriptionJpaDto;
import transcribe.core.core.bean.loader.BeanLoader;
import transcribe.domain.operation.data.OperationType;
import transcribe.domain.transcription.data.TranscriptionEntity;
import transcribe.domain.transcription.service.RenameTranscriptionCommand;
import transcribe.domain.transcription.service.TranscriptionService;

import java.util.Objects;

public class RenameTranscriptionDialog extends JpaSaveDialog<TranscriptionJpaDto> {

    private final TranscriptionService transcriptionService;
    private final SimpleJpaDtoService<TranscriptionJpaDto, TranscriptionEntity, Long> simpleJpaDtoService;
    private final Binder<RenameTranscriptionCommand> binder;

    public RenameTranscriptionDialog(TranscriptionJpaDto transcription, BeanLoader beanLoader) {
        super(TranscriptionJpaDto.class, beanLoader.load(OperationRunner.class));
        Objects.requireNonNull(transcription, "transcription");
        Objects.requireNonNull(beanLoader, "beanLoader");

        this.transcriptionService = beanLoader.load(TranscriptionService.class);
        this.simpleJpaDtoService = new SimpleJpaDtoService<>(TranscriptionJpaDto.class, beanLoader);
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
        var renamed = transcriptionService.rename(binder.getBean());

        return simpleJpaDtoService.getById(renamed.id());
    }

    @Override
    protected boolean validate() {
        return binder.validate().isOk();
    }

}
