package echoflux.domain.completion.data;

import echoflux.domain.transcription.data.TranscriptionProjection;

public interface CompletionProjection extends ScalarCompletionProjection {

    TranscriptionProjection getTranscription();

}
