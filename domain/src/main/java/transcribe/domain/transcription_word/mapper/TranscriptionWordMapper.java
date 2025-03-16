package transcribe.domain.transcription_word.mapper;

import org.mapstruct.CollectionMappingStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.NullValueCheckStrategy;
import org.mapstruct.ReportingPolicy;
import transcribe.core.word.common.Word;
import transcribe.domain.transcription_word.data.TranscriptionWordEntity;

import java.util.List;

@Mapper(collectionMappingStrategy = CollectionMappingStrategy.ADDER_PREFERRED,
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS,
        componentModel = MappingConstants.ComponentModel.SPRING)
public interface TranscriptionWordMapper {

    List<TranscriptionWordEntity> toEntities(List<Word> words);

    TranscriptionWordEntity toEntity(Word word);

    List<Word> toWords(List<TranscriptionWordEntity> entities);

}
