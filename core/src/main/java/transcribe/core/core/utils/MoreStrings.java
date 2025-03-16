package transcribe.core.core.utils;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;

public final class MoreStrings {

    public static String EMPTY_LINE = StringUtils.LF + StringUtils.LF;

    public static String[] split(String str, String separatorChars) {
        var nullable = StringUtils.splitPreserveAllTokens(str, separatorChars);

        return ArrayUtils.nullToEmpty(nullable);
    }

    public static List<String> split(String str) {
        if (StringUtils.isBlank(str)) {
            return List.of();
        }
        var split = StringUtils.split(str);

        return List.of(split);
    }

    public static int countChars(List<String> strList) {
        if (CollectionUtils.isEmpty(strList)) {
            return 0;
        }

        return strList.stream()
                .filter(Predicate.not(Objects::isNull))
                .mapToInt(String::length)
                .sum();
    }

}
