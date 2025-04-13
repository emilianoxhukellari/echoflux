package transcribe.application.core.jpa.dto.impl;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import transcribe.application.core.jpa.dto.JpaDtoConfiguration;
import transcribe.application.core.jpa.dto.JpaDtoMapper;
import transcribe.application.core.jpa.dto.JpaDtoService;
import transcribe.application.core.spring.SpringContext;
import transcribe.core.core.bean.MoreBeans;
import transcribe.domain.core.repository.EnhancedJpaRepository;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Supplier;

@Slf4j
public class SimpleJpaDtoService<DTO, ENTITY, ID> implements JpaDtoService<DTO, ENTITY, ID> {

    private final static LoadingCache<Class<?>, SimpleJpaDtoService<?, ?, ?>> CACHE = CacheBuilder.newBuilder()
            .build(CacheLoader.from(SimpleJpaDtoService::new));

    private final EnhancedJpaRepository<ENTITY, ID> repository;
    private final JpaDtoMapper<DTO, ENTITY> mapper;
    private final Class<DTO> beanType;
    private final JpaDtoConfiguration jpaDtoConfiguration;

    @SuppressWarnings("unchecked")
    public static <DTO, ENTITY, ID> SimpleJpaDtoService<DTO, ENTITY, ID> ofBeanType(Class<DTO> beanType) {
        return (SimpleJpaDtoService<DTO, ENTITY, ID>) CACHE.getUnchecked(beanType);
    }

    private SimpleJpaDtoService(Class<DTO> beanType) {
        this.beanType = Objects.requireNonNull(beanType, "DTO bean type must not be null");
        this.jpaDtoConfiguration = JpaDtoConfiguration.ofBeanType(beanType);

        @SuppressWarnings("unchecked")
        var repository = (EnhancedJpaRepository<ENTITY, ID>) SpringContext.loadBeanWhen(
                EnhancedJpaRepository.class,
                r -> Objects.equals(jpaDtoConfiguration.entityBeanType(), r.getBeanType())
        );
        @SuppressWarnings("unchecked")
        var mapper = (JpaDtoMapper<DTO, ENTITY>) SpringContext.loadBeanWhen(
                JpaDtoMapper.class,
                m -> Objects.equals(beanType, m.getBeanType())
        );

        this.repository = Objects.requireNonNull(repository, "Repository must not be null");
        this.mapper = Objects.requireNonNull(mapper, "Mapper must not be null");
    }

    @Override
    public Optional<DTO> findById(ID id) {
        return SpringContext.getTransactionalReadonly(() -> repository.findByIdEnhanced(id, beanType));
    }

    @Override
    public List<DTO> findAll(Specification<ENTITY> specification, Pageable pageable) {
        return SpringContext.getTransactionalReadonly(() -> repository.findAllEnhanced(specification, pageable, beanType));
    }

    @Override
    public long count(Specification<ENTITY> specification) {
        return SpringContext.getTransactionalReadonly(() -> repository.count(specification));
    }

    @Override
    public DTO save(DTO dto) {
        var id = getIdValue(dto);

        return SpringContext.getTransactional(() -> {
            ENTITY entity;
            if (id != null) {
                log.debug("Updating entity of type {} from DTO with id : {}", dto.getClass().getSimpleName(), id);

                var currentEntity = repository.getReferenceById(id);
                entity = mapper.updateEntity(currentEntity, dto);
            } else {
                log.debug("Creating new entity of type {} from DTO", dto.getClass().getSimpleName());

                entity = mapper.toEntity(dto);
            }

            return perform(() -> repository.save(entity));
        });
    }

    @Override
    public void delete(DTO dto) {
        var id = getIdValue(dto);
        log.debug("Deleting entity with id : {} using DTO", id);

        SpringContext.runTransactional(() -> repository.deleteById(id));
    }

    @Override
    public DTO toDto(ENTITY entity) {
        return SpringContext.getTransactionalReadonly(() -> mapper.toDto(entity));
    }

    @Override
    public DTO perform(Supplier<ENTITY> action) {
        Objects.requireNonNull(action, "Action must not be null");

        return SpringContext.getTransactional(() -> {
            var entity = action.get();
            return mapper.toDto(entity);
        });
    }

    @SuppressWarnings("unchecked")
    private ID getIdValue(DTO dto) {
        return (ID) MoreBeans.getFieldValue(dto, jpaDtoConfiguration.idFieldName());
    }

}
