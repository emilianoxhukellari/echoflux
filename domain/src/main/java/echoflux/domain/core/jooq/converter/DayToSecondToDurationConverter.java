package echoflux.domain.core.jooq.converter;

import org.jetbrains.annotations.NotNull;
import org.jooq.Converter;
import org.jooq.types.DayToSecond;

import java.time.Duration;

public class DayToSecondToDurationConverter implements Converter<DayToSecond, Duration> {

    @Override
    public Duration from(DayToSecond databaseObject) {
        return databaseObject == null
                ? null
                : databaseObject.toDuration();
    }

    @Override
    public DayToSecond to(Duration userObject) {
        return userObject == null
                ? null
                : DayToSecond.valueOf(userObject);
    }

    @NotNull
    @Override
    public Class<DayToSecond> fromType() {
        return DayToSecond.class;
    }

    @NotNull
    @Override
    public Class<Duration> toType() {
        return Duration.class;
    }

}
