package echoflux.domain.application_user.data;

import com.fasterxml.jackson.annotation.JsonIgnore;
import echoflux.core.core.country.Country;
import echoflux.domain.transcription.data.TranscriptionEntity_;
import io.hypersistence.utils.hibernate.id.Tsid;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import echoflux.domain.core.data.BaseEntity;
import echoflux.domain.transcription.data.TranscriptionEntity;
import lombok.Setter;

import java.time.ZoneId;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "application_user")
@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false, of = "id")
public class ApplicationUserEntity extends BaseEntity<Long> {

    @Id
    @Tsid
    @Column(name = "id")
    private Long id;

    @Column(name = "username")
    @NotBlank
    private String username;

    @Column(name = "name")
    @NotBlank
    private String name;

    @JsonIgnore
    @Column(name = "password")
    @NotBlank
    private String password;

    @Column(name = "enabled")
    @NotNull
    private Boolean enabled;

    @Column(name = "country")
    @NotNull
    @Enumerated(EnumType.STRING)
    private Country country;

    @Column(name = "zone_id")
    @NotNull
    private ZoneId zoneId;

    @CollectionTable(
            name = "application_user_role",
            joinColumns = @JoinColumn(name = "application_user_id")
    )
    @ElementCollection(fetch = FetchType.LAZY)
    @Enumerated(EnumType.STRING)
    @Column(name = "role")
    @NotNull
    private Set<Role> roles = new HashSet<>();

    @OneToMany(
            mappedBy = TranscriptionEntity_.APPLICATION_USER,
            fetch = FetchType.LAZY
    )
    @Builder.Default
    private Set<TranscriptionEntity> transcriptions = new HashSet<>();

}
