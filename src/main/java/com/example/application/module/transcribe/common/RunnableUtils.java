package com.example.application.module.transcribe.common;

import java.util.function.Consumer;

public class RunnableUtils {

    public static <T> void consumeIfPresent(Consumer<T> consumer, T value) {
        if (value != null) {
            consumer.accept(value);
        }
    }

}
