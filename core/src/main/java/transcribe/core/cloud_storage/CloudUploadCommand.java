package transcribe.core.cloud_storage;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.nio.file.Path;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CloudUploadCommand {

    @NotNull
    Path path;

    @Nullable
    String contentType;

    @Builder.Default
    boolean temp = false;

}
