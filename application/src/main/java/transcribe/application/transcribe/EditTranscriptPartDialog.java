package transcribe.application.transcribe;

import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.Validator;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DurationFormatUtils;
import transcribe.application.core.component.AudioSegmentPlayer;
import transcribe.application.core.jpa.dialog.save.JpaSaveDialog;
import transcribe.domain.operation.data.OperationType;


public class EditTranscriptPartDialog extends JpaSaveDialog<Void> {

    private final Binder<Void> binder = new Binder<>();

    //todo: change to dto after adding word functionality
    public EditTranscriptPartDialog(Long transcriptionId, Integer transcriptPartSequence, String audioSrc) {
        super(Void.class);

        var contentField = new TextArea("Content");
        binder.forField(contentField)
                .withValidator(
                        Validator.from(
                                StringUtils::isNotBlank,
                                "Content must have at least one non-whitespace character"
                        )
                )
                .withValidator(
                        Validator.from(
                                v -> v.length() <= 10000,
                                "Content must be at most 10000 characters"
                        )
                );

        var form = new FormLayout();
        form.add(contentField, 2);

        var fromTime = DurationFormatUtils.formatDuration(0L, "HH:mm:ss");
        var toTime = DurationFormatUtils.formatDuration(0L, "HH:mm:ss");

        var audioPlayer = new AudioSegmentPlayer(
                audioSrc,
                0L,
                0L
        );

        add(form, audioPlayer);
        withTitle("Edit %s - %s".formatted(fromTime, toTime));
        setModal(true);
        setOperationCustomizer(
                o -> o
                        .withName("Editing transcript part")
                        .withType(OperationType.BLOCKING)
                        .withCustomSuccessMessage("Edited transcript part successfully")
        );
        setWidth("700px");
        setMaxWidth("95vw");
    }

    @Override
    protected Void save() {
        return null;
    }

    @Override
    protected boolean validate() {
        return binder.validate().isOk();
    }

}
