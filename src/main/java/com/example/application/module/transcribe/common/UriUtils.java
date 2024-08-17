package com.example.application.module.transcribe.common;

import org.apache.commons.lang3.Validate;

import java.net.URI;

public final class UriUtils {

    public static URI newUri(String uri) {
        Validate.notBlank(uri, "URI is required");

        try {
            return URI.create(uri);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("The provided URI is invalid");
        } catch (Throwable e) {
            throw new IllegalArgumentException("An error occurred while creating the URI");
        }
    }

}
