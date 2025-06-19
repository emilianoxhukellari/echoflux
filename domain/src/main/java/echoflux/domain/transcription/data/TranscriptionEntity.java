package echoflux.domain.transcription.data;

import echoflux.core.storage.StorageProvider;
import echoflux.domain.completion.data.CompletionEntity_;
import echoflux.domain.transcription_word.data.TranscriptionWordEntity_;
import io.hypersistence.utils.hibernate.id.Tsid;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
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
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import echoflux.core.core.validate.constraint.duration.PositiveOrZeroDuration;
import echoflux.core.transcribe.Language;
import echoflux.domain.application_user.data.ApplicationUserEntity;
import echoflux.domain.core.data.BaseEntity;
import echoflux.domain.completion.data.CompletionEntity;
import echoflux.domain.transcription_word.data.TranscriptionWordEntity;

import java.net.URI;
import java.time.Duration;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "transcription")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false, of = "id")
public class TranscriptionEntity extends BaseEntity<Long> {

    @Id
    @Tsid
    @Column(name = "id")
    private Long id;

    @Column(name = "status")
    @NotNull
    @Enumerated(EnumType.STRING)
    private TranscriptionStatus status;

    @Column(name = "source_uri")
    private URI sourceUri;

    @Column(name = "uri")
    private URI uri;

    @Column(name = "storage_provider")
    @Enumerated(EnumType.STRING)
    private StorageProvider storageProvider;

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

    @Column(name = "length")
    @PositiveOrZeroDuration
    @JdbcTypeCode(SqlTypes.INTERVAL_SECOND)
    private Duration length;

    @Column(name = "error")
    private String error;

    @OneToMany(
            mappedBy = TranscriptionWordEntity_.TRANSCRIPTION,
            orphanRemoval = true,
            cascade = CascadeType.ALL,
            fetch = FetchType.LAZY
    )
    @OrderColumn(name = TranscriptionWordEntity_.SEQUENCE)
    @Builder.Default
    private List<TranscriptionWordEntity> words = new ArrayList<>();

    @OneToMany(
            mappedBy = CompletionEntity_.TRANSCRIPTION,
            fetch = FetchType.LAZY
    )
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
