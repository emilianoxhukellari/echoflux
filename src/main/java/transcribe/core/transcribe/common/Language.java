package transcribe.core.transcribe.common;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum Language {

    ALBANIAN("sq-AL"),
    ENGLISH_GB("en-GB"),
    ENGLISH_US("en-US");

    private final String bcp47;

}
