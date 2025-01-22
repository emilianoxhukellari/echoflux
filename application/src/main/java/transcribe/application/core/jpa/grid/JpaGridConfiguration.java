package transcribe.application.core.jpa.grid;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import transcribe.application.core.jpa.dto.JpaDtoService;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class JpaGridConfiguration<DTO, ENTITY, ID> {

    @NotNull
    Class<DTO> beanType;

    @NotNull
    JpaDtoService<DTO, ENTITY, ID> service;

    @NotNull
    @Builder.Default
    Integer defaultPageSize = 50;

    @NotNull
    @Builder.Default
    Specification<ENTITY> defaultSpecification = (_, _, criteriaBuilder) -> criteriaBuilder.conjunction();

    /**
     * If unspecified, the ID property is used.
     * */
    @NotNull
    @Builder.Default
    String defaultSortAttribute = StringUtils.EMPTY;

    @NotNull
    @Builder.Default
    Sort.Direction defaultSortDirection = Sort.Direction.DESC;

}
