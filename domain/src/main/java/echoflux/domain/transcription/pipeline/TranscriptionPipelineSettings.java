package echoflux.domain.transcription.pipeline;

import echoflux.core.storage.StorageProvider;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import echoflux.core.audio.common.AudioContainer;
import echoflux.core.core.concurrency.ConcurrencyLevel;
import echoflux.core.core.provider.AiProvider;
import echoflux.core.settings.Settings;

@Settings(key = "9714d1bb-4d3d-40dd-8a96-2e0eb1350d83")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TranscriptionPipelineSettings {

    @Builder.Default
    @NotBlank
    private String enhanceCompletionDynamicTemplateName = "enhance-transcription";

    @Builder.Default
    @NotBlank
    private String enhanceCompletionContentDataModelKey = "content";

    @Builder.Default
    @NotBlank
    private String enhanceCompletionLanguageDataModelKey = "language";

    @Builder.Default
    private AiProvider completionsAiProvider = AiProvider.OPENAI;

    @Builder.Default
    private StorageProvider storageProvider = StorageProvider.GOOGLE;

    @Builder.Default
    @NotNull
    @Valid
    private Partition partition = Partition.builder()
            .durationMinutes(20)
            .toleranceDurationMinutes(5)
            .minSilenceDurationSeconds(2)
            .splitConcurrency(ConcurrencyLevel.AVAILABLE_PROCESSORS)
            .transcribeConcurrency(6)
            .enhanceConcurrency(2)
            .enhanceWordLimit(4000)
            .build();

    @Builder.Default
    @NotNull
    @Valid
    private Transcode transcode = Transcode.builder()
            .container(AudioContainer.WEBM)
            .channels(1)
            .build();

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Transcode {

        @NotNull
        private AudioContainer container;

        @NotNull
        @Min(1)
        private Integer channels;

    }

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Partition {

        @NotNull
        @Min(0)
        private Integer durationMinutes;

        @NotNull
        @Min(0)
        private Integer toleranceDurationMinutes;

        @NotNull
        @Min(0)
        private Integer minSilenceDurationSeconds;

        @NotNull
        private Integer splitConcurrency;

        @NotNull
        private Integer transcribeConcurrency;

        @NotNull
        private Integer enhanceConcurrency;

        @NotNull
        @Min(1)
        private Integer enhanceWordLimit;

    }

}
