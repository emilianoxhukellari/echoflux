package echoflux.domain.template.data;

import io.hypersistence.utils.hibernate.id.Tsid;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import echoflux.domain.core.data.BaseEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "template")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false, of = "name")
public class TemplateEntity extends BaseEntity<Long> {

    @Id
    @Tsid
    @Column(name = "id")
    private Long id;

    @Column(name = "name", unique = true)
    @NotBlank
    private String name;

    @Column(name = "content")
    @NotBlank
    private String content;

}
