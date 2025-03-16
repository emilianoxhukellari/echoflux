package transcribe.annotation.projection;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum AttributeProjectType {

    /**
     * The attribute is included in the attribute paths when fetching.
     * */
    DEFAULT(1),

    /**
     * The attribute is excluded from the attribute paths when fetching.
     * */
    DEFERRED(0);

    private final int order;

}
