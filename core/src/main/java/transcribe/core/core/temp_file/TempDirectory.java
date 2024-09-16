package transcribe.core.core.temp_file;

import java.io.File;
import java.nio.file.Path;

public interface TempDirectory {

    Path locationPath();

    default String locationString() {
        return locationPath().toString();
    }

    default File locationFile() {
        return locationPath().toFile();
    }

}
