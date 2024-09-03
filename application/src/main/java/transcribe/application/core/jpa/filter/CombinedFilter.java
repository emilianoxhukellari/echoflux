package transcribe.application.core.jpa.filter;

import org.apache.commons.collections4.ListUtils;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;

public record CombinedFilter<T>(List<JpaFilter<T>> filters) {

    public static <T> CombinedFilter<T> empty() {
        return new CombinedFilter<>(List.of());
    }

    public static <T> CombinedFilter<T> of(List<JpaFilter<T>> filters) {
        return new CombinedFilter<>(filters);
    }

    public Specification<T> specification() {
        return ListUtils.emptyIfNull(filters).stream()
                .map(JpaFilter::getSpecification)
                .reduce(Specification::and)
                .orElse((_, _, criteriaBuilder) -> criteriaBuilder.conjunction());
    }

}
