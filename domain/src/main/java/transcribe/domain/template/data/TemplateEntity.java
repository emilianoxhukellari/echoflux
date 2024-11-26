package transcribe.domain.template.data;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import transcribe.domain.audit.data.AuditEntity;
import transcribe.core.core.annotation.BigText;

@Entity
@Table(name = "template")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class TemplateEntity extends AuditEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "name", unique = true)
    @NotBlank
    private String name;

    @Column(name = "content")
    @NotBlank
    @BigText
    private String content;

}
