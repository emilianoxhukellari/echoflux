package transcribe.application.transcribe;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.formlayout.FormLayout;
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
import transcribe.application.core.spring.SpringContext;
import transcribe.core.core.utils.MoreEnums;
import transcribe.core.document.exporter.DocumentExporter;
import transcribe.domain.operation.data.OperationType;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Objects;

public class DownloadTranscriptDialog extends EnhancedDialog {

    private final DocumentExporter documentExporter;
    private final OperationRunner operationRunner;
    private final HelperDownloadAnchor.Factory helperDownloadAnchorFactory;
    private final Binder<DownloadBean> binder;

    public DownloadTranscriptDialog(Long transcriptionId,
                                    String transcriptionName,
                                    HelperDownloadAnchor.Factory helperDownloadAnchorFactory) {
        Objects.requireNonNull(transcriptionId);
        this.helperDownloadAnchorFactory = Objects.requireNonNull(helperDownloadAnchorFactory);
        this.documentExporter = SpringContext.getBean(DocumentExporter.class);
        this.operationRunner = SpringContext.getBean(OperationRunner.class);
        this.binder = new Binder<>(DownloadBean.class);

        binder.setBean(
                DownloadBean.builder()
                        .transcriptionId(transcriptionId)
                        .name(transcriptionName)
                        .format(TranscriptExportableDocumentType.DOCX)
                        .build()
        );

        var format = new ComboBox<TranscriptExportableDocumentType>("Format");
        format.setItems(TranscriptExportableDocumentType.values());
        format.setItemLabelGenerator(t -> MoreEnums.toDisplayName(t.getDocumentType()));
        binder.forField(format)
                .asRequired("Format is required")
                .bind(DownloadBean::getFormat, DownloadBean::setFormat);

        var name = new TextField("File name");
        binder.forField(name)
                .asRequired("File name is required")
                .bind(DownloadBean::getName, DownloadBean::setName);

        var form = new FormLayout();
        form.add(format, 2);
        form.add(name, 2);

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
        var button = new Button("Download", _ -> {
            if (binder.validate().isOk()) {
                download();
            }
        });

        button.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

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

                    //todo: retrieve transcript
                    return documentExporter.export("", binder.getBean().getFormat().getDocumentType());
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

    }

}
