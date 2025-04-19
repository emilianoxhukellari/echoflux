package echoflux.domain.core.repository;

import jakarta.persistence.Id;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.transaction.annotation.Transactional;
import echoflux.annotation.core.AttributeOverride;
import echoflux.annotation.core.ObjectConvertable;
import echoflux.annotation.core.ParentProperty;
import echoflux.annotation.projection.ProjectionInterface;
import echoflux.annotation.projection.ProjectionInterfaceSupport;
import echoflux.core.core.bean.BeanTypeAware;
import echoflux.core.core.bean.MoreBeans;
import echoflux.core.core.projection.ProjectionInterfaceLoader;

import java.util.List;
import java.util.Optional;

@NoRepositoryBean
@Transactional(readOnly = true)
public interface EnhancedJpaRepository<ENTITY, ID> extends
        JpaRepository<ENTITY, ID>,
        JpaSpecificationExecutor<ENTITY>,
        BeanTypeAware<ENTITY> {

    /**
     * <p>
     * Supports projection of interfaces, classes, and record types. By default, this method will project
     * all attributes.
     * </p>
     * <p>
     * Use {@link AttributeOverride} to configure the attributes and {@link ParentProperty} to allow nested properties.
     * </p>
     * <p>
     * Classes and record types must be annotated with {@link ProjectionInterfaceSupport} which will generate a
     * projection interface for the class or record type.
     * Alternatively, there must be a projection interface annotated with {@link ProjectionInterface}
     * that also implements {@link ObjectConvertable}.
     * </p>
     */
    default <T> Optional<T> findByIdEnhanced(ID id, Class<T> projection) {
        var idAttributeName = MoreBeans.getSingleFieldWithAnnotation(getBeanType(), Id.class).getName();
        var projectProperties = MoreBeans.getDefaultProjectAttributeNamesNested(projection);

        if (projection.isInterface()) {
            return findBy((root, _, cb) -> cb.equal(root.get(idAttributeName), id),
                    q ->
                            q.as(projection)
                                    .project(projectProperties)
                                    .one()
            );
        }

        var projectionInterface = ProjectionInterfaceLoader.load(projection);
        return findBy((root, _, cb) -> cb.equal(root.get(idAttributeName), id),
                q ->
                        q.as(projectionInterface)
                                .project(projectProperties)
                                .one()
                                .map(ObjectConvertable::toObject)
        );
    }

    /**
     * <p>
     * Supports projection of interfaces, classes, and record types. By default, this method will project
     * all attributes.
     * </p>
     * <p>
     * Use {@link AttributeOverride} to configure the attributes and {@link ParentProperty} to allow nested properties.
     * </p>
     * <p>
     * Classes and record types must be annotated with {@link ProjectionInterfaceSupport} which will generate a
     * projection interface for the class or record type.
     * Alternatively, there must be a projection interface annotated with {@link ProjectionInterface}
     * that also implements {@link ObjectConvertable}.
     * </p>
     */
    default <T> List<T> findAllEnhanced(Specification<ENTITY> specification, Pageable pageable, Class<T> projection) {
        var projectProperties = MoreBeans.getDefaultProjectAttributeNamesNested(projection);

        if (projection.isInterface()) {
            return findBy(specification, q ->
                    q.as(projection)
                            .project(projectProperties)
                            .sortBy(pageable.getSort())
                            .page(pageable)
                            .getContent()
            );
        }

        var projectionInterface = ProjectionInterfaceLoader.load(projection);
        return findBy(specification, q ->
                q.as(projectionInterface)
                        .project(projectProperties)
                        .sortBy(pageable.getSort())
                        .page(pageable)
                        .map(ObjectConvertable::toObject)
                        .getContent()
        );
    }

}
