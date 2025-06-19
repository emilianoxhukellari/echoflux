package echoflux.application.core.jpa.filter.impl;

import echoflux.domain.core.criteria.CriteriaPathResolver;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Path;
import org.springframework.data.jpa.domain.Specification;
import echoflux.application.core.jpa.filter.JpaFilter;

public abstract class BetweenJpaFilter<E, V extends Comparable<? super V>> extends JpaFilter<E> {

    public BetweenJpaFilter(String property, boolean asCollection) {
        super(property, asCollection);
    }

    protected abstract V getFrom();

    protected abstract V getTo();

    @Override
    public Specification<E> getSpecification() {
        Specification<E> fromSpecification = (root, _, cb) -> {
            if (getFrom() == null) {
                return cb.conjunction();
            }
            Path<V> path = CriteriaPathResolver.resolve(root, property, JoinType.LEFT, asCollection);

            return cb.greaterThanOrEqualTo(path, getFrom());
        };

        Specification<E> toSpecification = (root, _, cb) -> {
            if (getTo() == null) {
                return cb.conjunction();
            }
            Path<V> path = CriteriaPathResolver.resolve(root, property, JoinType.LEFT, asCollection);

            return cb.lessThanOrEqualTo(path, getTo());
        };

        return fromSpecification.and(toSpecification);
    }


}
