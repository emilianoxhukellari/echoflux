package echoflux.domain.core.jooq.core;

import echoflux.core.core.validate.guard.Guard;
import org.jooq.Field;
import org.jooq.Table;

import java.util.NoSuchElementException;
import java.util.Objects;

public final class JooqUtils {

    public static Field<?> getFieldByQualifiedName(Table<?> table, String qualifiedName) {
        Guard.notNull(table, "table");

        return table.fieldStream()
                .filter(f -> Objects.equals(f.getQualifiedName().toString(), qualifiedName))
                .findFirst()
                .orElseThrow(() -> new NoSuchElementException("Field [%s] not found".formatted(qualifiedName)));
    }

    public static Field<?> getFieldByUnqualifiedName(Table<?> table, String unqualifiedName) {
        Guard.notNull(table, "table");

        var field = table.field(unqualifiedName);
        Guard.notNull(field, "Field [%s] not found in table [%s]".formatted(unqualifiedName, table.getName()));

        return field;
    }

}
