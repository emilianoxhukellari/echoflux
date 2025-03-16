package transcribe.domain.transcription_word.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import transcribe.domain.transcription_word.data.TranscriptionWordRepository;
import transcribe.domain.transcription_word.data.WordDto;
import transcribe.domain.transcription_word.service.TranscriptionWordService;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class TranscriptionWordServiceImpl implements TranscriptionWordService {

    private final TranscriptionWordRepository transcriptionWordRepository;

    @Override
    public List<WordDto> findAllByTranscriptionId(Long transcriptionId) {
        return transcriptionWordRepository.findAllByTranscriptionId(transcriptionId);
    }

}
