package transcribe.application.transcribe;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.popover.Popover;
import com.vaadin.flow.component.popover.PopoverPosition;
import com.vaadin.flow.component.popover.PopoverVariant;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.virtuallist.VirtualList;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.Validator;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DurationFormatUtils;
import org.vaadin.lineawesome.LineAwesomeIcon;
import transcribe.application.core.component.AudioSegmentPlayer;
import transcribe.application.core.jpa.dialog.save.JpaSaveDialog;
import transcribe.application.core.spring.SpringContext;
import transcribe.core.core.utils.MoreEnums;
import transcribe.domain.operation.data.OperationType;
import transcribe.domain.transcript.transcript_part.service.TranscriptPartService;
import transcribe.domain.transcript.transcript_part_text.data.TranscriptPartTextEntity;
import transcribe.domain.transcript.transcript_part_text.data.TranscriptPartTextType;
import transcribe.domain.transcript.transcript_part_text.service.AddTranscriptPartTextCommand;
import transcribe.domain.transcript.transcript_part_text.service.TranscriptPartTextService;

import java.util.Comparator;

public class EditTranscriptPartDialog extends JpaSaveDialog<TranscriptPartTextEntity> {

    private final TranscriptPartTextService transcriptPartTextService;
    private final Binder<AddTranscriptPartTextCommand> binder;

    //todo: change to dto after adding word functionality
    public EditTranscriptPartDialog(Long transcriptionId, Integer transcriptPartSequence, String audioSrc) {
        super(TranscriptPartTextEntity.class);
        this.transcriptPartTextService = SpringContext.getBean(TranscriptPartTextService.class);
        this.binder = new Binder<>();

        var transcriptPart = SpringContext.getBean(TranscriptPartService.class)
                .getForTranscriptionAndSequence(transcriptionId, transcriptPartSequence);

        var addTranscriptPartTextCommand = AddTranscriptPartTextCommand.builder()
                .transcriptPartId(transcriptPart.getId())
                .content(transcriptPart.getLatestTextEntity().getContent())
                .type(TranscriptPartTextType.USER)
                .build();

        binder.setBean(addTranscriptPartTextCommand);

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
                )
                .bind(AddTranscriptPartTextCommand::getContent, AddTranscriptPartTextCommand::setContent);

        var form = new FormLayout();
        form.add(contentField, 2);

        var fromTime = DurationFormatUtils.formatDuration(transcriptPart.getStartOffsetMillis(), "HH:mm:ss");
        var toTime = DurationFormatUtils.formatDuration(transcriptPart.getEndOffsetMillis(), "HH:mm:ss");

        var rollbackButton = newRollbackButton();
        var rollbackLayout = new HorizontalLayout(rollbackButton);
        rollbackLayout.setWidthFull();
        rollbackLayout.setJustifyContentMode(FlexComponent.JustifyContentMode.END);

        var audioPlayer = new AudioSegmentPlayer(
                audioSrc,
                transcriptPart.getStartOffsetMillis(),
                transcriptPart.getEndOffsetMillis()
        );

        add(rollbackLayout, form, audioPlayer);
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
    protected TranscriptPartTextEntity save() {
        return SpringContext.getBean(TranscriptPartTextService.class)
                .add(binder.getBean());
    }

    @Override
    protected boolean validate() {
        return binder.validate().isOk();
    }

    private Button newRollbackButton() {
        var button = new Button("Rollback", LineAwesomeIcon.UNDO_ALT_SOLID.create());

        var popover = new Popover();
        popover.setTarget(button);
        popover.setPosition(PopoverPosition.END);
        popover.addThemeVariants(PopoverVariant.ARROW);

        var virtualList = new VirtualList<TranscriptPartTextEntity>();
        var rollbacks = transcriptPartTextService.getAllRollbacksForTranscriptPart(binder.getBean().getTranscriptPartId())
                .stream()
                .sorted(Comparator.comparing(TranscriptPartTextEntity::getVersion).reversed())
                .toList();
        virtualList.setItems(rollbacks);
        virtualList.setRenderer(new ComponentRenderer<>(this::newRollbackItem));
        virtualList.setWidth("400px");

        popover.add(virtualList);

        return button;
    }

    private HorizontalLayout newRollbackItem(TranscriptPartTextEntity entity) {
        var content = new TextArea(MoreEnums.toDisplayName(entity.getType()));
        content.setValue(entity.getContent());
        content.setReadOnly(true);
        content.setMinWidth("300px");
        content.setMaxWidth("300px");

        var useButton = new Button(LineAwesomeIcon.UNDO_ALT_SOLID.create());
        useButton.addClickListener(_ -> {
            binder.getBean().setContent(entity.getContent());
            binder.readBean(binder.getBean());
        });
        useButton.getStyle().set("margin-top", "35px");

        var hl = new HorizontalLayout(content, useButton);
        hl.setAlignItems(FlexComponent.Alignment.CENTER);
        hl.setJustifyContentMode(FlexComponent.JustifyContentMode.BETWEEN);
        hl.setPadding(true);

        return hl;
    }

}
