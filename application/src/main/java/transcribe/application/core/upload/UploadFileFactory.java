package transcribe.application.core.upload;

import com.vaadin.flow.component.upload.receivers.FileFactory;
import transcribe.core.core.temp_file.TempFileNameGenerator;
import transcribe.core.media.temp_file.MediaTempDirectory;

import java.io.File;

public enum UploadFileFactory implements FileFactory, TempFileNameGenerator {

    INSTANCE;

    @Override
    public File createFile(String fileName) {
        return MediaTempDirectory.INSTANCE.locationPath().resolve(newFileName() + ".tmp").toFile();
    }

    @Override
    public String fileNamePrefix() {
        return "upload";
    }

}
