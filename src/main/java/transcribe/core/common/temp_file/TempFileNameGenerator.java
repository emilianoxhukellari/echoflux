package transcribe.core.common.temp_file;

import com.github.f4b6a3.ulid.UlidCreator;

public interface TempFileNameGenerator {

    default String newFileName() {
        return String.format("%s-%s", fileNamePrefix(), UlidCreator.getUlid());
    }

    String fileNamePrefix();

}
