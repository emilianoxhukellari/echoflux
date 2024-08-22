package transcribe.core.transcribe.google;

import com.google.cloud.speech.v2.SpeechRecognitionAlternative;
import com.google.cloud.speech.v2.SpeechRecognitionResult;
import jakarta.annotation.Nullable;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.ListUtils;
import org.apache.commons.lang3.StringUtils;
import org.mapstruct.factory.Mappers;
import transcribe.core.common.mapper.CoreMapper;
import transcribe.core.transcribe.common.TranscribeResult;

import java.util.List;
import java.util.stream.Collectors;

public final class GoogleSpeechToTextUtils {

    private static final CoreMapper CORE_MAPPER = Mappers.getMapper(CoreMapper.class);

    public static TranscribeResult toTranscribeResult(@Nullable List<SpeechRecognitionResult> resultList) {

        return ListUtils.emptyIfNull(resultList)
                .stream()
                .map(SpeechRecognitionResult::getAlternativesList)
                .filter(CollectionUtils::isNotEmpty)
                .map(List::getFirst)
                .collect(Collectors.teeing(
                        Collectors.mapping(
                                SpeechRecognitionAlternative::getTranscript,
                                Collectors.joining(StringUtils.SPACE)
                        ),
                        Collectors.flatMapping(
                                alternative -> alternative.getWordsList().stream(),
                                Collectors.mapping(CORE_MAPPER::toWord, Collectors.toList())
                        ),
                        TranscribeResult::new
                ));
    }

}
