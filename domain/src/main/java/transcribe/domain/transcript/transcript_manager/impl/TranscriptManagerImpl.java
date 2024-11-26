package transcribe.domain.transcript.transcript_manager.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import transcribe.core.settings.SettingsLoader;
import transcribe.domain.transcript.transcript_manager.SaveAsPartsCommand;
import transcribe.domain.transcript.transcript_manager.TranscriptManager;
import transcribe.domain.transcript.transcript_manager.TranscriptManagerSettings;
import transcribe.domain.transcript.transcript_part.data.TranscriptPartEntity;
import transcribe.domain.transcript.transcript_part.part.Part;
import transcribe.domain.transcript.transcript_part.part.PartUtils;
import transcribe.domain.transcript.transcript_part.service.CreateTranscriptPartCommand;
import transcribe.domain.transcript.transcript_part.service.TranscriptPartService;
import transcribe.domain.transcript.transcript_part_text.data.TranscriptPartTextType;
import transcribe.domain.transcript.transcript_part_text.service.AddTranscriptPartTextCommand;
import transcribe.domain.transcript.transcript_part_text.service.TranscriptPartTextService;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Component
@RequiredArgsConstructor
public class TranscriptManagerImpl implements TranscriptManager {

    private final TranscriptPartService transcriptPartService;
    private final TranscriptPartTextService transcriptPartTextService;
    private final SettingsLoader settingsLoader;

    @Transactional(readOnly = true)
    @Override
    public String getTranscriptWithTimestamps(Long transcriptionId) {
        var parts = transcriptPartService.getAllForTranscription(transcriptionId)
                .stream()
                .map(e -> Part.builder()
                        .text(
                                Objects.requireNonNull(e.getLatestTextEntity(), "No last text for this part")
                                        .getContent()
                        )
                        .startOffsetMillis(e.getStartOffsetMillis())
                        .endOffsetMillis(e.getEndOffsetMillis())
                        .build()
                )
                .toList();

        return PartUtils.toTextWithTimestamps(parts);
    }

    @Transactional
    @Override
    public List<TranscriptPartEntity> saveAsParts(SaveAsPartsCommand command) {
        var settings = settingsLoader.load(TranscriptManagerSettings.class);
        var parts = PartUtils.toParts(command.getWords(), settings.getMaxWordsPerPart());

        var partEntities = new ArrayList<TranscriptPartEntity>(parts.size());

        for (int i = 0; i < parts.size(); i++) {
            var partEntity = transcriptPartService.create(
                    CreateTranscriptPartCommand.builder()
                            .transcriptionId(command.getTranscriptionId())
                            .sequence(i)
                            .startOffsetMillis(parts.get(i).getStartOffsetMillis())
                            .endOffsetMillis(parts.get(i).getEndOffsetMillis())
                            .build()
            );

            transcriptPartTextService.add(
                    AddTranscriptPartTextCommand.builder()
                            .transcriptPartId(partEntity.getId())
                            .content(parts.get(i).getText())
                            .type(TranscriptPartTextType.ORIGINAL)
                            .build()
            );

            partEntities.add(partEntity);
        }

        return partEntities;
    }

}
