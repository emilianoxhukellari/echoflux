package com.example.application.core.common.utils;

import jakarta.annotation.Nullable;

import java.net.URI;

public final class UriUtils {

    public static @Nullable URI newUri(@Nullable String uri) {
        if (uri == null) {
            return null;
        }

        try {
            return URI.create(uri);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("The provided URI is invalid");
        } catch (Throwable e) {
            throw new IllegalArgumentException("An error occurred while creating the URI");
        }
    }

}
