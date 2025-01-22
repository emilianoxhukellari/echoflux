package transcribe.core.transcribe.common;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import transcribe.core.core.display_name.DisplayName;

@Getter
@RequiredArgsConstructor
public enum Language {

    @DisplayName("Albanian")
    ALBANIAN("sq-AL"),

    @DisplayName("Greek")
    GREEK("el-GR"),

    @DisplayName("English (GB)")
    ENGLISH_GB("en-GB"),

    @DisplayName("English (US)")
    ENGLISH_US("en-US");

    private final String bcp47;

}
