package transcribe.application.core.jpa.dto;

import jakarta.validation.constraints.NotNull;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.validation.annotation.Validated;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.function.Supplier;

@Validated
public interface JpaDtoService<DTO, ENTITY, ID> {

    default DTO getById(@NotNull ID id) {
        return findById(id)
                .orElseThrow(() -> new NoSuchElementException("Not found by id [%s]".formatted(id)));
    }

    Optional<DTO> findById(ID id);

    List<DTO> findAll(@NotNull Specification<ENTITY> specification, @NotNull Pageable pageable);

    long count(@NotNull Specification<ENTITY> specification);

    DTO save(@NotNull DTO dto);

    void delete(DTO dto);

    /**
     * Perform an action on the entity and return the mapped DTO.
     * */
    DTO perform(Supplier<ENTITY> action);

    /**
     * Map the entity to a DTO including the lazy loaded fields.
     * */
    DTO toDto(ENTITY entity);

}