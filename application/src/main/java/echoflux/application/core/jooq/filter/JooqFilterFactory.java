package echoflux.application.core.jooq.filter;

import com.fasterxml.jackson.databind.JsonNode;
import echoflux.application.core.jooq.core.JooqPropertyType;
import echoflux.application.core.jooq.filter.impl.BooleanJooqFilter;
import echoflux.application.core.jooq.filter.impl.DurationJooqFilter;
import echoflux.application.core.jooq.filter.impl.EnumArrayJooqFilter;
import echoflux.application.core.jooq.filter.impl.EnumJooqFilter;
import echoflux.application.core.jooq.filter.impl.NumberJooqFilter;
import echoflux.application.core.jooq.filter.impl.OffsetDateTimeJooqFilter;
import echoflux.application.core.jooq.filter.impl.TextArrayJooqFilter;
import echoflux.application.core.jooq.filter.impl.TextJooqFilter;
import org.jooq.Field;

import java.net.URI;
import java.time.Duration;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.Objects;

public final class JooqFilterFactory {

    @SuppressWarnings("unchecked")
    public static JooqFilter<?> newFilter(Field<?> field) {
        Objects.requireNonNull(field, "field");

        var supportedType = JooqPropertyType.fromField(field);

        return switch (supportedType) {
            case STRING -> new TextJooqFilter((Field<String>) field);
            case STRING_ARRAY -> new TextArrayJooqFilter((Field<String[]>) field);
            case URI -> TextJooqFilter.ofUriField((Field<URI>) field);
            case ZONE_ID -> TextJooqFilter.ofZoneIdField((Field<ZoneId>) field);
            case JSON -> TextJooqFilter.ofJsonfield((Field<JsonNode>) field);
            case BOOLEAN -> new BooleanJooqFilter((Field<Boolean>) field);
            case OFFSET_DATE_TIME -> new OffsetDateTimeJooqFilter((Field<OffsetDateTime>) field);
            case DOUBLE, FLOAT, LONG, INTEGER -> new NumberJooqFilter((Field<Number>) field);
            case DURATION -> new DurationJooqFilter((Field<Duration>) field);
            case ENUM -> new EnumJooqFilter((Field<Enum<?>>) field);
            case ENUM_ARRAY -> new EnumArrayJooqFilter((Field<Enum<?>[]>) field);
        };
    }

}
