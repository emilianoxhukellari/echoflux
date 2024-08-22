package transcribe.core.common.mapper;

import com.google.cloud.speech.v2.WordInfo;
import com.google.protobuf.Duration;
import org.mapstruct.*;
import transcribe.core.transcribe.common.Word;

@Mapper(collectionMappingStrategy = CollectionMappingStrategy.ADDER_PREFERRED,
        nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS,
        unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface CoreMapper {

    @Mapping(target = "text", source = "word")
    @Mapping(target = "startOffset", qualifiedByName = "durationToSeconds")
    @Mapping(target = "endOffset", qualifiedByName = "durationToSeconds")
    Word toWord(WordInfo wordInfo);

    @Named("durationToSeconds")
    default long durationToSeconds(Duration duration) {
        return duration.getSeconds();
    }

}
