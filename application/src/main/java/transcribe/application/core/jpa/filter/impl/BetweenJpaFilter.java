package transcribe.application.core.jpa.filter.impl;

import jakarta.persistence.criteria.JoinType;
import org.springframework.data.jpa.domain.Specification;
import transcribe.application.core.jpa.filter.JpaFilter;

public abstract class BetweenJpaFilter<T, V extends Comparable<? super V>> extends JpaFilter<T> {

    public BetweenJpaFilter(String property, boolean asCollection) {
        super(property, asCollection);
    }

    protected abstract V getFrom();

    protected abstract V getTo();

    @Override
    public Specification<T> getSpecification() {
        Specification<T> fromSpecification = (root, _, criteriaBuilder) -> {
            if (getFrom() == null) {
                return criteriaBuilder.conjunction();
            }

            return asCollection
                    ? criteriaBuilder.greaterThanOrEqualTo(root.join(property, JoinType.LEFT), getFrom())
                    : criteriaBuilder.greaterThanOrEqualTo(root.get(property), getFrom());
        };

        Specification<T> toSpecification = (root, _, criteriaBuilder) -> {
            if (getTo() == null) {
                return criteriaBuilder.conjunction();
            }

            return asCollection
                    ? criteriaBuilder.lessThanOrEqualTo(root.join(property, JoinType.LEFT), getTo())
                    : criteriaBuilder.lessThanOrEqualTo(root.get(property), getTo());
        };

        return fromSpecification.and(toSpecification);
    }


}
