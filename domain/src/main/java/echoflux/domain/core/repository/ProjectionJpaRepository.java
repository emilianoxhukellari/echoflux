package echoflux.domain.core.repository;

import echoflux.core.core.validate.constraint.interface_type.InterfaceType;
import echoflux.core.core.validate.guard.Guard;
import echoflux.domain.core.data.BaseEntity;
import echoflux.domain.core.data.HasId;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.data.util.TypeInformation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

@NoRepositoryBean
@Transactional(readOnly = true)
@Validated
public interface ProjectionJpaRepository<E extends BaseEntity<ID>, ID> extends JpaSpecificationExecutor<E> {

    default <P extends HasId<ID>> P getProjectedById(@NotNull ID id,
                                                     @NotNull @InterfaceType Class<P> projectionType,
                                                     @NotNull Set<@NotBlank String> attributePaths) {
        return findProjectedById(id, projectionType, attributePaths)
                .orElseThrow(() -> new EntityNotFoundException("Entity with id [%s] not found".formatted(id)));
    }

    default <P extends HasId<ID>> Optional<P> findProjectedById(ID id,
                                                                @NotNull @InterfaceType Class<P> projectionType,
                                                                @NotNull Set<@NotBlank String> attributePaths) {
        var typeInformation = TypeInformation.of(projectionType);

        boolean collectionPath = attributePaths.stream()
                .map(typeInformation::getRequiredProperty)
                .anyMatch(TypeInformation::isCollectionLike);

        if (!collectionPath) {
            return findBy((root, _, cb) -> cb.equal(root.get(HasId.ID), id),
                    q ->
                            q.as(projectionType)
                                    .project(attributePaths)
                                    .one()
            );
        }

        var all = findBy((root, _, cb) -> cb.equal(root.get(HasId.ID), id),
                q ->
                        q.as(projectionType)
                                .project(attributePaths)
                                .all()
        );

        return Guard.singleOrEmpty(all);
    }

    default <P extends HasId<ID>> List<P> findAllProjected(@NotNull Specification<E> specification,
                                                           @NotNull Pageable pageable,
                                                           @NotNull @InterfaceType Class<P> projectionType,
                                                           @NotNull Set<@NotBlank String> attributePaths) {
        var typeInformation = TypeInformation.of(projectionType);

        boolean collectionPath = attributePaths.stream()
                .map(typeInformation::getRequiredProperty)
                .anyMatch(TypeInformation::isCollectionLike);

        if (!collectionPath) {
            return findBy(specification, q ->
                    q.as(projectionType)
                            .project(attributePaths)
                            .page(pageable)
                            .getContent()
            );
        }

        var hasIdList = findBy(specification, q ->
                q.as(HasId.class)
                        .project(HasId.ID)
                        .page(pageable)
                        .getContent()
        );

        if (hasIdList.isEmpty()) {
            return List.of();
        }

        @SuppressWarnings("unchecked")
        var idList = hasIdList.stream()
                .map(hasId -> (ID) hasId.getId())
                .filter(Objects::nonNull)
                .toList();

        return findBy((root, _, _) -> root.get(HasId.ID).in(idList),
                q -> q.as(projectionType)
                        .project(attributePaths)
                        .sortBy(pageable.getSort())
                        .all()
        );
    }

}
