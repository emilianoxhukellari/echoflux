package transcribe.core.transcribe.common;

import com.google.cloud.speech.v2.WordInfo;
import com.google.protobuf.Duration;
import org.mapstruct.*;

@Mapper(collectionMappingStrategy = CollectionMappingStrategy.ADDER_PREFERRED,
        unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface SpeechToTextMapper {

    @Mapping(target = "text", source = "word")
    @Mapping(target = "startOffset", qualifiedByName = "durationToSeconds")
    @Mapping(target = "endOffset", qualifiedByName = "durationToSeconds")
    Word toWord(WordInfo wordInfo);

    @Named("durationToSeconds")
    default long durationToSeconds(Duration duration) {
        return duration.getSeconds();
    }

}
