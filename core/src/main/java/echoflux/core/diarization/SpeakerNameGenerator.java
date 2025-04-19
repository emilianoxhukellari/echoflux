package echoflux.core.diarization;

import org.apache.commons.lang3.Validate;

import java.util.concurrent.ConcurrentHashMap;

public class SpeakerNameGenerator {

    private final ConcurrentHashMap<String, String> map = new ConcurrentHashMap<>();

    public String generate(String speakerId) {
        Validate.notBlank(speakerId, "speaker id required");

        return map.computeIfAbsent(speakerId, _ -> "Speaker %d".formatted(map.size() + 1));
    }

}
