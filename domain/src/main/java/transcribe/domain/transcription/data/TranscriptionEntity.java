package transcribe.domain.transcription.data;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderColumn;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import transcribe.core.core.validate.constraint.duration.PositiveOrZeroDuration;
import transcribe.core.transcribe.common.Language;
import transcribe.domain.application_user.data.ApplicationUserEntity;
import transcribe.domain.audit.data.BaseEntity;
import transcribe.domain.completion.data.CompletionEntity;
import transcribe.domain.transcription_word.data.TranscriptionWordEntity;

import java.net.URI;
import java.time.Duration;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "transcription")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class TranscriptionEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "status")
    @NotNull
    @Enumerated(EnumType.STRING)
    private TranscriptionStatus status;

    @Column(name = "source_uri")
    private URI sourceUri;

    @Column(name = "cloud_uri")
    private URI cloudUri;

    @Column(name = "language")
    @Enumerated(EnumType.STRING)
    @NotNull
    private Language language;

    @Column(name = "name")
    @NotBlank
    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "application_user_id")
    @NotNull
    private ApplicationUserEntity applicationUser;

    @Column(name = "enhanced")
    @NotNull
    private Boolean enhanced;

    @Column(name = "length")
    @PositiveOrZeroDuration
    @JdbcTypeCode(SqlTypes.INTERVAL_SECOND)
    private Duration length;

    @Column(name = "error")
    private String error;

    @OneToMany(
            mappedBy = "transcription",
            orphanRemoval = true,
            cascade = CascadeType.ALL,
            fetch = FetchType.LAZY
    )
    @OrderColumn(name = "sequence")
    @Builder.Default
    private List<TranscriptionWordEntity> words = new ArrayList<>();

    @OneToMany(mappedBy = "transcription", fetch = FetchType.LAZY)
    @Builder.Default
    private Set<CompletionEntity> completions = new HashSet<>();

    public void addWords(List<TranscriptionWordEntity> transcriptionWords) {
        words.addAll(transcriptionWords);
        words.forEach(w -> w.setTranscription(this));
    }

    /**
     * @param fromIndex inclusive
     * @param toIndex   exclusive
     */
    public void removeWords(int fromIndex, int toIndex) {
        var sublist = words.subList(fromIndex, toIndex);
        sublist.forEach(w -> w.setTranscription(null));
        sublist.clear();
    }

}
