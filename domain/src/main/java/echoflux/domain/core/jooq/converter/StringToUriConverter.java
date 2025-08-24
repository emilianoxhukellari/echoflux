package echoflux.domain.core.jooq.converter;

import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jooq.Converter;

import java.net.URI;
import java.util.Objects;

public class StringToUriConverter implements Converter<String, URI> {

    @Override
    public URI from(String databaseObject) {
        return StringUtils.isBlank(databaseObject) ? null : URI.create(databaseObject);
    }

    @Override
    public String to(URI userObject) {
        return Objects.toString(userObject, null);
    }

    @NotNull
    @Override
    public Class<String> fromType() {
        return String.class;
    }

    @NotNull
    @Override
    public Class<URI> toType() {
        return URI.class;
    }

}
