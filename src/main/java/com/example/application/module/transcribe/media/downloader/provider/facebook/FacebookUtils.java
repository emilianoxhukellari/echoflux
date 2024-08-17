package com.example.application.module.transcribe.media.downloader.provider.facebook;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;

import java.net.URI;

public final class FacebookUtils {

    public static boolean isFacebookUri(URI uri) {
        Validate.notNull(uri, "URI is required to check if it is a Facebook URL");

        return StringUtils.contains(uri.toString(), "facebook.");
    }

}
