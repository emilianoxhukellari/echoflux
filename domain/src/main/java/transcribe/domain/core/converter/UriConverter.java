package transcribe.domain.core.converter;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import transcribe.core.core.utils.MoreUris;

import java.net.URI;
import java.util.Objects;

@Converter(autoApply = true)
public class UriConverter implements AttributeConverter<URI, String> {

    @Override
    public String convertToDatabaseColumn(URI attribute) {
        return Objects.toString(attribute, null);
    }

    @Override
    public URI convertToEntityAttribute(String dbData) {
        return MoreUris.newUri(dbData);
    }

}
