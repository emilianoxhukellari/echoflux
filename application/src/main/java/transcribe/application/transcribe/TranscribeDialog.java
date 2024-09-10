package transcribe.application.transcribe;

import com.vaadin.flow.component.html.NativeLabel;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.component.upload.receivers.FileBuffer;
import lombok.extern.slf4j.Slf4j;
import org.vaadin.lineawesome.LineAwesomeIcon;
import transcribe.application.core.dialog.EnhancedDialog;
import transcribe.application.core.upload.UploadFileFactory;

@Slf4j
public class TranscribeDialog extends EnhancedDialog {

    public TranscribeDialog() {
        var fileBuffer = new FileBuffer(UploadFileFactory.INSTANCE);
        var upload = new Upload();
        upload.setReceiver(fileBuffer);
        upload.setDropLabelIcon(LineAwesomeIcon.UPLOAD_SOLID.create());
        upload.setDropLabel(new NativeLabel("Upload media file"));

        setHeaderTitle("Transcribe");

        setMinWidth("500px");
        add(upload);

        setModal(true);
    }

}
