package transcribe.core.transcribe.common;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum Language {

    ALBANIAN("sq-AL", "Albanian"),
    ENGLISH_GB("en-GB", "English (UK)"),
    ENGLISH_US("en-US", "English (US)"),;

    private final String bcp47;
    private final String displayName;

}
