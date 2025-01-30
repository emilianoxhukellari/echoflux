package transcribe.domain.transcription.manager.impl;

import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import transcribe.core.settings.SettingsLoader;
import transcribe.domain.transcription.manager.SaveOriginalWordsCommand;
import transcribe.domain.transcription.manager.TranscriptionManager;
import transcribe.domain.transcription.manager.TranscriptionManagerSettings;
import transcribe.domain.transcription_speaker.data.TranscriptionSpeakerEntity;
import transcribe.domain.transcription_speaker.service.CreateTranscriptionSpeakerCommand;
import transcribe.domain.transcription_speaker.service.TranscriptionSpeakerService;
import transcribe.domain.transcription_word.data.TranscriptionWordEntity;
import transcribe.domain.transcription_word.service.CreateTranscriptionWordCommand;
import transcribe.domain.transcription_word.service.TranscriptionWordService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Component
@RequiredArgsConstructor
public class TranscriptionManagerImpl implements TranscriptionManager {

    private final TranscriptionWordService transcriptionWordService;
    private final TranscriptionSpeakerService transcriptionSpeakerService;
    private final SettingsLoader settingsLoader;

    @Transactional
    public List<TranscriptionWordEntity> saveOriginalWords(SaveOriginalWordsCommand command) {
        var settings = settingsLoader.load(TranscriptionManagerSettings.class);

        var emptySpeakerName = settings.getEmptySpeakerName();
        var speakerNameToSpeakerEntity = new HashMap<String, TranscriptionSpeakerEntity>();
        var createWordCommands = new ArrayList<CreateTranscriptionWordCommand>(command.getWords().size());
        var speechToTextWords = command.getWords();

        for (int i = 0; i < speechToTextWords.size(); i++) {
            var word = speechToTextWords.get(i);

            var speakerEntity = ensureSpeaker(
                    word.getSpeakerName(),
                    emptySpeakerName,
                    command.getTranscriptionId(),
                    speakerNameToSpeakerEntity
            );

            var createCommand = CreateTranscriptionWordCommand.builder()
                    .transcriptionId(command.getTranscriptionId())
                    .transcriptionSpeaker(speakerEntity)
                    .sequence(i)
                    .startOffsetMillis(word.getStartOffsetMillis())
                    .endOffsetMillis(word.getEndOffsetMillis())
                    .content(word.getContent())
                    .build();

            createWordCommands.add(createCommand);
        }

        return transcriptionWordService.createAll(createWordCommands);
    }

    private TranscriptionSpeakerEntity ensureSpeaker(String speakerName,
                                                     String emptySpeakerName,
                                                     Long transcriptionId,
                                                     HashMap<String, TranscriptionSpeakerEntity> cache) {
        var name = StringUtils.isBlank(speakerName)
                ? emptySpeakerName
                : speakerName;

        return ensureSpeaker(name, transcriptionId, cache);
    }

    private TranscriptionSpeakerEntity ensureSpeaker(String speakerName,
                                                     Long transcriptionId,
                                                     HashMap<String, TranscriptionSpeakerEntity> cache) {
        if (cache.containsKey(speakerName)) {
            return cache.get(speakerName);
        }

        var speaker = transcriptionSpeakerService.create(
                CreateTranscriptionSpeakerCommand.builder()
                        .name(speakerName)
                        .transcriptionId(transcriptionId)
                        .build()
        );

        cache.put(speakerName, speaker);

        return speaker;
    }

}