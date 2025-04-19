package echoflux.core.transcribe.common;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import echoflux.core.core.display_name.DisplayName;

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
    ENGLISH_US("en-US"),

    @DisplayName("Latvian")
    LATVIAN("lv-LV"),

    @DisplayName("Lithuanian")
    LITHUANIAN("lt-LT"),

    @DisplayName("Estonian")
    ESTONIAN("et-EE"),

    @DisplayName("Russian")
    RUSSIAN("ru-RU"),

    @DisplayName("Polish")
    POLISH("pl-PL");

    private final String bcp47;

}
