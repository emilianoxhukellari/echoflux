package transcribe.application.core.jpa.core;

import com.vaadin.flow.shared.util.SharedUtil;
import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.Objects;
import java.util.stream.Collectors;

public class JpaPropertyDefinitionUtils {

    public static <T> String toDisplayName(JpaPropertyDefinition<T, ?> propertyDefinition) {
        Objects.requireNonNull(propertyDefinition, "Property definition must not be null");

        var parts = propertyDefinition.getName().split("\\.");

        return Arrays.stream(parts)
                .map(SharedUtil::camelCaseToHumanFriendly)
                .collect(Collectors.joining(StringUtils.SPACE));
    }

}
