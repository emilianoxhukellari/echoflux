package com.example.application.core.transcribe.google;

import com.google.cloud.speech.v2.*;
import jakarta.annotation.Nullable;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.ListUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.List;

public final class GoogleSpeechToTextUtils {

    public static String toFullText(@Nullable List<SpeechRecognitionResult> resultList) {

        return ListUtils.emptyIfNull(resultList).stream()
                .map(SpeechRecognitionResult::getAlternativesList)
                .filter(CollectionUtils::isNotEmpty)
                .map(List::getFirst)
                .map(SpeechRecognitionAlternative::getTranscript)
                .reduce((a, b) -> StringUtils.join(a, StringUtils.SPACE, b))
                .orElse(StringUtils.EMPTY);
    }

}
