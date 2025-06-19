package echoflux.domain.settings.data;

import com.fasterxml.jackson.databind.JsonNode;
import io.hypersistence.utils.hibernate.id.Tsid;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import echoflux.domain.core.data.BaseEntity;

@Entity
@Table(name = "settings")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false, of = "key")
public class SettingsEntity extends BaseEntity<Long> {

    @Id
    @Tsid
    @Column(name = "id")
    private Long id;

    @Column(name = "key", unique = true)
    @NotNull
    private String key;

    @Column(name = "name", unique = true)
    @NotBlank
    private String name;

    @Column(name = "value")
    @NotNull
    @JdbcTypeCode(SqlTypes.JSON)
    private JsonNode value;

}
