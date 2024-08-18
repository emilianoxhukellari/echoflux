package com.example.application.core.common.utils;

import jakarta.annotation.Nullable;
import org.apache.commons.collections4.CollectionUtils;

import java.util.List;

public final class MoreLists {

    @Nullable
    public static <T> T getLast(@Nullable List<T> list) {
        if (CollectionUtils.isEmpty(list)) {
            return null;
        }

        return list.getLast();
    }

    @Nullable
    public static <T> T getFirst(@Nullable List<T> list) {
        if (CollectionUtils.isEmpty(list)) {
            return null;
        }

        return list.getFirst();
    }

}
