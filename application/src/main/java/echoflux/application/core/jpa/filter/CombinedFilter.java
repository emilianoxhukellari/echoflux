package echoflux.application.core.jpa.filter;

import echoflux.core.core.validate.guard.Guard;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;

public record CombinedFilter<E>(List<JpaFilter<E>> filters) {

    public CombinedFilter {
        Guard.notNull(filters);
    }

    public static <E> CombinedFilter<E> empty() {
        return new CombinedFilter<>(List.of());
    }

    public static <E> CombinedFilter<E> of(List<JpaFilter<E>> filters) {
        return new CombinedFilter<>(filters);
    }

    public Specification<E> specification() {
        return filters.stream()
                .map(JpaFilter::getSpecification)
                .reduce(Specification::and)
                .orElse((_, _, cb) -> cb.conjunction());
    }

}
