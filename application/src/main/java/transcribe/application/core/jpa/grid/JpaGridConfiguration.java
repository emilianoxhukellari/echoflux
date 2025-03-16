package transcribe.application.core.jpa.grid;

import jakarta.annotation.Nullable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import transcribe.application.core.jpa.dto.JpaDtoService;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class JpaGridConfiguration<DTO, ENTITY, ID> {

    private Class<DTO> beanType;
    private JpaDtoService<DTO, ENTITY, ID> service;
    @Nullable
    private Specification<ENTITY> defaultSpecification;
    @Nullable
    private String defaultSortAttribute;
    @Nullable
    private Sort.Direction defaultSortDirection;

}
