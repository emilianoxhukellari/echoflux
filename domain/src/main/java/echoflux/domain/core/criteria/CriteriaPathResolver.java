package echoflux.domain.core.criteria;

import jakarta.persistence.criteria.From;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Root;
import org.apache.commons.lang3.StringUtils;

import java.util.Objects;

public final class CriteriaPathResolver {

    public static <E, T> Path<T> resolve(Root<E> root, String attributePath) {
        return resolve(root, attributePath, JoinType.LEFT, false);
    }

    @SuppressWarnings("unchecked")
    public static <E, T> Path<T> resolve(Root<E> root, String attributePath, JoinType joinType, boolean joinFinal) {
        Objects.requireNonNull(root, "root");
        Objects.requireNonNull(attributePath, "attributePath");
        Objects.requireNonNull(joinType, "joinType");

        var parts = StringUtils.split(attributePath, ".");
        From<?, ?> from = root;

        for (int i = 0; i < parts.length; i++) {
            var part = parts[i];
            boolean isLast = (i == parts.length - 1);

            if (isLast && !joinFinal) {
                return from.get(part);
            } else {
                from = from.join(part, joinType);
            }
        }

        return (Path<T>) from;
    }

}
