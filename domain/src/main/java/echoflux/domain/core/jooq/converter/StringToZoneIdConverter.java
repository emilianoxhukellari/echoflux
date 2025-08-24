package echoflux.domain.core.jooq.converter;

import org.jetbrains.annotations.NotNull;
import org.jooq.Converter;

import java.time.ZoneId;

public class StringToZoneIdConverter implements Converter<String, ZoneId> {

    @Override
    public ZoneId from(String databaseObject) {
        return databaseObject == null ? null : ZoneId.of(databaseObject);
    }

    @Override
    public String to(ZoneId userObject) {
        return userObject == null ? null : userObject.getId();
    }

    @NotNull
    @Override
    public Class<String> fromType() {
        return String.class;
    }

    @NotNull
    @Override
    public Class<ZoneId> toType() {
        return ZoneId.class;
    }

}
