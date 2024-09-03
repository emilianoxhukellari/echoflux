package transcribe.application.core.jpa.core;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collection;
import java.util.NoSuchElementException;

@RequiredArgsConstructor
@Getter
public enum JpaSupportedType {

    STRING(String.class),
    BOOLEAN(Boolean.class),
    ENUM(Enum.class),
    LOCAL_DATE_TIME(LocalDateTime.class),
    LOCAL_DATE(LocalDateTime.class),
    DOUBLE(Double.class),
    LONG(Long.class),
    FLOAT(Float.class),
    INTEGER(Integer.class),
    COLLECTION(Collection.class);

    private final Class<?> type;

    /**
     * @throws NoSuchElementException if the given type is not supported
     * */
    public static JpaSupportedType ofBeanType(Class<?> beanType) {
        return Arrays.stream(values())
                .filter(t -> t.getType().isAssignableFrom(beanType))
                .findFirst()
                .orElseThrow(() -> new NoSuchElementException("Unsupported jpa type: " + beanType));

    }

}
