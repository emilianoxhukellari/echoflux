package echoflux.core.core.country;

import echoflux.core.core.display_name.DisplayName;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.ZoneId;
import java.util.List;

@Getter
@RequiredArgsConstructor
public enum Country {

    @DisplayName("Albania")
    ALBANIA(
            List.of(
                    ZoneId.of("Europe/Tirane")
            )
    ),

    @DisplayName("Latvia")
    LATVIA(
            List.of(
                    ZoneId.of("Europe/Riga")
            )
    );

    private final List<ZoneId> zoneIds;

}
