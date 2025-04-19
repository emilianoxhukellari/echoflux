package echoflux.application.core.jpa.filter;

import org.apache.commons.collections4.ListUtils;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;

public record CombinedFilter<ENTITY>(List<JpaFilter<ENTITY>> filters) {

    public static <ENTITY> CombinedFilter<ENTITY> empty() {
        return new CombinedFilter<>(List.of());
    }

    public static <ENTITY> CombinedFilter<ENTITY> of(List<JpaFilter<ENTITY>> filters) {
        return new CombinedFilter<>(filters);
    }

    public Specification<ENTITY> specification() {
        return ListUtils.emptyIfNull(filters).stream()
                .map(JpaFilter::getSpecification)
                .reduce(Specification::and)
                .orElse((_, _, criteriaBuilder) -> criteriaBuilder.conjunction());
    }

}
