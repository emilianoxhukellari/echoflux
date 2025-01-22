package transcribe.domain.transcript.transcript_manager.impl;

import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import transcribe.core.settings.SettingsLoader;
import transcribe.domain.transcript.transcript_manager.SaveEnhancedParts;
import transcribe.domain.transcript.transcript_manager.SaveOriginalParts;
import transcribe.domain.transcript.transcript_manager.TranscriptManager;
import transcribe.domain.transcript.transcript_manager.TranscriptManagerSettings;
import transcribe.domain.transcript.transcript_part.data.TranscriptPartEntity;
import transcribe.domain.transcript.transcript_part.mapper.TranscriptPartMapper;
import transcribe.domain.transcript.transcript_part.part.PartModel;
import transcribe.domain.transcript.transcript_part.part.PartModelUtils;
import transcribe.domain.transcript.transcript_part.service.TranscriptPartService;
import transcribe.domain.transcript.transcript_part_text.data.TranscriptPartTextType;
import transcribe.domain.transcript.transcript_part_text.service.AddTranscriptPartTextCommand;
import transcribe.domain.transcript.transcript_part_text.service.TranscriptPartTextService;

import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class TranscriptManagerImpl implements TranscriptManager {

    private final TranscriptPartService transcriptPartService;
    private final TranscriptPartTextService transcriptPartTextService;
    private final SettingsLoader settingsLoader;
    private final TranscriptPartMapper transcriptPartMapper;

    @Override
    public List<String> getTranscriptPartitionsWithMetadata(@NotNull Long transcriptionId) {
        var partModels = getTranscriptPartModels(transcriptionId);

        var partitions = new ArrayList<String>();
        var currentPartModels = new ArrayList<PartModel>();

        for (var partModel : partModels) {
            currentPartModels.add(partModel);

            if (partModel.getEndOfPartition()) {
                partitions.add(PartModelUtils.toText(currentPartModels, true));
                currentPartModels.clear();
            }
        }

        return partitions;
    }

    @Override
    public String getTranscript(Long transcriptionId) {
        var partModels = getTranscriptPartModels(transcriptionId);

        return PartModelUtils.toText(partModels, false);
    }

    @Override
    public List<PartModel> getTranscriptPartModels(Long transcriptionId) {
        return transcriptPartService.getAllForTranscription(transcriptionId)
                .stream()
                .map(transcriptPartMapper::toModel)
                .toList();
    }

    @Transactional
    @Override
    public List<TranscriptPartEntity> saveOriginalParts(SaveOriginalParts command) {
        var settings = settingsLoader.load(TranscriptManagerSettings.class);

        var sequenceStart = 0;
        var parts = new ArrayList<PartModel>();
        for (var partition : command.getPartitions()) {
            var partitionParts = PartModelUtils.toParts(partition.getSpeechToTextWords(), sequenceStart, settings.getMaxWordsPerPart());
            parts.addAll(partitionParts);
            sequenceStart += partitionParts.size();
        }

        var partEntities = new ArrayList<TranscriptPartEntity>(parts.size());

        for (var part : parts) {
            var partEntity = transcriptPartService.create(
                    transcriptPartMapper.toCommand(part, command.getTranscriptionId())
            );

            transcriptPartTextService.add(
                    AddTranscriptPartTextCommand.builder()
                            .transcriptPartId(partEntity.getId())
                            .content(part.getText())
                            .type(TranscriptPartTextType.ORIGINAL)
                            .build()
            );

            partEntities.add(partEntity);
        }

        return partEntities;
    }

    @Transactional
    @Override
    public List<TranscriptPartEntity> saveEnhancedParts(SaveEnhancedParts command) {
        for (var part : command.getPartModels()) {
            var partEntity = transcriptPartService.getForTranscriptionAndSequence(
                    command.getTranscriptionId(),
                    part.getSequence()
            );
            transcriptPartTextService.add(
                    AddTranscriptPartTextCommand.builder()
                            .transcriptPartId(partEntity.getId())
                            .content(part.getText())
                            .type(TranscriptPartTextType.ENHANCED)
                            .build()
            );
        }

        return transcriptPartService.getAllForTranscription(command.getTranscriptionId());
    }

}
