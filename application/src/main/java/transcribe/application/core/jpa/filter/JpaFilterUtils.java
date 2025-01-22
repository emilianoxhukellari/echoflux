package transcribe.application.core.jpa.filter;

import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Root;
import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;

public final class JpaFilterUtils {

    @SuppressWarnings("unchecked")
    public static <T> Path<T> get(Root<?> root, String propertyNested) {
        var attributeNames = StringUtils.split(propertyNested, ".");

        return Arrays.stream(attributeNames)
                .reduce((Path<T>) root, Path::get, (a, _) -> a);
    }

}
