package echoflux.domain.transcription.data;

import echoflux.domain.application_user.data.ScalarApplicationUserProjection;

public interface TranscriptionProjection extends ScalarTranscriptionProjection {

    ScalarApplicationUserProjection getApplicationUser();

}
