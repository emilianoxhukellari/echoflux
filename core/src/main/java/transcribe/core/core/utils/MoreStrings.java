package transcribe.core.core.utils;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

public final class MoreStrings {

    public static String[] split(String str, String separatorChars) {
        var nullable = StringUtils.splitPreserveAllTokens(str, separatorChars);

        return ArrayUtils.nullToEmpty(nullable);
    }

    public static String stripSpace(String str) {
        if (StringUtils.isBlank(str)) {
            return StringUtils.EMPTY;
        }

        return StringUtils.strip(str, StringUtils.SPACE);
    }

}
