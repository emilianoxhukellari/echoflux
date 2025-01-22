package transcribe.application.core.jpa.filter.impl;

import jakarta.persistence.criteria.JoinType;
import org.springframework.data.jpa.domain.Specification;
import transcribe.application.core.jpa.filter.JpaFilter;
import transcribe.application.core.jpa.filter.JpaFilterUtils;

public abstract class BetweenJpaFilter<ENTITY, V extends Comparable<? super V>> extends JpaFilter<ENTITY> {

    public BetweenJpaFilter(String attribute, String property, boolean asCollection) {
        super(attribute, property, asCollection);
    }

    protected abstract V getFrom();

    protected abstract V getTo();

    @Override
    public Specification<ENTITY> getSpecification() {
        Specification<ENTITY> fromSpecification = (root, _, criteriaBuilder) -> {
            if (getFrom() == null) {
                return criteriaBuilder.conjunction();
            }

            return asCollection
                    ? criteriaBuilder.greaterThanOrEqualTo(root.join(attribute, JoinType.LEFT), getFrom())
                    : criteriaBuilder.greaterThanOrEqualTo(JpaFilterUtils.get(root, attribute), getFrom());
        };

        Specification<ENTITY> toSpecification = (root, _, criteriaBuilder) -> {
            if (getTo() == null) {
                return criteriaBuilder.conjunction();
            }

            return asCollection
                    ? criteriaBuilder.lessThanOrEqualTo(root.join(attribute, JoinType.LEFT), getTo())
                    : criteriaBuilder.lessThanOrEqualTo(JpaFilterUtils.get(root, attribute), getTo());
        };

        return fromSpecification.and(toSpecification);
    }


}
