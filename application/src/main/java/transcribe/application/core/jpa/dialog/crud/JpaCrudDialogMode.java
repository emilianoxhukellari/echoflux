package transcribe.application.core.jpa.dialog.crud;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum JpaCrudDialogMode {

    NEW("New"),
    EDIT("Edit");

    private final String prettyName;

}
