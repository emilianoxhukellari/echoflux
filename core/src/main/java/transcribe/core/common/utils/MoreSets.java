package transcribe.core.common.utils;

import jakarta.annotation.Nullable;
import org.apache.commons.collections4.SetUtils;

import java.util.Set;

public class MoreSets {

    /**
     * @return true if the set contains the given element. If the set is null, it is treated as empty
     */
    public static <T> boolean contains(@Nullable Set<T> list, T element) {
        return SetUtils.emptyIfNull(list).contains(element);
    }

}
