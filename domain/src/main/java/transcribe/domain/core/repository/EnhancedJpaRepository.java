package transcribe.domain.core.repository;

import jakarta.persistence.Id;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.transaction.annotation.Transactional;
import transcribe.core.core.bean.BeanTypeAware;
import transcribe.annotation.core.ObjectConvertable;
import transcribe.core.core.bean.utils.MoreBeans;
import transcribe.core.core.projection.ProjectionInterfaceLoader;

import java.util.List;
import java.util.Optional;

@NoRepositoryBean
@Transactional(readOnly = true)
public interface EnhancedJpaRepository<ENTITY, ID> extends
        JpaRepository<ENTITY, ID>,
        JpaSpecificationExecutor<ENTITY>,
        BeanTypeAware<ENTITY> {

    default <T> Optional<T> findById(ID id, Class<T> projection) {
        var projectionInterface = ProjectionInterfaceLoader.load(projection);
        var idAttributeName = MoreBeans.getSingleFieldWithAnnotation(getBeanType(), Id.class).getName();
        var attributeNames = MoreBeans.getAttributeNamesNested(projection);

        return findBy((root, _, cb) -> cb.equal(root.get(idAttributeName), id),
                q ->
                        q.as(projectionInterface)
                                .project(attributeNames)
                                .one()
                                .map(ObjectConvertable::toObject)
        );
    }

    default <T> List<T> findAll(Specification<ENTITY> specification, Pageable pageable, Class<T> projection) {
        if (projection.isInterface()) {
            return findBy(specification, q ->
                    q.as(projection)
                            .sortBy(pageable.getSort())
                            .page(pageable)
                            .getContent()
            );
        }

        var projectionInterface = ProjectionInterfaceLoader.load(projection);
        var attributeNames = MoreBeans.getAttributeNamesNested(projection);

        return findBy(specification, q ->
                q.as(projectionInterface)
                        .project(attributeNames)
                        .sortBy(pageable.getSort())
                        .page(pageable)
                        .map(ObjectConvertable::toObject)
                        .getContent()
        );
    }

}
