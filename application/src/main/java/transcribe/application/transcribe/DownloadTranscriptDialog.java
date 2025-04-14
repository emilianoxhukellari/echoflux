package transcribe.application.transcribe;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.server.StreamResource;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.function.Failable;
import transcribe.application.core.component.HelperDownloadAnchor;
import transcribe.application.core.dialog.EnhancedDialog;
import transcribe.application.core.operation.Operation;
import transcribe.application.core.operation.OperationRunner;
import transcribe.core.core.bean.loader.BeanLoader;
import transcribe.core.core.utils.TsEnums;
import transcribe.core.document.exporter.DocumentExporter;
import transcribe.domain.operation.data.OperationType;
import transcribe.domain.transcription.manager.TranscriptionManager;
import transcribe.domain.transcription.service.TranscriptionService;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Objects;

public class DownloadTranscriptDialog extends EnhancedDialog {

    private final DocumentExporter documentExporter;
    private final OperationRunner operationRunner;
    private final TranscriptionManager transcriptionManager;
    private final HelperDownloadAnchor.Factory helperDownloadAnchorFactory;
    private final Binder<DownloadBean> binder;

    public DownloadTranscriptDialog(Long transcriptionId,
                                    HelperDownloadAnchor.Factory helperDownloadAnchorFactory,
                                    BeanLoader beanLoader) {
        Objects.requireNonNull(transcriptionId, "transcriptionId");
        Objects.requireNonNull(helperDownloadAnchorFactory, "helperDownloadAnchorFactory");

        this.helperDownloadAnchorFactory = helperDownloadAnchorFactory;
        this.documentExporter = beanLoader.load(DocumentExporter.class);
        this.operationRunner = beanLoader.load(OperationRunner.class);
        this.transcriptionManager = beanLoader.load(TranscriptionManager.class);
        this.binder = new Binder<>(DownloadBean.class);

        var transcription = beanLoader.load(TranscriptionService.class)
                .projectById(transcriptionId);

        binder.setBean(
                DownloadBean.builder()
                        .transcriptionId(transcriptionId)
                        .name(transcription.name())
                        .format(TranscriptExportableDocumentType.DOCX)
                        .withTimestamps(true)
                        .build()
        );

        var format = new ComboBox<TranscriptExportableDocumentType>("Format");
        format.setItems(TranscriptExportableDocumentType.values());
        format.setItemLabelGenerator(t -> TsEnums.toDisplayName(t.getDocumentType()));
        binder.forField(format)
                .asRequired("Format is required")
                .bind(DownloadBean::getFormat, DownloadBean::setFormat);

        var name = new TextField("File name");
        binder.forField(name)
                .asRequired("File name is required")
                .bind(DownloadBean::getName, DownloadBean::setName);

        var withTimestamps = new Checkbox("Include timestamps");
        binder.forField(withTimestamps)
                .bind(DownloadBean::isWithTimestamps, DownloadBean::setWithTimestamps);

        var form = new FormLayout();
        form.add(format, 2);
        form.add(name, 2);
        form.add(withTimestamps, 2);

        add(form);
        setHeaderTitle("Download transcript");
        setModal(true);
        getFooter().add(newFooterContent());
        setWidth("500px");
        setCloseOnOutsideClick(true);
    }

    private HorizontalLayout newFooterContent() {
        var hl = new HorizontalLayout();
        hl.setSizeFull();
        hl.add(newDownloadButton());
        hl.setJustifyContentMode(FlexComponent.JustifyContentMode.END);

        return hl;
    }

    private Button newDownloadButton() {
        var button = new Button("Download", VaadinIcon.DOWNLOAD.create());
        button.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        button.addClickListener(_ -> {
            if (binder.writeBeanIfValid(binder.getBean())) {
                download();
            }
        });

        return button;
    }

    private void download() {
        var resourceName = "%s.%s".formatted(
                binder.getBean().getName(),
                binder.getBean().getFormat().getDocumentType().getContainer()
        );
        var helperAnchor = helperDownloadAnchorFactory.create();

        var operation = Operation.<Path>builder()
                .name("Exporting document")
                .description("Getting transcript and exporting document")
                .beforeCall(this::close)
                .callable(() -> {
                    var transcript = transcriptionManager.renderTranscript(
                            binder.getBean().getTranscriptionId(),
                            binder.getBean().isWithTimestamps()
                    );

                    return documentExporter.export(transcript, binder.getBean().getFormat().getDocumentType());
                })
                .onSuccess(path -> {
                    var streamResource = new StreamResource(
                            resourceName,
                            () -> Failable.call(() -> Files.newInputStream(path, StandardOpenOption.DELETE_ON_CLOSE))
                    );
                    helperAnchor.setHref(streamResource);
                    helperAnchor.click();
                })
                .onSuccessNotify(false)
                .type(OperationType.BLOCKING)
                .build();

        operationRunner.run(operation, UI.getCurrent());
    }

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    private static class DownloadBean {

        private Long transcriptionId;
        private TranscriptExportableDocumentType format;
        private String name;
        private boolean withTimestamps;

    }

}
